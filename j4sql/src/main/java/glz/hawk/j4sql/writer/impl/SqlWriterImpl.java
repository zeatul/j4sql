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

package glz.hawk.j4sql.writer.impl;

import glz.hawk.j4sql.condition.Condition;
import glz.hawk.j4sql.condition.ConnectCondition;
import glz.hawk.j4sql.condition.impl.*;
import glz.hawk.j4sql.dsl.delete.Delete;
import glz.hawk.j4sql.dsl.insert.Insert;
import glz.hawk.j4sql.dsl.select.Select;
import glz.hawk.j4sql.dsl.update.Update;
import glz.hawk.j4sql.support.*;
import glz.hawk.j4sql.support.impl.EmptyValue;
import glz.hawk.j4sql.support.impl.JoinInfo;
import glz.hawk.j4sql.support.impl.NullValue;
import glz.hawk.j4sql.support.impl.SqlClause;
import glz.hawk.j4sql.writer.SqlWriter;
import glz.hawkframework.core.helper.StringHelper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static glz.hawk.j4sql.support.SqlDialect.MYSQL;
import static glz.hawk.j4sql.support.SqlDialect.ORACLE;
import static glz.hawk.j4sql.support.impl.Keywords.*;
import static glz.hawkframework.core.support.ArgumentSupport.argNotEmpty;
import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 * K:关键字
 *
 * @author Hawk
 */
public class SqlWriterImpl<B extends BuilderContext> extends AbstractSqlCodeWriter implements SqlWriter {

    protected final B builderContext;

    private final Configuration configuration;

    public SqlWriterImpl(Appendable out, Configuration configuration, B builderContext) {
        super(argNotNull(out, "out"), argNotNull(configuration, "configuration").getIndent(), configuration.getLineSeparator());
        this.configuration = configuration;
        this.builderContext = argNotNull(builderContext, "builderContext");
    }


    @Override
    public void write(Insert insert) throws IOException {
        emitInsert(argNotNull(insert, "insert").getInsertQuery(), configuration.isInline());
    }

    protected void emitInsert(InsertQuery query, boolean inline) throws IOException {

        // INSERT INTO TABLE
        builderContext.buildStep(SqlClause.INTO_TABLE_CLAUSE, () -> emit("$K $T", INSERT_INTO, query.getIntoTable()));

        // COLUMNS
        builderContext.buildStep(SqlClause.INTO_COLUMNS_CLAUSE, () -> {
            List<NamedColumn> intoColumns = query.getIntoColumns();
            if (!intoColumns.isEmpty()) {
                emit("(");
                emit("$C", intoColumns.get(0));
                for (int i = 1; i < intoColumns.size(); i++) {
                    emit(", $C", intoColumns.get(i));
                }
                emit(")");
            }
        });

        // VALUES
        builderContext.buildStep(SqlClause.INTO_VALUES_CLAUSE, () -> {
            List<List<ValueColumn>> valueColumnses = query.getValueColumnses();
            if (!valueColumnses.isEmpty()) {
                emitNewLineOrSpace(inline);
                emit("$K", VALUES);
            }
            for (int i = 0; i < valueColumnses.size(); i++) {
                if (i == 0) {
                    emit(" ");
                } else {
                    emitNewLine();
                    emit("$k", VALUES); // Align keywords
                }
                emitValueColumns(valueColumnses.get(i));
                if (i < valueColumnses.size() - 1) {
                    emit(",");
                }
            }
        });

        // SELECT
        Select select = query.getSelect();
        if (select != null) {
            emitNewLineOrSpace(inline);
            emitSelect(select.getSelectQuery(), inline);
        }
    }

    @Override
    public void write(Delete delete) throws IOException {
        emitDelete(argNotNull(delete, "delete").getDeleteQuery(), configuration.isInline());
    }

    protected void emitDelete(DeleteQuery query, boolean inline) throws IOException {
        // DELETE FROM
        builderContext.buildStep(SqlClause.DELETE_FROM_CLAUSE, () -> emit("$K $K $T", DELETE, FROM, query.getFromTable()));

        // JOIN ON
        emitJoinInfos(query.getJoinInfos(), inline);

        // WHERE
        emitWhere(query.getWhere(), inline);
    }

    @Override
    public void write(Update update) throws IOException {
        emitUpdate(argNotNull(update, "update").getUpdateQuery(), configuration.isInline());
    }

