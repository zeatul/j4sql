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

package glz.hawk.j4sql.mybatis.mapper;

import glz.hawk.j4sql.mybatis.sql.provider.SqlProviderAdapter;
import glz.hawk.j4sql.mybatis.statement.GeneralStatementProvider;
import glz.hawk.j4sql.mybatis.statement.InsertStatementProvider;
import glz.hawk.j4sql.mybatis.statement.MultiRowsInsertStatementProvider;
import org.apache.ibatis.annotations.Flush;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.executor.BatchResult;

import java.util.List;

/**
 * This interface is responsible for
 *
 * @author Hawk
 */
public interface BaseInsertMapper<T> {

    @InsertProvider(SqlProviderAdapter.class)
    int insert(InsertStatementProvider<T> statementProvider );

    @InsertProvider(SqlProviderAdapter.class)
    int insertMultiple(MultiRowsInsertStatementProvider<T> statementProvider);

    @InsertProvider(SqlProviderAdapter.class)
    int insertSelective(InsertStatementProvider<T> statementProvider);

    @InsertProvider(SqlProviderAdapter.class)
    int insertGeneral(GeneralStatementProvider statementProvider);

    @Flush
    List<BatchResult> flush();
}
