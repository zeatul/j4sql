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

package glz.hawk.j4sql.dsl.select;

import glz.hawk.j4sql.support.AliasedSelectColumn;
import glz.hawk.j4sql.support.Configuration;
import glz.hawk.j4sql.support.NamedColumn;
import glz.hawk.j4sql.support.PhysicalTable;
import glz.hawk.j4sql.support.impl.DefaultBuilderContext;
import glz.hawk.j4sql.support.impl.DefaultConfiguration;
import glz.hawk.j4sql.support.impl.SqlBuilderImpl;
import glz.hawk.j4sql.writer.impl.SqlWriterImpl;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static glz.hawk.j4sql.support.impl.DSL.*;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class SelectTest {

    private final Configuration configuration = new DefaultConfiguration();
    PhysicalTable bookType = tab("BOOK_TYPE");
    AliasedSelectColumn<NamedColumn> bookTypeCode = col("BOOK_TYPE_CODE").as("bookTypeCode");
    NamedColumn bookTypeName = col("BOOK_TYPE_NAME");

    PhysicalTable book = tab("BOOK");
    AliasedSelectColumn<NamedColumn> bookId = col("BOOK_ID").as("bookId");
    AliasedSelectColumn<NamedColumn> bookName = col("BOOK_NAME").as("bookName");
    AliasedSelectColumn<NamedColumn> bookPrice = col("BOOK_PRICE").as("bookPrice");

    PhysicalTable author = tab("AUTHOR");
    AliasedSelectColumn<NamedColumn> authorId = col("AUTHOR_ID").as("authorId");
    AliasedSelectColumn<NamedColumn> authorName = col("AUTHOR_NAME").as("authorName");

    PhysicalTable bookAuthorMap = tab("BOOK_AUTHOR_MAP");
    AliasedSelectColumn<NamedColumn> bookAuthorMapId = col("BOOK_AUTHOR_MAP_ID").as("bookAuthorId");

    @Test
    public void testSelect() {
        System.out.println("Start Test Suit!");

        System.out.println();
        System.out.println("# test normal sql");
        Select select = select(bookId, bookName, bookPrice, select(bookTypeName).from(bookType).where(col(bookType, bookTypeCode).eq(col(book, bookTypeCode))).asColumn("bookTypeName"))
            .from(book)
            .where(bookPrice.gt(new BigDecimal("15.60")), and(bookName.like("Hello%")),
                and(exists(select().from(bookAuthorMap).where(col(bookAuthorMap, bookId).eq(col(book, bookId))).build())),
                and(not(exists(select().from(bookAuthorMap).where(col(bookAuthorMap, bookId).eq(col(book, bookId))).build()))))
            .or(bookId.between(10, 1000), and(bookName.isNotNull()), and(bookPrice.isNull()))
            .orderBy(bookId.asc(), bookName.desc(), bookPrice.sortDefault())
            .limit(100)
            .offset(5)
            .forUpdate()
            .wait(5)
            .build();
        System.out.println(new SqlBuilderImpl<>(configuration, SqlWriterImpl::new).build(select, new DefaultBuilderContext()));

        System.out.println();
        System.out.println("# test join sql");
        select = select(bookName, authorName)
            .from(book)
            .join(bookAuthorMap)
            .on(col(book, bookId).eq(col(bookAuthorMap, bookId)))
            .join(author)
            .on(col(author, authorId).eq(col(bookAuthorMap, authorId)))
            .build();
        System.out.println((new SqlBuilderImpl<>(configuration, SqlWriterImpl::new).build(select, new DefaultBuilderContext())));

        System.out.println();
        System.out.println("# test group by，having and subquery table sql");
        select = select().from(select(bookTypeCode, sum(bookPrice).as("total"))
                .from(book)
                .groupBy(bookTypeCode)
                .having(sum(bookPrice).gt(0)).asTable("T1"))
            .build();
        System.out.println((new SqlBuilderImpl<>(configuration, SqlWriterImpl::new).build(select, new DefaultBuilderContext())));

        System.out.println();
        System.out.println("# test expression");
        select = select(bookTypeCode, bookPrice.plus(120).times(4).as("twoTimes"))
            .from(book)
            .build();
        System.out.println((new SqlBuilderImpl<>(configuration, SqlWriterImpl::new).build(select, new DefaultBuilderContext())));

    }

}
