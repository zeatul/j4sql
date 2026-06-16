package glz.hawk.j4sql.mybatis.translator;

import glz.hawk.jdesigner.spec.database.Column;
import glz.hawk.jdesigner.spec.database.Table;
import glz.hawk.jdesigner.translator.Translator;

public class DefaultTableToSupportFactory implements TableToSupportFactory{
    @Override
    public TableToSupport generate(Translator<Table, String> supportClassPackageTranslator, Translator<Table, String> supportClassNameTranslator, Translator<Table, String> poClassPackageTranslator, Translator<Table, String> poClassNameTranslator, Translator<Table, String> updateClassPackageTranslator, Translator<Table, String> updateClassNameTranslator, Translator<Table, String> columnUpdateClassNameTranslator, Translator<Table, String> fieldTableNameTranslator, Translator<Table, String> tableNameTranslator, Translator<Column, String> fieldColumnNameTranslator, Translator<Column, String> columnNameTranslator, boolean supportColumnInsertOrUpdate, Translator<Column, String> fieldNameTranslator, Translator<Column, String> jdbcTypeNameTranslator) {
        return new TableToSupport(supportClassPackageTranslator, supportClassNameTranslator, poClassPackageTranslator, poClassNameTranslator, updateClassPackageTranslator, updateClassNameTranslator,  columnUpdateClassNameTranslator, fieldTableNameTranslator, tableNameTranslator,  fieldColumnNameTranslator,  columnNameTranslator,  supportColumnInsertOrUpdate, fieldNameTranslator, jdbcTypeNameTranslator);
    }
}
