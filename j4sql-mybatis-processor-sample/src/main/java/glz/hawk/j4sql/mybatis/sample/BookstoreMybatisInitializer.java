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

package glz.hawk.j4sql.mybatis.sample;

import glz.hawk.j4sql.mybatis.annotation.J4sqlMybatisGeneratorDef;
import glz.hawk.jdesigner.generator.annotation.PackageMaps;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
@J4sqlMybatisGeneratorDef(scanPackages = {"glz.hawk.bookstore.designer.meta", "glz.hawk.bookstore.designer.database"},
    resourceCollectors = {"glz.hawk.bookstore.designer.BookstoreModelIndexer"},
    poClassPackageMaps = @PackageMaps(namespacePackageMaps = {@PackageMaps.NamespacePackageMap(namespace = "glz.hawk.bookstore.designer.database", packageName = "glz.hawk.bookstore.persist.po")}),
    updateClassPackageMaps = @PackageMaps(namespacePackageMaps = {@PackageMaps.NamespacePackageMap(namespace = "glz.hawk.bookstore.designer.database", packageName = "glz.hawk.bookstore.persist.update")}),
    supportClassPackageMaps = @PackageMaps(namespacePackageMaps = {@PackageMaps.NamespacePackageMap(namespace = "glz.hawk.bookstore.designer.database", packageName = "glz.hawk.bookstore.persist.support")}),
    mapperClassPackageMaps = @PackageMaps(namespacePackageMaps = {@PackageMaps.NamespacePackageMap(namespace = "glz.hawk.bookstore.designer.database", packageName = "glz.hawk.bookstore.persist.mapper")}),
    sqlProviderClassPackageMaps = @PackageMaps(namespacePackageMaps = {@PackageMaps.NamespacePackageMap(namespace = "glz.hawk.bookstore.designer.database", packageName = "glz.hawk.bookstore.persist.sql.provider")}),
    repositoryClassPackageMaps = @PackageMaps(namespacePackageMaps = {@PackageMaps.NamespacePackageMap(namespace = "glz.hawk.bookstore.designer.database", packageName = "glz.hawk.bookstore.persist.repository")}),
    abstractRepositoryClassPackageMaps = @PackageMaps(namespacePackageMaps = {@PackageMaps.NamespacePackageMap(namespace = "glz.hawk.bookstore.designer.database", packageName = "glz.hawk.bookstore.persist.repository.abs")}),
    repositoryImplClassPackageMaps = @PackageMaps(namespacePackageMaps = {@PackageMaps.NamespacePackageMap(namespace = "glz.hawk.bookstore.designer.database", packageName = "glz.hawk.bookstore.persist.repository.impl")}),
    ddlPackageName = "ddl",
    dialectName = "mysql",
    supportColumnInsertOrUpdate = true,
    tableEnumClassPackage = "glz.hawk.bookstore.persist.support.enums",
    tableEnumClassName = "BookstoreTables"
)
public class BookstoreMybatisInitializer {
}
