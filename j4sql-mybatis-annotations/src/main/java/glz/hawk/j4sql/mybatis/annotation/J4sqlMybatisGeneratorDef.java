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

package glz.hawk.j4sql.mybatis.annotation;

import glz.hawk.j4sql.mybatis.generate.DefaultJ4sqlMybatisGeneratorCustomizerProvider;
import glz.hawk.j4sql.mybatis.generate.J4sqlMybatisClassGeneratorForAnnotationProcessor;
import glz.hawk.j4sql.mybatis.generate.J4sqlMybatisGeneratorCustomizer;
import glz.hawk.jdesigner.builder.context.ClasspathBuilderContext;
import glz.hawk.jdesigner.builder.resource.ResourcePathCollector;
import glz.hawk.jdesigner.generator.DefaultBuilderContextSupplier;
import glz.hawk.jdesigner.generator.annotation.PackageMaps;

import java.lang.annotation.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The class annotated with this annotation will be picked by the annotation processor to generated corresponding java code.
 * <p>Only one class could be annotated by this annotation.</p>
 *
 * @author Hawk
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface J4sqlMybatisGeneratorDef {

    /**
     * This attribute provides the packages to scan.
     */
    String[] scanPackages();

    /**
     * Only the models in these packages will be used to generate table related class.
     * <p>If this attribute's value is empty, all the models will be used to generate table related class</p>
     */
    String[] includePackages() default {};

    /**
     * Any models in these packages will not be used to generate table related class.
     */
    String[] excludePackages() default {};

    /**
     * This attribute provides all the resource list, as annotation processor doesn't support package scan.
     * <p>The resource matching scanPackages is valid.</p>
     * <p>The value must be the full name of implementation of interface {@link ResourcePathCollector}</p>
     */
    String[] resourceCollectors();

    PackageMaps poClassPackageMaps() default @PackageMaps();

    PackageMaps updateClassPackageMaps() default @PackageMaps();

    PackageMaps supportClassPackageMaps() default @PackageMaps();

    PackageMaps mapperClassPackageMaps() default @PackageMaps();

    PackageMaps sqlProviderClassPackageMaps() default @PackageMaps();

    PackageMaps repositoryClassPackageMaps() default @PackageMaps();

    PackageMaps abstractRepositoryClassPackageMaps() default @PackageMaps();

    PackageMaps repositoryImplClassPackageMaps() default @PackageMaps();

    /**
     * The generated ddl files will be found in this package.
     */
    String ddlPackageName() default "";

    /**
     * This attribute's name must match the value of {@link #customizerProviderClass()}
     */
    String dialectName() default "mysql";

    /**
     * This attribute is responsible for customizing the {@link J4sqlMybatisClassGeneratorForAnnotationProcessor}
     * <p>{@link DefaultJ4sqlMybatisGeneratorCustomizerProvider} now only supports dialect name: {@code 'mysql'} and ‘{@code oracle'}</p>
     */
    Class<? extends Function<String, J4sqlMybatisGeneratorCustomizer>> customizerProviderClass() default DefaultJ4sqlMybatisGeneratorCustomizerProvider.class;

    /**
     * Whether to support Insert or Update Column.
     * <p>If the value is {@code true}, the generator will generate ColumnUpdate Class and extra method in the repository</p>
     */
    boolean supportColumnInsertOrUpdate() default false;

    /**
     * Gets the qualified name of {@code Supplier<ClasspathBuilderContext>}
     * <p>This class is responsible for providing {@code ClasspathBuilderContext}</p>
     */
    Class<? extends Supplier<? extends ClasspathBuilderContext>> builderContextSupplierClass() default DefaultBuilderContextSupplier.class;

    /**
     * Gets the package of TableEnum Class
     */
    String tableEnumClassPackage() default "table.enums";

    /**
     * Gets the name of TableEnum Class
     */
    String tableEnumClassName() default "TableEnum";
}
