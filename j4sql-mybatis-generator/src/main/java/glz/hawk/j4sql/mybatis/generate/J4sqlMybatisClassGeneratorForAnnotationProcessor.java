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

package glz.hawk.j4sql.mybatis.generate;

import glz.hawk.codepoet.ddl.SqlFile;
import glz.hawk.codepoet.java.JavaFile;
import glz.hawk.j4sql.mybatis.translator.TableToSupportFactory;
import glz.hawk.jdesigner.builder.context.PackageGenerator;
import glz.hawk.jdesigner.spec.manager.ModelWarehouse;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Zhang Peng
 */
public class J4sqlMybatisClassGeneratorForAnnotationProcessor extends AbstractJ4sqlMybatisClassGenerator {
    private final Filer filer;
    private final List<Element> originatingElements;

    public J4sqlMybatisClassGeneratorForAnnotationProcessor(ModelWarehouse modelWarehouse, String[] includePackages, String[] excludePackages, Filer filer, List<Element> originatingElements, PackageGenerator poClassPackageGenerator, PackageGenerator updateClassPackageGenerator, PackageGenerator supportClassPackageGenerator, PackageGenerator mapperClassPackageGenerator, PackageGenerator sqlProviderClassPackageGenerator, PackageGenerator repositoryClassPackageGenerator,PackageGenerator abstractRepositoryClassPackageGenerator, PackageGenerator repositoryImplClassPackageGenerator, String ddlPackageName, String dialectName, J4sqlMybatisGeneratorCustomizer customizer, boolean supportColumnInsertOrUpdate, String tableEnumClassPackage, String tableEnumClassName) {
        super(modelWarehouse, includePackages, excludePackages, poClassPackageGenerator, updateClassPackageGenerator, supportClassPackageGenerator, mapperClassPackageGenerator, sqlProviderClassPackageGenerator, repositoryClassPackageGenerator,abstractRepositoryClassPackageGenerator, repositoryImplClassPackageGenerator, ddlPackageName, dialectName, customizer, supportColumnInsertOrUpdate, tableEnumClassPackage, tableEnumClassName);
        this.filer = argNotNull(filer, "filer");
        this.originatingElements = originatingElements;
    }

    @Override
    protected void write(SqlFile sqlFile) {
        sqlFile = sqlFile.toBuilder().addOriginatingElements(this.originatingElements).build();
        sqlFile.writeTo(filer);
    }

    protected void write(JavaFile javaFile) {
        javaFile = javaFile.toBuilder().addOriginatingElements(this.originatingElements).build();
        javaFile.writeTo(filer);
    }
}
