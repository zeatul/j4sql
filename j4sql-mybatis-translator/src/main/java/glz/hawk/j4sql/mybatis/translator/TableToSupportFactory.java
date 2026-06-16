package glz.hawk.j4sql.mybatis.translator;

import glz.hawk.jdesigner.spec.database.Column;
import glz.hawk.jdesigner.spec.database.Table;
import glz.hawk.jdesigner.translator.Translator;

public interface TableToSupportFactory {
    TableToSupport generate(Translator<Table, String> supportClassPackageTranslator, Translator<Table, String> supportClassNameTranslator, Translator<Table, String> poClassPackageTranslator, Translator<Table, String> poClassNameTranslator, Translator<Table, String> updateClassPackageTranslator, Translator<Table, String> updateClassNameTranslator, Translator<Table, String> columnUpdateClassNameTranslator, Translator<Table, String> fieldTableNameTranslator, Translator<Table, String> tableNameTranslator, Translator<Column, String> fieldColumnNameTranslator, Translator<Column, String> columnNameTranslator, boolean supportColumnInsertOrUpdate, Translator<Column, String> fieldNameTranslator, Translator<Column, String> jdbcTypeNameTranslator);
}
