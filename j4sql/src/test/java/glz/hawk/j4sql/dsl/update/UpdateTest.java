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

package glz.hawk.j4sql.dsl.update;

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
public class UpdateTest {
    private final Configuration configuration = new DefaultConfiguration();
    private final PhysicalTable BOOK = tab("BOOK");
    private final AliasedSqlTable<PhysicalTable> ALIASED_BOOK = BOOK.as("B");
    private final NamedColumn BOOK_ID = col("BOOK_ID");
    private final NamedColumn BOOK_NAME = col("BOOK_NAME");
    private final NamedColumn BOOK_PRICE = col("BOOK_PRICE");

    private final PhysicalTable AUTHOR = tab("AUTHOR");
    private final AliasedSqlTable<PhysicalTable> ALIASED_AUTHOR = AUTHOR.as("A");
    private final NamedColumn AUTHOR_ID = col("AUTHOR_ID");
    private final NamedColumn AUTHOR_NAME = col("AUTHOR_NAME");

    private final PhysicalTable BOOK_AUTHOR_MAP = tab("BOOK_AUTHOR_MAP");
    private final AliasedSqlTable<PhysicalTable> ALIASED_BOOK_AUTHOR_MAP = BOOK_AUTHOR_MAP.as("M");

    @Test
    public void updateTest() {
        Update update = update(BOOK)
            .set(BOOK_NAME, "测试")
            .set(BOOK_PRICE, 20.12)
            .where(BOOK_ID.eq(1000))
            .build();
        System.out.println(new SqlBuilderImpl<>(configuration, SqlWriterImpl::new).build(update, new DefaultBuilderContext()));
    }

    @Test
    public void updateTest2() {
        Update update = update(ALIASED_BOOK)
            .join(ALIASED_BOOK_AUTHOR_MAP)
            .on(col(ALIASED_BOOK, BOOK_ID).eq(col(ALIASED_BOOK_AUTHOR_MAP, BOOK_ID)))
            .join(ALIASED_AUTHOR)
            .on(col(ALIASED_AUTHOR, AUTHOR_ID).eq(col(ALIASED_BOOK_AUTHOR_MAP, AUTHOR_ID)))
            .set(BOOK_NAME, "测试")
            .set(BOOK_PRICE, 20.12)
            .where(col(ALIASED_AUTHOR, AUTHOR_NAME).like("Hello%"))
            .build();
        System.out.println(new SqlBuilderImpl<>(configuration, SqlWriterImpl::new).build(update, new DefaultBuilderContext()));
    }
}
