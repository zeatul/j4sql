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

import glz.hawk.j4sql.mybatis.annotation.J4sqlMybatisGeneratorDef;
import glz.hawk.j4sql.mybatis.generate.J4sqlMybatisClassGeneratorForAnnotationProcessor;
import glz.hawk.j4sql.mybatis.generate.J4sqlMybatisGeneratorCustomizer;
import glz.hawk.jdesigner.builder.context.ClasspathBuilderContext;
import glz.hawk.jdesigner.builder.context.PackageGenerator;
import glz.hawk.jdesigner.builder.context.PackageGeneratorImpl;
import glz.hawk.jdesigner.generator.annotation.PackageMaps;
import glz.hawk.jdesigner.spec.manager.ModelWarehouse;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import static glz.hawk.jdesigner.generator.GeneratorUtils.*;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
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
        return Collections.singleton(J4sqlMybatisGeneratorDef.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        messager.printMessage(Diagnostic.Kind.NOTE, "J4sqlMybatisGeneratorProcessor is running.");

        if (annotations == null || annotations.isEmpty()) {
            return true;
        }

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(J4sqlMybatisGeneratorDef.class);
        if (elements.isEmpty()) {
            throw new IllegalStateException("Found no class annotated with " + J4sqlMybatisGeneratorDef.class);
        } else if (elements.size() > 1) {
            throw new IllegalStateException("Only one class can be annotated with " + J4sqlMybatisGeneratorDef.class);
        }

        J4sqlMybatisGeneratorDef def = elements.iterator().next().getAnnotation(J4sqlMybatisGeneratorDef.class);
        assert def != null;
        String[] scanPackages = def.scanPackages();
        String[] includePackages = def.includePackages();
        String[] excludePackages = def.excludePackages();
        Arrays.stream(scanPackages).forEach(s -> messager.printMessage(Diagnostic.Kind.NOTE, "scanPackage: " + s));
        boolean supportColumnInsertOrUpdate = def.supportColumnInsertOrUpdate();

        Supplier<? extends ClasspathBuilderContext> builderContextSupplier = instance(getSupplierClass(def::builderContextSupplierClass));
        ModelWarehouse modelWarehouse = buildModelWarehouse(scanPackages, def.resourceCollectors(), messager, builderContextSupplier);

        Function<String, J4sqlMybatisGeneratorCustomizer> j4sqlMybatisGeneratorCustomizerProvider = instance(getCustomizerProviderClass(def::customizerProviderClass));

        J4sqlMybatisClassGeneratorForAnnotationProcessor generator = new J4sqlMybatisClassGeneratorForAnnotationProcessor(modelWarehouse, includePackages, excludePackages, filer, new ArrayList<>(elements), buildPackageGenerator(def.poClassPackageMaps()), buildPackageGenerator(def.updateClassPackageMaps()), buildPackageGenerator(def.supportClassPackageMaps()), buildPackageGenerator(def.mapperClassPackageMaps()), buildPackageGenerator(def.sqlProviderClassPackageMaps()), buildPackageGenerator(def.repositoryClassPackageMaps()),buildPackageGenerator(def.abstractRepositoryClassPackageMaps()), buildPackageGenerator(def.repositoryImplClassPackageMaps()), def.ddlPackageName(), def.dialectName(), j4sqlMybatisGeneratorCustomizerProvider.apply(def.dialectName()), supportColumnInsertOrUpdate, def.tableEnumClassPackage(), def.tableEnumClassName());

        generator.generatePo();
        generator.generateUpdate();
        generator.generateSupport();
        generator.generateTableEnum();
        generator.generateMapper();
        generator.generateSqlProvider();
        generator.generateRepository();
        generator.generateAbstractRepository();
        generator.generateRepositoryImpl();
        generator.generateDDL();
        return true;
    }


    @SuppressWarnings("unchecked")
    public Class<? extends Function<String, J4sqlMybatisGeneratorCustomizer>> getCustomizerProviderClass(Supplier<Class<? extends Function<String, J4sqlMybatisGeneratorCustomizer>>> supplier) {
        try {
            return supplier.get();
        } catch (MirroredTypeException ex) {
            String qualifiedName = ((TypeElement) ((DeclaredType) ex.getTypeMirror()).asElement()).getQualifiedName().toString();
            try {
                return (Class<Function<String, J4sqlMybatisGeneratorCustomizer>>) Class.forName(qualifiedName);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected PackageGenerator buildPackageGenerator(PackageMaps packageMaps) {
        return PackageGeneratorImpl.builder().add(packageMaps.qualifiedNamePackageMaps(), (b, ms) -> Arrays.stream(ms).forEach(m -> b.add(m.namespace(), m.modelName(), m.packageName()))).add(packageMaps.namespacePackageMaps(), (b, ms) -> Arrays.stream(ms).forEach(m -> b.add(m.namespace(), m.packageName()))).add(packageMaps.namespaceModelPackageMaps(), (b, ms) -> Arrays.stream(ms).forEach(m -> b.add(m.namespace(), getNormalSupplierClass(m::modelClass), m.packageName()))).build();
    }
}
