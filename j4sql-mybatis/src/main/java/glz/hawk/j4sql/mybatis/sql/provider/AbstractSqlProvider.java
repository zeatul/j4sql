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

package glz.hawk.j4sql.mybatis.sql.provider;

import glz.hawkframework.core.helper.ObjectHelper;
import glz.hawk.j4sql.mybatis.writer.MybatisBuilderContext;
import glz.hawk.j4sql.mybatis.statement.GeneralStatementProvider;
import glz.hawk.j4sql.dsl.delete.Delete;
import glz.hawk.j4sql.dsl.insert.Insert;
import glz.hawk.j4sql.dsl.select.Select;
import glz.hawk.j4sql.dsl.update.Update;
import glz.hawk.j4sql.support.SqlBuilder;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public abstract class AbstractSqlProvider {

    /**
     *
     */
    protected final ConcurrentHashMap<String, String> sqlCache = new ConcurrentHashMap<>();
    protected final SqlBuilder<MybatisBuilderContext> sqlBuilder;

    protected AbstractSqlProvider(SqlBuilder<MybatisBuilderContext> sqlBuilder) {
        this.sqlBuilder = sqlBuilder;
    }

    protected String buildKey(String methodName, Class<?>... parameterClasses) {
        return String.format("%s:%s:(%s)", getClass().getCanonicalName(), methodName,
            ObjectHelper.isEmpty(parameterClasses) ? "" : Arrays.stream(parameterClasses).map(Class::getCanonicalName).collect(Collectors.joining(", ")));
    }

    protected String buildKey(Object... keyPars) {
        return Arrays.stream(keyPars).map(this::convertToString).collect(Collectors.joining("@@@"));
    }

    public GeneralStatementProvider generalInsert(Insert insert) {
        return SqlProviderHelper.generalInsert(sqlBuilder, insert);
    }

    public GeneralStatementProvider generalDelete(Delete delete) {
        return SqlProviderHelper.generalDelete(sqlBuilder, delete);
    }

    public GeneralStatementProvider generalUpdate(Update update) {
        return SqlProviderHelper.generalUpdate(sqlBuilder, update);
    }

    public GeneralStatementProvider generalSelect(Select select) {
        return SqlProviderHelper.generalSelect(sqlBuilder, select);
    }

    protected String convertToString(Object object) {
        if (object instanceof Class<?>) {
            return ((Class<?>) object).getCanonicalName();
        } else if (object == null) {
            return "null";
        } else {
            return object.toString();
        }
    }

}
