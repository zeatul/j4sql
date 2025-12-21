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

package glz.hawk.j4sql.support.impl;

import glz.hawk.j4sql.dsl.delete.Delete;
import glz.hawk.j4sql.dsl.insert.Insert;
import glz.hawk.j4sql.dsl.select.Select;
import glz.hawk.j4sql.dsl.update.Update;
import glz.hawk.j4sql.support.BuilderContext;
import glz.hawk.j4sql.support.Configuration;
import glz.hawk.j4sql.support.SqlBuilder;
import glz.hawk.j4sql.support.SqlWriterSupplier;
import glz.hawk.j4sql.writer.SqlWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.UncheckedIOException;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class SqlBuilderImpl<B extends BuilderContext> implements SqlBuilder<B> {

    private final static Logger LOG = LoggerFactory.getLogger(SqlBuilderImpl.class);

    private final Configuration configuration;

    private final SqlWriterSupplier<B> sqlWriterSupplier;

    public SqlBuilderImpl(Configuration configuration, SqlWriterSupplier<B> sqlWriterSupplier) {
        this.configuration = argNotNull(configuration, "configuration");
        this.sqlWriterSupplier = argNotNull(sqlWriterSupplier, "sqlWriterSupplier");
    }

    @Nonnull
    @Override
    public String build(Insert insert, B builderContext) {
        return templateBuild(configuration, insert, (Insert query, SqlWriter writer) -> writer.write(query), builderContext);
    }

    @Nonnull
    @Override
    public String build(Delete delete, B builderContext) {
        return templateBuild(configuration, delete, (Delete query, SqlWriter writer) -> writer.write(query), builderContext);
    }

    @Nonnull
    @Override
    public String build(Update update, B builderContext) {
        return templateBuild(configuration, update, (Update query, SqlWriter writer) -> writer.write(query), builderContext);
    }

    @Nonnull
    @Override
    public String build(Select select, B builderContext) {
        return templateBuild(configuration, select, (Select query, SqlWriter writer) -> writer.write(query), builderContext);
    }

    private <Q> String templateBuild(Configuration configuration, Q query, Function<Q> function, B builderContext) {
        StringBuilder sb = new StringBuilder();
        SqlWriter writer = sqlWriterSupplier.get(sb, configuration, builderContext);
        try {
            function.execute(query, writer);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        String sql = sb.toString();
        LOG.debug("The Sql is following: \n{}", sql);
        return sql;
    }


    @FunctionalInterface
    private static interface Function<Query> {
        void execute(Query query, SqlWriter writer) throws IOException;
    }
}
