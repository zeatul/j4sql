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

import glz.hawk.j4sql.condition.Condition;
import glz.hawk.j4sql.support.*;
import glz.hawk.j4sql.support.impl.DefaultBuilderContext;
import glz.hawk.j4sql.support.impl.DefaultConfiguration;
import glz.hawk.j4sql.support.impl.SqlBuilderImpl;
import glz.hawk.j4sql.util.YamlUtils;
import glz.hawk.j4sql.writer.impl.SqlWriterImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static glz.hawk.j4sql.support.impl.DSL.*;
import static glz.hawk.j4sql.util.SqlUtils.compress;
import static glz.hawk.j4sql.util.SqlUtils.toSql;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class SelectTest {

    private static Map<String, String> sqlMap;

    private final Configuration configuration = DefaultConfiguration.builder().setDialect(SqlDialect.MYSQL).build();

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

//    AliasedSelectColumn<NamedColumn> bookAuthorMapId = col("BOOK_AUTHOR_MAP_ID").as("bookAuthorId");

    PhysicalTable bookAuthorMap = tab("BOOK_AUTHOR_MAP");

    @BeforeAll
    public static void setup() {
        sqlMap = YamlUtils.readYaml("/glz/hawk/j4sql/dsl/SelectTest.sql");
    }


    @Test
    public void test_always_false_condition() {
        Select select = select(bookId, bookName, bookPrice, select(bookTypeName).from(bookType).where(col2(bookType, bookTypeCode).eq(col2(book, bookTypeCode))).asColumn("bookTypeName"))
            .from(book)
            .where(colv(1).eq(colv(-1)))
            .build();
        System.out.println(new SqlBuilderImpl<>(configuration, SqlWriterImpl::new).build(select, new DefaultBuilderContext()));
    }

    @Test
    public void test_between_condition() {
        Condition condition = $c(TRUE, and(bookPrice.between(new BigDecimal("15.60"), new BigDecimal("115.60")))).build();
        Select select = select(bookId, bookName, bookPrice)
            .from(book)
            .where(condition)
            .build();
        System.out.println(new SqlBuilderImpl<>(configuration, SqlWriterImpl::new).build(select, new DefaultBuilderContext()));

         condition = $c(TRUE, and(colv(new BigDecimal("15.60")).between(bookPrice, new BigDecimal("115.60")))).build();
         select = select(bookId, bookName, bookPrice)
            .from(book)
            .where(condition)
            .build();
        System.out.println(new SqlBuilderImpl<>(configuration, SqlWriterImpl::new).build(select, new DefaultBuilderContext()));
    }

    @Test
    public void test_ignore_condition() {
        Condition condition = $c(TRUE,
            or(true, ()->bookPrice.gt(new BigDecimal("15.60"))),
            and(true, ()->bookName.like("Hello%")),
            and(true, ()->bookTypeName.like("Hello%")))
            .build();
        Select select = select(bookId, bookName, bookPrice, select(bookTypeName).from(bookType).where(col2(bookType, bookTypeCode).eq(col2(book, bookTypeCode))).asColumn("bookTypeName"))
            .from(book)
            .where(condition)
            .build();
        System.out.println(new SqlBuilderImpl<>(configuration, SqlWriterImpl::new).build(select, new DefaultBuilderContext()));
    }

    @Test
    public void test_normal_sql() {
        Select select = select(bookId, bookName, bookPrice, select(bookTypeName).from(bookType).where(col2(bookType, bookTypeCode).eq(col2(book, bookTypeCode))).asColumn("bookTypeName"))
            .hint("hint_hint_hint")
            .from(book)
            .where(bookPrice.gt(new BigDecimal("15.60")), and(bookName.like("Hello%")),
                and(exists(select().from(bookAuthorMap).where(col2(bookAuthorMap, bookId).eq(col2(book, bookId))).build())),
                and(not(exists(select().from(bookAuthorMap).where(col2(bookAuthorMap, bookId).eq(col2(book, bookId))).build()))))
            .or(bookId.between(10, 1000), and(bookName.isNotNull()), and(bookPrice.isNull()))
            .orderBy(bookId.asc(), bookName.desc(), bookPrice.sortDefault())
            .limit(100)
            .offset(5)
            .forUpdate()
            .wait(5)
            .build();
        assertThat(compress(toSql(configuration, select))).isEqualTo(compress(sqlMap.get("test_normal_sql")));
    }

    @Test
    public void test_join_sql() {
        Select select = select(bookName, authorName)
            .from(book)
            .join(bookAuthorMap)
            .on(col2(book, bookId).eq(col2(bookAuthorMap, bookId)))
            .join(author)
            .on(col2(author, authorId).eq(col2(bookAuthorMap, authorId)))
            .build();
        System.out.println((new SqlBuilderImpl<>(configuration, SqlWriterImpl::new).build(select, new DefaultBuilderContext())));
    }

    @Test
    public void test_alias_table_sql() {
        Select select = select(bookName, authorName)
            .from(book.as("t1"))
            .join(bookAuthorMap.as("t2"))
            .on(col2("t1", bookId).eq(col2(bookAuthorMap.as("t2"), bookId)))
            .join(author)
            .on(col2(author, authorId).eq(col2(bookAuthorMap, authorId)))
            .build();
        System.out.println((new SqlBuilderImpl<>(configuration, SqlWriterImpl::new).build(select, new DefaultBuilderContext())));
    }

    @Test
    public void test_groupBy_sql() {

        Select select = select().from(select(bookTypeCode, sum(bookPrice).as("total"))
                .from(book)
                .groupBy(bookTypeCode)
                .having(sum(bookPrice).gt(0)).asTable("T1"))
            .build();
        System.out.println((new SqlBuilderImpl<>(configuration, SqlWriterImpl::new).build(select, new DefaultBuilderContext())));


    }

    @Test
    public void test_column_compute() {
        Select select = select(bookTypeCode, bookPrice.plus(120).times(4).as("twoTimes"))
            .from(book)
            .build();
        System.out.println((new SqlBuilderImpl<>(configuration, SqlWriterImpl::new).build(select, new DefaultBuilderContext())));
    }

}
