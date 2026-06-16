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

import java.time.LocalDate;

import static glz.hawk.j4sql.support.impl.DSL.*;
import static org.assertj.core.api.BDDAssertions.and;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class UpdateTest {
    private final Configuration configuration = DefaultConfiguration.builder().build();
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
    public void update_join_test() {
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

    @Test
    public void update_multiple_table_test() {
        PhysicalTable CTAT_CL_BAL = tab("CTAT_CL_BAL");
        AliasedSqlTable<?> a = CTAT_CL_BAL.as("a");
        NamedColumn ACBA = col("ACBA");
        NamedColumn AR_ACC_ID = col("AR_ACC_ID");
        NamedColumn LDGR_AMT_TP_END = col("LDGR_AMT_TP_END");
        Update update = update(a,
            select(sum(ACBA).as("TOTAL_AMOUNT")).from(CTAT_CL_BAL).where(AR_ACC_ID.eq("10001111")).asTable("b")
        ).set(col(a, ACBA), col(a, ACBA).plus(200))
            .where(col(a, AR_ACC_ID).eq("111111"), and(col(a, LDGR_AMT_TP_END).eq("J001")), and(col("b", "TOTAL_AMOUNT").plus(200).le(1000)))
            .build();
        System.out.println(new SqlBuilderImpl<>(configuration, SqlWriterImpl::new).build(update, new DefaultBuilderContext()));
    }

    @Test
    public void update_case_when_test() {
        PhysicalTable CATA_CL_BAL_BAK = tab("CATA_CL_BAL_BAK");
        AliasedSqlTable<PhysicalTable> a = CATA_CL_BAL_BAK.as("a");
        NamedColumn AR_ACC_ID = col("AR_ACC_ID");
        NamedColumn ACBA = col("ACBA");
        NamedColumn ACR_DT = col("ACR_DT");
        NamedColumn PREVDAY_BAL = col("PREVDAY_BAL");
        NamedColumn LDGR_AMT_TP_ECDA = col("LDGR_AMT_TP_ECDA");
        Update update = update(a)
            .join(
                select(AR_ACC_ID, sum(ACBA).as("totalAmt"))
                    .from(CATA_CL_BAL_BAK)
                    .where(AR_ACC_ID.eq("AR000100000"))
                    .groupBy(AR_ACC_ID)
                    .asTable("b")
            )
            .on(col(a, AR_ACC_ID).eq(col("b", AR_ACC_ID)))
            .set(col(a, ACBA), col(a, ACBA).plus(5))
            .set(col(a, ACR_DT), cases()
                .when(col(a, ACR_DT).lt(LocalDate.of(2026, 6, 5))).then(LocalDate.of(2026, 6, 5))
                .elses(col(a, ACR_DT)).asColumn())
            .set(col(a, PREVDAY_BAL), cases()
                .when(col(a, ACR_DT).lt(LocalDate.of(2026, 6, 5))).then(col(a, ACBA))
                .when(col(a, ACR_DT).gt(LocalDate.of(2026, 6, 5))).then(col(a, ACBA).plus(5))
                .elses(col(a, PREVDAY_BAL)).asColumn())
            .where(col(a, AR_ACC_ID).eq("AR00011111"), and(col(a, LDGR_AMT_TP_ECDA).eq("J0001")), and(col("b", "totalAmt").le(500)))
            .build();
        System.out.println(new SqlBuilderImpl<>(configuration, SqlWriterImpl::new).build(update, new DefaultBuilderContext()));
    }

}