    protected void emitUpdate(UpdateQuery query, boolean inline) throws IOException {
        // UPDATE
        builderContext.buildStep(SqlClause.UPDATE_TABLE_CLAUSE, () -> {
            emit("$K $T", UPDATE, query.getUpdateTables().get(0));
            for (int i = 1; i < query.getUpdateTables().size(); i++) {
                emit(", $T", query.getUpdateTables().get(i));
            }
        });

        // JOIN ON
        emitJoinInfos(query.getJoinInfos(), inline);

        // SET
        List<UpdateColumnValuePair> updateColumnValuePairs = query.getUpdateColumnValuePairs();
        if (updateColumnValuePairs.isEmpty()) {
            throw new IllegalStateException("The updateColumnValueParis must not be empty.");
        }
        emitNewLineOrSpace(inline);
        builderContext.buildStep(SqlClause.SET_CLAUSE, () -> {
            emit("$K", SET);
            emit(" $C = $C", updateColumnValuePairs.get(0).getUpdateColumn(), updateColumnValuePairs.get(0).getValueColumn());
            for (int i = 1; i < updateColumnValuePairs.size(); i++) {
                emit(", $C = $C", updateColumnValuePairs.get(i).getUpdateColumn(), updateColumnValuePairs.get(i).getValueColumn());
            }
        });

        // WHERE
        emitWhere(query.getWhere(), inline);
    }

    @Override
    public void write(Select select) throws IOException {
        emitSelect(argNotNull(select, "select").getSelectQuery(), configuration.isInline());
    }

    protected void emitSelect(SelectQuery query, boolean inline) throws IOException {
        // SELECT
        builderContext.buildStep(SqlClause.SELECT_CLAUSE, () -> {
            emit("$K", SELECT);

            // mysql and oracle hint
            if ((configuration.getDialect() == MYSQL || configuration.getDialect() == ORACLE) && StringHelper.isNotBlank(query.getHint())) {
                emit(" /*+ $L */", query.getHint());
            }

            // COUNT DISTINCT
            if (query.isCount() && query.isDistinct()) {
                emit(" $K($K", COUNT, DISTINCT);
            }
            // COUNT
            else if (query.isCount()) {
                emit(" $K(", COUNT);
            }
            // DISTINCT
            else if (query.isDistinct()) {
                emit(" $K", DISTINCT);
            }

            List<SelectColumn> selectColumns = query.getSelect();
            if (selectColumns.isEmpty()) {
                emit(" $K", ASTERISK);
            } else {
                emit(" $C", selectColumns.get(0));
                for (int i = 1; i < selectColumns.size(); i++) {
                    emit(", $C", selectColumns.get(i));
                }
            }
            if (query.isCount()) {
                emit(")");
            }
        });

        // INTO
        SqlTable intoTable = query.getInto();
        if (intoTable != null) {
            emitNewLineOrSpace(inline);
            builderContext.buildStep(SqlClause.INTO_CLAUSE, () -> emit("$K %T", INTO, intoTable));
        }

        // FROM
        List<SqlTable> fromTables = query.getFrom();
        if (!fromTables.isEmpty()) {
            emitNewLineOrSpace(inline);
            builderContext.buildStep(SqlClause.SELECT_FROM_CLAUSE, () -> {
                emit("$K $T", FROM, fromTables.get(0));
                for (int i = 1; i < fromTables.size(); i++) {
                    emit(", $T", fromTables.get(i));
                }
            });
        }

        // JOIN
        emitJoinInfos(query.getJoinInfos(), inline);

        // WHERE
        emitWhere(query.getWhere(), inline);

        // GROUP BY
        List<SqlColumn> groupByColumns = query.getGroupBy();
        if (!groupByColumns.isEmpty()) {
            emitNewLineOrSpace(inline);
            builderContext.buildStep(SqlClause.GROUP_BY_CLAUSE, () -> {
                emit("$K $C", GROUP_BY, groupByColumns.get(0));
                for (int i = 1; i < groupByColumns.size(); i++) {
                    emit(", $C", groupByColumns.get(i));
                }
            });
        }

        // HAVING
        Condition havingCondition = query.getHaving();
        if (havingCondition != null) {
            emitNewLineOrSpace(inline);
            builderContext.buildStep(SqlClause.HAVING_CLAUSE, () -> emit("$K $B", HAVING, havingCondition));
        }

        // ORDER BY
        List<OrderColumn> orderByColumns = query.getOrderBy();
        if (!orderByColumns.isEmpty()) {
            emitNewLineOrSpace(inline);
            builderContext.buildStep(SqlClause.ORDER_BY_CLAUSE, () -> {
                emit("$K $O", ORDER_BY, orderByColumns.get(0));
                for (int i = 1; i < orderByColumns.size(); i++) {
                    emit(", $O", orderByColumns.get(i));
                }
            });
        }

        // LIMIT
        Object limit = query.getLimit();
        if (limit != null) {
            emitNewLineOrSpace(inline);
            builderContext.buildStep(SqlClause.LIMIT_CLAUSE, () -> emit("$K $C", LIMIT, limit));
        }

        // OFFSET
        Object offset = query.getOffset();
        if (offset != null) {
            emitNewLineOrSpace(inline);
            builderContext.buildStep(SqlClause.OFFSET_CLAUSE, () -> emit("$K $C", OFFSET, offset));
        }

        // FOR UPDATE
        boolean forUpdate = query.isForUpdate();
        if (forUpdate) {
            emitNewLineOrSpace(inline);
            emit("$K", FOR_UPDATE);
        }

        // NO WAIT
        boolean noWait = query.isNoWait();
        if (noWait) {
            emitNewLineOrSpace(inline);
            emit("$K", NO_WAIT);
        }

        // WAIT
        Integer secondsOfWait = query.getWait();
        if (secondsOfWait != null) {
            emitNewLineOrSpace(inline);
            builderContext.buildStep(SqlClause.WAIT_CLAUSE, () -> emit("$K $L", WAIT, secondsOfWait));
        }
    }


