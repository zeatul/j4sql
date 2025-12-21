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

package glz.hawk.j4sql.dsl.delete;

import glz.hawk.j4sql.support.AliasedSqlTable;
import glz.hawk.j4sql.support.Configuration;
import glz.hawk.j4sql.support.NamedColumn;
import glz.hawk.j4sql.support.PhysicalTable;
import glz.hawk.j4sql.support.impl.DefaultBuilderContext;
import glz.hawk.j4sql.support.impl.DefaultConfiguration;
import glz.hawk.j4sql.support.impl.SqlBuilderImpl;
import glz.hawk.j4sql.writer.impl.SqlWriterImpl;
import org.junit.jupiter.api.Test;

import static glz.hawk.j4sql.support.impl.DSL.*;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class DeleteTest {
    private final Configuration configuration = new DefaultConfiguration();
    private final PhysicalTable BOOK = tab("BOOK");
    private final AliasedSqlTable<PhysicalTable> ALIASED_BOOK = BOOK.as("t");
    private final NamedColumn BOOK_ID = col("BOOK_ID");
    private final NamedColumn BOOK_NAME = col("BOOK_NAME");
    private final NamedColumn BOOK_PRICE = col("BOOK_PRICE");

    @Test
    public void deleteTest() {
        Delete delete = deleteFrom(BOOK).where(BOOK_ID.eq(100), or(BOOK_NAME.like("Hello%"))).and(BOOK_PRICE.ge(10.01)).build();
        System.out.println(new SqlBuilderImpl<>(configuration, SqlWriterImpl::new).build(delete, new DefaultBuilderContext()));

        delete = deleteFrom(ALIASED_BOOK).where(BOOK_ID.eq(100)).build();
        System.out.println(new SqlBuilderImpl<>(configuration, SqlWriterImpl::new).build(delete, new DefaultBuilderContext()));
    }
}
