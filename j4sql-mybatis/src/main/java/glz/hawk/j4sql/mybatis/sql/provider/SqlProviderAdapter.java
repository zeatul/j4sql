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

import glz.hawk.j4sql.mybatis.statement.GeneralStatementProvider;
import glz.hawk.j4sql.mybatis.statement.InsertStatementProvider;
import glz.hawk.j4sql.mybatis.statement.MultiRowsInsertStatementProvider;
import glz.hawk.j4sql.mybatis.statement.UpdateStatementProvider;
import org.apache.ibatis.builder.annotation.ProviderMethodResolver;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class SqlProviderAdapter implements ProviderMethodResolver {

    /**
     * Map to {@link #allInOne(GeneralStatementProvider)} method, represent to any SQL.
     */
    public final static String ALL_IN_ONE = "allInOne";

    public String insert(InsertStatementProvider<?> statementProvider) {
        return statementProvider.getStatement();
    }

    public String insertMultiple(MultiRowsInsertStatementProvider<?> statementProvider) {
        return statementProvider.getStatement();
    }

    public String insertSelective(InsertStatementProvider<?> statementProvider) {
        return statementProvider.getStatement();
    }

    public String insertGeneral(GeneralStatementProvider statementProvider) {
        return statementProvider.getStatement();
    }

    public String deleteGeneral(GeneralStatementProvider statementProvider) {
        return statementProvider.getStatement();
    }

    public String update(UpdateStatementProvider statementProvider) {
        return statementProvider.getStatement();
    }

    public String updateGeneral(GeneralStatementProvider statementProvider) {
        return statementProvider.getStatement();
    }

    public String selectOne(GeneralStatementProvider statementProvider){
        return statementProvider.getStatement();
    }

    public String selectMany(GeneralStatementProvider statementProvider){
        return  statementProvider.getStatement();
    }

    public String count(GeneralStatementProvider statementProvider){
        return  statementProvider.getStatement();
    }

    public String cursor(GeneralStatementProvider statementProvider){
        return  statementProvider.getStatement();
    }

    public String allInOne(GeneralStatementProvider statementProvider){
        return statementProvider.getStatement();
    }

}