    protected void emitJoinInfos(List<JoinInfo> joinInfos, boolean inline) throws IOException {
        for (JoinInfo joinInfo : joinInfos) {
            emitNewLineOrSpace(inline);
            builderContext.buildStep(SqlClause.JOIN_CLAUSE, () -> emit("$K $T", joinInfo.getJoinType(), joinInfo.getJoinTable()));

            Condition joinCondition = joinInfo.getCondition();
            if (joinCondition != null) {
                emitNewLineOrSpace(inline);
                builderContext.buildStep(SqlClause.ON_CLAUSE, () -> emit("$K $B", ON, joinCondition));
            }
        }
    }

    protected void emitWhere(Condition whereCondition, boolean inline) throws IOException {
        if (whereCondition != null) {
            emitNewLineOrSpace(inline);
            builderContext.buildStep(SqlClause.WHERE_CLAUSE, () -> emit("$K $B", WHERE, whereCondition));
        }
    }

    @Override
    protected void emit(Keyword keyword, boolean isBlank) throws IOException {
        String output = keyword.toSql(configuration);
        if (isBlank) {
            int length = output.length();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length; i++) {
                sb.append(" ");
            }
            emit(sb.toString());
        } else {
            emit(output);
        }
    }


    protected void emitValueColumns(List<ValueColumn> valueColumns) throws IOException {
        argNotEmpty(valueColumns, "valueColumns");
        emit("(");
        emit("$C", valueColumns.get(0));
        for (int i = 1; i < valueColumns.size(); i++) {
            emit(", $C", valueColumns.get(i));
        }
        emit(")");
    }

    protected void emitSourceColumn(SqlColumn sqlColumn) throws IOException {
        // PhysicalColumn
        if (sqlColumn instanceof PhysicalColumn) {
            PhysicalColumn physicalColumn = (PhysicalColumn) sqlColumn;
            emit("$T.$L", physicalColumn.getTable(), physicalColumn.getColumnName());
            return;
        }

        if (sqlColumn instanceof NamedColumn) {
            NamedColumn namedColumn = (NamedColumn) sqlColumn;
            if (StringHelper.isNotBlank(namedColumn.getTableName())) {
                emit("$L.", namedColumn.getTableName());
            }
            emit(namedColumn.getColumnName());
            return;
        }

        if (sqlColumn instanceof ScalarColumn) {
            emitValue(((ScalarColumn) sqlColumn).getValue());
            return;
        }

        if (sqlColumn instanceof ValueColumn) {
            emitValue(((ValueColumn) sqlColumn).getValue());
            return;
        }

        if (sqlColumn instanceof SelectFunctionColumn) {
            builderContext.buildStep(SqlClause.SELECT_FUNCTION_COLUMN_CLAUSE, () -> emitFunction(((SelectFunctionColumn) sqlColumn).getFunction()));
            return;
        }

        if (sqlColumn instanceof SubqueryColumn) {
            emit("(");
            emitSelect(((SubqueryColumn) sqlColumn).getSelect().getSelectQuery(), true);
            emit(")");
            return;
        }

        if (sqlColumn instanceof SelectExpressionColumn) {
            builderContext.buildStep(SqlClause.SELECT_EXPRESSION_COLUMN_CLAUSE, () -> emitExpression(((SelectExpressionColumn) sqlColumn).getExpression()));
            return;
        }

        if (sqlColumn instanceof CaseWhenColumn) {
            builderContext.buildStep(SqlClause.CASE_WHEN_CLAUSE, () -> {
                CaseWhenQuery caseWhen = ((CaseWhenColumn) sqlColumn).getCaseWhen().getCaseWhenQuery();
                emit("($K ", CASE);
                if (caseWhen.getCase() != null) {
                    emit("$C ", caseWhen.getCase());
                }
                for (WhenTenPair whenThen : caseWhen.getWhenThenPairs()) {
                    if (whenThen.getWhenColumn() != null) {
                        emit("$K $C $K $C ", WHEN, whenThen.getWhenColumn(), THEN, whenThen.getThen());
                    } else {
                        emit("$K $B $K $C ", WHEN, whenThen.getWhenCondition(), THEN, whenThen.getThen());
                    }
                }
                if (caseWhen.getElse() != null) {
                    emit("$K $C ", ELSE, caseWhen.getElse());
                }
                emit("$K)", END);
            });
            return;
        }

        throw new UnsupportedOperationException(String.format("Unsupported SqlColumn Class: %s", sqlColumn.getClass().getCanonicalName()));
    }

    protected void emit(OrderColumn orderColumn) throws IOException {
        SelectColumn selectColumn = orderColumn.getSelectColumn();
        if (selectColumn instanceof AliasedSelectColumn) {
            AliasedSelectColumn<?> aliasedSelectColumn = (AliasedSelectColumn<?>) selectColumn;
            if (aliasedSelectColumn.getSourceColumn() instanceof NamedColumn) {
                emitSourceColumn(aliasedSelectColumn.getSourceColumn());
            } else {
                emit("$L", aliasedSelectColumn.getAlias().getName());
            }
        } else if (selectColumn instanceof NamedColumn) {
            emitSourceColumn(selectColumn);
        } else {
            throw new IllegalArgumentException(String.format("The selectColumn Class (%s) without alias name can't be used in order by clause.", selectColumn.getClass().getCanonicalName()));
        }
        if (orderColumn.getSortType() != SortType.DEFAULT) {
            emit(" $K", orderColumn.getSortType());
        }
    }

    @Override
    protected void emit(SqlColumn sqlColumn) throws IOException {
        if (sqlColumn instanceof AliasedSelectColumn) {
            AliasedSelectColumn<?> aliasedSelectColumn = (AliasedSelectColumn<?>) sqlColumn;

            SqlClause sqlClause = builderContext.getBuildStack().peek();
            assert (sqlClause != null);
            boolean outputName = sqlClause.outputColumnName();
            boolean outputAlias = sqlClause.outputColumnAlias();
            if (!outputName && !outputAlias) {
                throw new IllegalArgumentException("The parameter outputName and outputAlias can't be false in the same time.");
            }

            if (outputName) {
                emitSourceColumn(aliasedSelectColumn.getSourceColumn());
                if (outputAlias) {
                    emit(" $K $L", AS, String.format("\"%s\"", aliasedSelectColumn.getAlias().getName()));
                }
            } else {
                emit("$L", aliasedSelectColumn.getAlias().getName());
            }
        } else {
            emitSourceColumn(sqlColumn);
        }
    }


    protected void emit(SqlTable sqlTable) throws IOException {
        if (sqlTable instanceof AliasedSqlTable) {
            AliasedSqlTable<?> aliasedSqlTable = (AliasedSqlTable<?>) sqlTable;
            emitSourceTable(aliasedSqlTable.getSourceTable());
            emit(" $L", aliasedSqlTable.getAlias().getName());
        } else {
            emitSourceTable(sqlTable);
        }
    }

    protected void emitSourceTable(SqlTable sqlTable) throws IOException {
        /*
          TODO: Support Schema
         */
        if (sqlTable instanceof PhysicalTable) {
            PhysicalTable t = (PhysicalTable) sqlTable;
            String schema = StringHelper.isBlank(t.getSchema()) ? configuration.getDefaultSchema() : t.getSchema();
            if (StringHelper.isBlank(schema)) {
                emit(t.getTableName());
            } else {
                emit("$L.$L", schema, t.getTableName());
            }
            return;
        }
        if (sqlTable instanceof SubqueryTable) {
            emit("(");
            emitSelect(((SubqueryTable) sqlTable).getSelect().getSelectQuery(), true);
            emit(")");
            return;
        }
        throw new IllegalArgumentException(String.format("Unsupported Source SqlTable Class: %s", sqlTable.getClass().getCanonicalName()));
    }


    @Override
    protected void emit(Condition condition) throws IOException {
        if (condition instanceof CompareCondition) {
            CompareCondition compareCondition = (CompareCondition) condition;
            emit("$C $K $C", compareCondition.getLeftColumn(), compareCondition.getComparator(), compareCondition.getRightColumn());
            return;
        }
        if (condition instanceof IsNullCondition) {
            emit("$C $K", ((IsNullCondition) condition).getSqlColumn(), IS_NULL);
            return;
        }
        if (condition instanceof IsNotNullCondition) {
            emit("$C $K", ((IsNotNullCondition) condition).getSqlColumn(), IS_NOT_NULL);
            return;
        }
        if (condition instanceof BetweenCondition) {
            BetweenCondition betweenCondition = (BetweenCondition) condition;
            emit("$C", betweenCondition.getSqlColumn());
            emit(" $K $C $K $C", BETWEEN, betweenCondition.getMinValue(), AND, betweenCondition.getMaxValue());
            return;
        }
        if (condition instanceof NotCondition) {
            emit("$K $B", NOT, ((NotCondition) condition).getOriginalCondition());
            return;
        }
        if (condition instanceof ExistsCondition) {
            emit("$K (", EXISTS);
            emitSelect(((ExistsCondition) condition).getSelect().getSelectQuery(), true);
            emit(")");
            return;
        }
        if (condition instanceof CombinedCondition) {
            // TODO: 最外围的combinedCondition不需要括号
            emit("(");
            CombinedCondition combinedCondition = (CombinedCondition) condition;
            emit("$B", combinedCondition.getCondition());
            for (ConnectCondition connectCondition : combinedCondition.getConnectConnections()) {
                emit(" $K $B", connectCondition.getConnector(), connectCondition.getCondition());
            }
            emit(")");
            return;
        }
        if (condition instanceof EmptyCondition) {
            emit(" ");
            return;
        }
        throw new UnsupportedEncodingException(String.format("Unsupported Condition Type: %s", condition.getClass()));
    }

    protected void emitExpression(Expression expression) throws IOException {
        emit("($C $L $C)", expression.getLhs(), expression.getOperator().getSignal(configuration), expression.getRhs());
    }


    protected void emitFunction(Function function) throws IOException {
        emit("$K(", function.getFunctionName());
        SqlColumn[] params = function.getParams();
        if (params.length >= 1) {
            emit("$C", params[0]);
        }
        for (int i = 1; i < params.length; i++) {
            emit(", $C", params[i]);
        }
        emit(")");
    }

    @SuppressWarnings("unchecked")
    protected void emitValue(Object value) throws IOException {
        if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            if (length <= 0) {
                throw new IllegalArgumentException("The array must not be empty.");
            }
            emitSingleValue(Array.get(value, 0));
            for (int i = 1; i < length; i++) {
                emitAndIndent(", ");
                emitSingleValue(Array.get(value, i));
            }
        } else if (value instanceof Collection) {
            Collection<Object> collection = (Collection<Object>) value;
            if (collection.isEmpty()) {
                throw new IllegalArgumentException("The collection must not be empty.");
            }
            Iterator<Object> it = collection.iterator();
            int index = 0;
            while (it.hasNext()) {
                if (index == 0) {
                    emitSingleValue(it.next());
                } else {
                    emitAndIndent(", ");
                    emitSingleValue(it.next());
                }
                index++;
            }
        } else {
            emitSingleValue(value);
        }
    }

    protected void emitSingleValue(Object value) throws IOException {
        if (value instanceof Number) {
            emitNumber((Number) value);
        } else if (value instanceof CharSequence) {
            emitString(value.toString());
        } else if (value instanceof NamedParameter) {
            emitNamedParameter((NamedParameter) value);
        } else if (value instanceof LocalDateTime) {
            emitLocalDateTime((LocalDateTime) value);
        } else if (value instanceof LocalDate) {
            emitLocalDate((LocalDate) value);
        } else if (value instanceof LocalTime) {
            emitLocalTime((LocalTime) value);
        } else if (value instanceof NullValue) {
            emit("null");
        } else if (value instanceof EmptyValue) {
            emit("''");
        } else {
            throw new UnsupportedOperationException(String.format("The type(%s) of single value is not supported by database.", value.getClass().getCanonicalName()));
        }
    }
}
