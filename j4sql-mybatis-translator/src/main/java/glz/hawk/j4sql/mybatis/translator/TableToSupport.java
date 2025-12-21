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
import glz.hawk.j4sql.mybatis.sql.MybatisParam;
import glz.hawk.j4sql.support.PhysicalTable;
import glz.hawk.j4sql.support.impl.DefaultAliasedNamedColumn;
import glz.hawk.j4sql.support.impl.DefaultPhysicalTable;
import glz.hawk.codepoet.java.ClassSpec;
import glz.hawk.codepoet.java.FieldSpec;
import glz.hawk.codepoet.java.JavaFile;
import glz.hawk.codepoet.java.type.ArrayTypeName;
import org.apache.ibatis.type.JdbcType;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;
import java.util.Arrays;
import java.util.stream.Collectors;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class TableToSupport extends AbstractTableToSupport implements Translator<Table, JavaFile> {

    private final Translator<Column, String> fieldNameTranslator;
    private final Translator<Column, String> getterNameTranslator;
    private final Translator<Column, String> getterUpdatedNameTranslator;

    public TableToSupport(Translator<Model, String> supportClassPackageTranslator, Translator<Table, String> supportClassNameTranslator,
                          Translator<Model, String> poClassPackageTranslator, Translator<Table, String> poClassNameTranslator,
                          Translator<Column, String> fieldNameTranslator, Translator<Column, String> getterNameTranslator,
                          Translator<Model, String> updateClassPackageTranslator, Translator<Table, String> updateClassNameTranslator, Translator<Table, String> columnUpdateClassNameTranslator,
                          Translator<Column, String> getterUpdatedNameTranslator) {
        super(supportClassPackageTranslator, supportClassNameTranslator, poClassPackageTranslator, poClassNameTranslator, updateClassPackageTranslator, updateClassNameTranslator, columnUpdateClassNameTranslator);
        this.fieldNameTranslator = argNotNull(fieldNameTranslator, "fieldNameTranslator");
        this.getterNameTranslator = argNotNull(getterNameTranslator, "getterNameTranslator");
        this.getterUpdatedNameTranslator = argNotNull(getterUpdatedNameTranslator, "getterUpdatedNameTranslator");
    }

    @Nonnull
    @Override
    public JavaFile translate(@Nonnull Table table) {

        argNotNull(table, "table");
        String className = supportClassNameTranslator.translate(table);
        ClassSpec.Builder builder = ClassSpec.builder(className).addModifier(Modifier.PUBLIC);

        // fields
        // table
        builder.addField(fieldTable(table));

        // column
        Arrays.stream(table.getColumns()).map(this::fieldColumn).forEach(builder::addField);

        // columns
        builder.addField(fieldColumns(table));

        // param
        Arrays.stream(table.getColumns()).map(this::paramFieldColumn).forEach(builder::addField);

        // params
        builder.addField(paramFieldColumns(table));

        // methods

        // static imports
        builder.addStaticImport(java.sql.Types.class, "*");

        return JavaFile.builder(supportClassPackageTranslator.translate(table), builder.build()).build();
    }

    protected FieldSpec fieldTable(Table table) {
        return FieldSpec.builder(PhysicalTable.class, fieldTableName(table), Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            .setInitializer("new $T($S)", DefaultPhysicalTable.class, fieldTableName(table))
            .build();
    }

    protected FieldSpec fieldColumn(Column column) {
        return FieldSpec.builder(DefaultAliasedNamedColumn.class, fieldColumnName(column), Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            .setInitializer("new $T($S, $S)", DefaultAliasedNamedColumn.class, fieldColumnName(column), fieldNameTranslator.translate(column))
            .build();
    }

    protected FieldSpec fieldColumns(Table table) {
        return FieldSpec.builder(ArrayTypeName.ofType(DefaultAliasedNamedColumn.class), fieldColumnsName(), Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            .setInitializer("new $T[]{$L}", DefaultAliasedNamedColumn.class, Arrays.stream(table.getColumns()).map(Column::getName).collect(Collectors.joining(", ")))
            .build();
    }

    protected FieldSpec paramFieldColumn(Column column) {
        return FieldSpec.builder(MybatisParam.class, paramColumnName(column), Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            .setInitializer("$T.builder($S, $L).build()", MybatisParam.class, fieldNameTranslator.translate(column), JdbcType.forCode(column.getDataType().getType()).name())
            .build();
    }

    protected FieldSpec paramFieldColumns(Table table) {
        return FieldSpec.builder(ArrayTypeName.ofType(MybatisParam.class), "PARAM_COLUMNS", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            .setInitializer("new $T[]{$L}", MybatisParam.class, Arrays.stream(table.getColumns()).map(this::paramColumnName).collect(Collectors.joining(", ")))
            .build();
    }

    /// ///////////////


}
