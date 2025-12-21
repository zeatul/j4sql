/*
 * Copyright 2025-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package glz.hawk.j4sql.mybatis.processor;

import glz.hawk.jdesigner.spec.base.Model;
import glz.hawk.jdesigner.builder.ResourcePathIndexesWarehouseBuilder;
import glz.hawk.jdesigner.builder.SimpleModelWarehouseProducer;
import glz.hawk.jdesigner.builder.context.ClasspathBuilderContextImpl;
import glz.hawk.jdesigner.builder.context.PackageGenerator;
import glz.hawk.jdesigner.builder.context.PackageGeneratorImpl;
import glz.hawk.jdesigner.builder.resource.ResourcePathCollector;
import glz.hawk.jdesigner.spec.manager.ModelWarehouse;
import glz.hawk.jdesigner.translator.Translator;
import glz.hawkframework.dao.function.ColumnFunction;
import glz.hawk.j4sql.mybatis.annotation.J4sqlMybatisGenerator;
import glz.hawk.j4sql.mybatis.annotation.PackageMaps;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.Supplier;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
@SupportedAnnotationTypes("glz.hawk.j4sql.mybatis.annotation.J4sqlMybatisGenerator")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class J4sqlMybatisGeneratorProcessor extends AbstractProcessor {
    private Types typeUtils;
    private Messager messager;
    private Filer filer;
    private Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
        elementUtils = processingEnv.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(J4sqlMybatisGenerator.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        messager.printMessage(Diagnostic.Kind.NOTE, "DynamicMybatisGeneratorAnnotationProcessor is running.");

        if (annotations == null || annotations.isEmpty()) {
            return true;
        }

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(J4sqlMybatisGenerator.class);
        if (elements.isEmpty()) {
            throw new IllegalStateException("Found no class annotated with " + J4sqlMybatisGenerator.class);
        } else if (elements.size() > 1) {
            throw new IllegalStateException("Only one class can be annotated with " + J4sqlMybatisGenerator.class);
        }

        J4sqlMybatisGenerator dynamicMybatisGenerator = elements.iterator().next().getAnnotation(J4sqlMybatisGenerator.class);
        String[] scanPackages = dynamicMybatisGenerator.scanPackages();
        Arrays.stream(scanPackages).forEach(s -> messager.printMessage(Diagnostic.Kind.NOTE, "scanPackage: " + s));

        ModelWarehouse modelWarehouse = buildModelWarehouse(scanPackages, dynamicMybatisGenerator.resourceCollectors());

        modelWarehouse.printAll();

        J4sqlMybatisClassGenerator generator = new J4sqlMybatisClassGenerator(modelWarehouse,
            instance(getColumnFunctionMapSupplierClass(dynamicMybatisGenerator::columnFunctionMapSupplierClass)),
            filer,
            new ArrayList<>(elements),
            instance(getTranslatorClass(dynamicMybatisGenerator::fieldNameTranslatorClass)),
            instance(getTranslatorClass(dynamicMybatisGenerator::getterNameTranslatorClass)),
            instance(getTranslatorClass(dynamicMybatisGenerator::setterNameTranslatorClass)),
            instance(getTranslatorClass(dynamicMybatisGenerator::columnToTypeNameTranslatorClass)),
            buildPackageGenerator(dynamicMybatisGenerator.poClassPackageMaps()),
            instance(getTranslatorClass(dynamicMybatisGenerator::poClassNameTranslatorClass)),
            instance(getTranslatorClass(dynamicMybatisGenerator::getterUpdatedNameTranslatorClass)),
            instance(getTranslatorClass(dynamicMybatisGenerator::fieldUpdatedNameTranslatorClass)),
            buildPackageGenerator(dynamicMybatisGenerator.updateClassPackageMaps()),
            instance(getTranslatorClass(dynamicMybatisGenerator::updateClassNameTranslatorClass)),
            instance(getTranslatorClass(dynamicMybatisGenerator::columnUpdateClassNameTranslatorClass)),
            buildPackageGenerator(dynamicMybatisGenerator.supportClassPackageMaps()),
            instance(getTranslatorClass(dynamicMybatisGenerator::supportClassNameTranslatorClass)),
            buildPackageGenerator(dynamicMybatisGenerator.mapperClassPackageMaps()),
            instance(getTranslatorClass(dynamicMybatisGenerator::mapperClassNameTranslatorClass)),
            buildPackageGenerator(dynamicMybatisGenerator.sqlProviderClassPackageMaps()),
            instance(getTranslatorClass(dynamicMybatisGenerator::sqlProviderClassNameTranslatorClass)),
            instance(getTranslatorClass(dynamicMybatisGenerator::columnParamNameTranslatorClass)),
            buildPackageGenerator(dynamicMybatisGenerator.repositoryClassPackageMaps()),
            instance(getTranslatorClass(dynamicMybatisGenerator::repositoryClassNameTranslatorClass)),
            buildPackageGenerator(dynamicMybatisGenerator.repositoryImplClassPackageMaps()),
            instance(getTranslatorClass(dynamicMybatisGenerator::repositoryImplClassNameTranslatorClass)),
            dynamicMybatisGenerator.ddlPackageName(),
            dynamicMybatisGenerator.dialectNames()
        );

        generator.generatePo();
        generator.generateUpdate();
        generator.generateSupport();
        generator.generateMapper();
        generator.generateSqlProvider();
        generator.generateRepository();
        generator.generateRepositoryImpl();
        generator.generateDDL();

        return true;
    }

    @SuppressWarnings("unchecked")
    protected <S, R> Class<? extends Translator<S, R>> getTranslatorClass(Supplier<Class<? extends Translator<S, R>>> supplier) {
        try {
            return supplier.get();
        } catch (MirroredTypeException ex) {
            String qualifiedName = ((TypeElement) ((DeclaredType) ex.getTypeMirror()).asElement()).getQualifiedName().toString();
            try {
                return (Class<? extends Translator<S, R>>) Class.forName(qualifiedName);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected Class<? extends Supplier<Map<Class<? extends Annotation>, Class<? extends ColumnFunction>>>> getColumnFunctionMapSupplierClass(Supplier<Class<? extends Supplier<Map<Class<? extends Annotation>, Class<? extends ColumnFunction>>>>> supplier) {
        try {
            return supplier.get();
        } catch (MirroredTypeException ex) {
            String qualifiedName = ((TypeElement) ((DeclaredType) ex.getTypeMirror()).asElement()).getQualifiedName().toString();
            try {
                return (Class<? extends Supplier<Map<Class<? extends Annotation>, Class<? extends ColumnFunction>>>>) Class.forName(qualifiedName);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected Class<?> getNormalClass(Supplier<Class<?>> supplier) {
        try {
            return supplier.get();
        } catch (MirroredTypeException ex) {
            String qualifiedName = ((TypeElement) ((DeclaredType) ex.getTypeMirror()).asElement()).getQualifiedName().toString();
            try {
                return Class.forName(qualifiedName);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected <T> T instance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    protected PackageGenerator buildPackageGenerator(PackageMaps packageMaps) {
        return PackageGeneratorImpl.builder()
            .add(packageMaps.fullNamePackageMaps(), (b, ms) -> Arrays.stream(ms).forEach(m -> b.add(m.namespace(), m.modelName(), m.packageName())))
            .add(packageMaps.namespacePackageMaps(), (b, ms) -> Arrays.stream(ms).forEach(m -> b.add(m.namespace(), m.packageName())))
            .add(packageMaps.namespaceModelPackageMaps(), (b, ms) -> Arrays.stream(ms).forEach(m -> b.add(m.namespace(), (Class<? extends Model>) getNormalClass(m::modelClass), m.packageName())))
            .build();
    }


    protected ModelWarehouse buildModelWarehouse(String[] scanPackages, String[] resourceCollectors) {
        ClasspathBuilderContextImpl builderContext = new ClasspathBuilderContextImpl();
        SimpleModelWarehouseProducer modelWarehouseProducer = new SimpleModelWarehouseProducer();
        ResourcePathIndexesWarehouseBuilder builder = new ResourcePathIndexesWarehouseBuilder(getClass().getClassLoader(), builderContext, modelWarehouseProducer,
            Arrays.stream(resourceCollectors).map(c -> {
                messager.printMessage(Diagnostic.Kind.NOTE, "resourceCollector: " + c);
                try {
                    return (ResourcePathCollector) Class.forName(c).newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).toArray(ResourcePathCollector[]::new)
            , scanPackages);
        return builder.build();
    }

}
