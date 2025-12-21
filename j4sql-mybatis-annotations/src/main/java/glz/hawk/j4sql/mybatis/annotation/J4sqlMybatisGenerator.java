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

import glz.hawk.jdesigner.builder.resource.ResourcePathCollector;
import glz.hawk.jdesigner.spec.database.Column;
import glz.hawk.jdesigner.spec.database.Table;
import glz.hawk.jdesigner.translator.Translator;
import glz.hawk.jdesigner.translator.meta.ColumnToTypeName;
import glz.hawkframework.dao.function.ColumnFunction;
import glz.hawk.codepoet.java.type.TypeName;

import java.lang.annotation.*;
import java.util.Map;
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

public @interface J4sqlMybatisGenerator {

    /**
     * This attribute provides the packages to scan.
     */
    String[] scanPackages();

    /**
     * This attribute provides all the resource list, as annotation processor doesn't support package scan.
     * <p>The resource matching scanPackages is valid.</p>
     * <p>The value must be the full name of implementation of interface {@link ResourcePathCollector}</p>
     */
    String[] resourceCollectors();

    Class<? extends Supplier<Map<Class<? extends Annotation>, Class<? extends ColumnFunction>>>> columnFunctionMapSupplierClass() default DefaultColumnFunctionMapSupplier.class;

    Class<? extends Translator<Column, String>> fieldNameTranslatorClass() default DefaultFieldNameTranslator.class;

    Class<? extends Translator<Column, String>> getterNameTranslatorClass() default DefaultGetterNameTranslator.class;

    Class<? extends Translator<Column, String>> setterNameTranslatorClass() default DefaultSetterNameTranslator.class;

    Class<? extends Translator<Column, TypeName>> columnToTypeNameTranslatorClass() default ColumnToTypeName.class;

    PackageMaps poClassPackageMaps() default @PackageMaps();

    Class<? extends Translator<Table, String>> poClassNameTranslatorClass() default DefaultPoClassNameTranslator.class;


    Class<? extends Translator<Column, String>> getterUpdatedNameTranslatorClass() default DefaultGetterUpdatedNameTranslator.class;

    Class<? extends Translator<Column, String>> fieldUpdatedNameTranslatorClass() default DefaultFieldUpdatedNameTranslator.class;

    PackageMaps updateClassPackageMaps() default @PackageMaps();

    Class<? extends Translator<Table, String>> updateClassNameTranslatorClass() default DefaultUpdateClassNameTranslator.class;

    Class<? extends Translator<Table, String>> columnUpdateClassNameTranslatorClass() default DefaultColumnUpdateClassNameTranslator.class;

    PackageMaps supportClassPackageMaps() default @PackageMaps();

    Class<? extends Translator<Table, String>> supportClassNameTranslatorClass() default DefaultSupportClassNameTranslator.class;

    PackageMaps mapperClassPackageMaps() default @PackageMaps();

    Class<? extends Translator<Table, String>> mapperClassNameTranslatorClass() default DefaultMapperClassNameTranslator.class;

    PackageMaps sqlProviderClassPackageMaps() default @PackageMaps();

    Class<? extends Translator<Table, String>> sqlProviderClassNameTranslatorClass() default DefaultSqlProviderClassNameTranslator.class;

    Class<? extends Translator<Column, String>> columnParamNameTranslatorClass() default DefaultColumnParamNameTranslator.class;

    PackageMaps repositoryClassPackageMaps() default @PackageMaps();

    Class<? extends Translator<Table, String>> repositoryClassNameTranslatorClass() default DefaultRepositoryClassNameTranslator.class;

    PackageMaps repositoryImplClassPackageMaps() default @PackageMaps();

    Class<? extends Translator<Table, String>> repositoryImplClassNameTranslatorClass() default DefaultRepositoryImplClassNameTranslator.class;

    String ddlPackageName() default "";

    /**
     * Now only supports dialect name: {@code 'mysql'} and ‘{@code oracle'}
     * <p>If the value of this attribute is empty, no ddl generated.</p>
     */
    String[] dialectNames() default {};
}
