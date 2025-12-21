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

package glz.hawk.j4sql.mybatis.translator;

import glz.hawk.jdesigner.spec.base.Model;
import glz.hawk.jdesigner.spec.database.Column;
import glz.hawk.jdesigner.spec.database.Table;
import glz.hawk.jdesigner.translator.Translator;
import glz.hawkframework.core.helper.StringHelper;
import glz.hawk.codepoet.java.type.ClassName;
import glz.hawk.codepoet.java.type.ParameterizedTypeName;

import java.util.List;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public abstract class AbstractTableToSupport {

    protected final Translator<Model, String> supportClassPackageTranslator;
    protected final Translator<Table, String> supportClassNameTranslator;
    protected final Translator<Model, String> poClassPackageTranslator;
    protected final Translator<Table, String> poClassNameTranslator;
    protected final Translator<Model, String> updateClassPackageTranslator;
    protected final Translator<Table, String> updateClassNameTranslator;
    protected final Translator<Table, String> columnUpdateClassNameTranslator;

    protected AbstractTableToSupport(Translator<Model, String> supportClassPackageTranslator, Translator<Table, String> supportClassNameTranslator, Translator<Model, String> poClassPackageTranslator, Translator<Table, String> poClassNameTranslator, Translator<Model, String> updateClassPackageTranslator, Translator<Table, String> updateClassNameTranslator, Translator<Table, String> columnUpdateClassNameTranslator) {
        this.supportClassPackageTranslator = argNotNull(supportClassPackageTranslator, "supportClassPackageTranslator");
        this.supportClassNameTranslator = argNotNull(supportClassNameTranslator, "supportClassNameTranslator");
        this.poClassPackageTranslator = argNotNull(poClassPackageTranslator, "poClassPackageTranslator");
        this.poClassNameTranslator = argNotNull(poClassNameTranslator, "poClassNameTranslator");
        this.updateClassPackageTranslator = argNotNull(updateClassPackageTranslator, "updateClassPackageTranslator");
        this.updateClassNameTranslator = argNotNull(updateClassNameTranslator, "updateClassNameTranslator");
        this.columnUpdateClassNameTranslator = argNotNull(columnUpdateClassNameTranslator, "columnUpdateClassNameTranslator");
    }

    protected String fieldTableName(Table table) {
        return table.getName();
    }

    protected String fieldColumnName(Column column) {
        return column.getName();
    }

    protected String fieldColumnsName(Table table) {
        return "COLUMNS";
    }

    protected String fieldColumnsParamName(Table table) {
        return "PARAM_COLUMNS";
    }

    protected String paramColumnName(Column column) {
        return String.format("PARAM_%S", column.getName());
    }

    protected String poParamName(Table table) {
        return StringHelper.unCapitalize(poClassNameTranslator.translate(table));
    }

    protected ClassName poClassName(Table table) {
        return ClassName.of(poClassPackageTranslator.translate(table), poClassNameTranslator.translate(table));
    }

    protected String posParamName(Table table) {
        return poParamName(table) + "s";
    }

    protected ParameterizedTypeName posClassName(Table table) {
        return ParameterizedTypeName.of(List.class, poClassName(table));
    }

    protected ClassName updateClassName(Table table) {
        return ClassName.of(updateClassPackageTranslator.translate(table), updateClassNameTranslator.translate(table));
    }

    protected String updateParamName(Table table) {
        return StringHelper.unCapitalize(updateClassNameTranslator.translate(table));
    }

    protected ClassName columnUpdateClassName(Table table) {
        return ClassName.of(updateClassPackageTranslator.translate(table), columnUpdateClassNameTranslator.translate(table));
    }

    protected String columnUpdateParamName(Table table) {
        return StringHelper.unCapitalize(columnUpdateClassNameTranslator.translate(table));
    }

    protected String fieldColumnsName() {
        return "COLUMNS";
    }

    protected ClassName supportClassName(Table table) {
        return ClassName.of(supportClassPackageTranslator.translate(table), supportClassNameTranslator.translate(table));
    }

}
