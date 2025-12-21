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

import glz.hawk.j4sql.mybatis.writer.MybatisBuilderContext;
import glz.hawk.j4sql.mybatis.writer.MybatisBuilderContextImpl;
import glz.hawk.j4sql.mybatis.statement.GeneralStatementProvider;
import glz.hawk.j4sql.mybatis.statement.impl.GeneralStatementProviderImpl;
import glz.hawk.j4sql.dsl.delete.Delete;
import glz.hawk.j4sql.dsl.insert.Insert;
import glz.hawk.j4sql.dsl.select.Select;
import glz.hawk.j4sql.dsl.update.Update;
import glz.hawk.j4sql.support.SqlBuilder;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public abstract class SqlProviderHelper {


    public static GeneralStatementProvider generalInsert(SqlBuilder<MybatisBuilderContext> sqlBuilder, Insert insert) {
        MybatisBuilderContext mybatisBuilderContext = new MybatisBuilderContextImpl(GeneralStatementProvider.PARAM_PREFIX);
        String sql = sqlBuilder.build(insert, mybatisBuilderContext);
        return GeneralStatementProviderImpl.builder(sql).addParams(mybatisBuilderContext.getParameters()).build();
    }

    public static GeneralStatementProvider generalDelete(SqlBuilder<MybatisBuilderContext> sqlBuilder, Delete delete) {
        MybatisBuilderContext mybatisBuilderContext = new MybatisBuilderContextImpl(GeneralStatementProvider.PARAM_PREFIX);
        String sql = sqlBuilder.build(delete, mybatisBuilderContext);
        return GeneralStatementProviderImpl.builder(sql).addParams(mybatisBuilderContext.getParameters()).build();
    }

    public static GeneralStatementProvider generalUpdate(SqlBuilder<MybatisBuilderContext> sqlBuilder, Update update) {
        MybatisBuilderContext mybatisBuilderContext = new MybatisBuilderContextImpl(GeneralStatementProvider.PARAM_PREFIX);
        String sql = sqlBuilder.build(update, mybatisBuilderContext);
        return GeneralStatementProviderImpl.builder(sql).addParams(mybatisBuilderContext.getParameters()).build();
    }

    public static GeneralStatementProvider generalSelect(SqlBuilder<MybatisBuilderContext> sqlBuilder, Select select) {
        MybatisBuilderContext mybatisBuilderContext = new MybatisBuilderContextImpl(GeneralStatementProvider.PARAM_PREFIX);
        String sql = sqlBuilder.build(select, mybatisBuilderContext);
        return GeneralStatementProviderImpl.builder(sql).addParams(mybatisBuilderContext.getParameters()).build();
    }
}
