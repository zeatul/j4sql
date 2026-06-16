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

package glz.hawk.j4sql.util;

import glz.hawk.j4sql.dsl.select.Select;
import glz.hawk.j4sql.support.Configuration;
import glz.hawk.j4sql.support.impl.DefaultBuilderContext;
import glz.hawk.j4sql.support.impl.SqlBuilderImpl;
import glz.hawk.j4sql.writer.impl.SqlWriterImpl;

import javax.annotation.Nonnull;

/**
 * This class is responsible for
 *
 * @author Zhang Peng
 */
public abstract class SqlUtils {

    public static boolean compare(@Nonnull String sql1, @Nonnull String sql2) {
        return compress(sql1).equals(compress(sql2));
    }


    public static String compress(@Nonnull String sql) {
        return sql.replaceAll("\\s+", " ").trim();
    }

    public static String toSql(@Nonnull Configuration configuration, @Nonnull Select select) {
        return new SqlBuilderImpl<>(configuration, SqlWriterImpl::new).build(select, new DefaultBuilderContext());
    }
}
