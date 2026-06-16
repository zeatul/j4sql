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

import glz.hawk.codepoet.core.AbstractCodeWriter;
import glz.hawk.j4sql.condition.Condition;
import glz.hawk.j4sql.support.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public abstract class AbstractSqlCodeWriter extends AbstractCodeWriter<AbstractSqlCodeWriter> {

    protected AbstractSqlCodeWriter(Appendable out, String indent, String lineSeparator) {
        super(out, indent, lineSeparator);
    }

    protected AbstractSqlCodeWriter emit(boolean booleanExpression, String format, Object... args) throws IOException {
        if (booleanExpression) {
            return emit(SqlCodeBlock.of(format, args));
        }
        return this;
    }

    protected AbstractSqlCodeWriter emit(String format, Object... args) throws IOException {
        return emit(SqlCodeBlock.of(format, args));
    }

    protected abstract void emit(Keyword keyword, boolean isBlank) throws IOException;

    protected abstract void emit(SqlTable sqlTable) throws IOException;

    protected abstract void emit(SqlColumn sqlColumn) throws IOException;

    protected abstract void emit(OrderColumn orderColumn) throws IOException;

    protected abstract void emit(Condition condition) throws IOException;

    protected AbstractSqlCodeWriter emit(SqlCodeBlock codeBlock) throws IOException {
        int a = 0;
        for (String part : codeBlock.formatParts) {
            switch (part) {
                case "$k": // Keyword to a whitespace string with the same length
                    emit(((Keyword) codeBlock.args.get(a++)), true);
                    break;
                case "$K": // Keyword
                    emit(((Keyword) codeBlock.args.get(a++)), false);
                    break;
                case "$C": // SqlColumn for select clause
                    emit((SqlColumn) codeBlock.args.get(a++));
                    break;
                case "$O": // OrderColumn
                    emit((OrderColumn) codeBlock.args.get(a++));
                    break;
                case "$T": // SqlTable
                    emit((SqlTable) codeBlock.args.get(a++));
                    break;
                case "$B": // Condition
                    emit((Condition) codeBlock.args.get(a++));
                    break;
                case "$L": // Literal
                    emit(codeBlock.args.get(a++).toString());
                    break;
                case "$S": // String
                    emitString(codeBlock.args.get(a++).toString());
                    break;
                case "$N":
                    emit((String) codeBlock.args.get(a++));
                    throw new UnsupportedOperationException();
                case "$$":
                    emit("$");
                    break;
                case "$>":
                    indent();
                    break;
                case "$<":
                    unindent();
                    break;
                case "$R":
                    emitNewLine();
                    break;
                default:
                    emitAndIndent(part);
                    break;
            }
        }
        return this;
    }


    protected void emitNumber(Number value) throws IOException {
        emit(value.toString());
    }

    protected void emitString(String str) throws IOException {
        emit(stringLiteralWithSingleQuotes(str));
    }

    protected void emitNamedParameter(NamedParameter namedParameter) throws IOException {
        emit(namedParameter.getParameterName());
    }

    protected void emitLocalDateTime(LocalDateTime dateTime) throws IOException {
        emit("'$L'", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(dateTime));
    }

    protected void emitLocalDate(LocalDate date) throws IOException {
        emit("'$L'", DateTimeFormatter.ofPattern("yyyy-MM-dd").format(date));
    }

    protected void emitLocalTime(LocalTime time) throws IOException {
        emit("'$L'", DateTimeFormatter.ofPattern("HH:mm:ss").format(time));
    }

    protected void emitNewLineOrSpace(boolean inline) throws IOException {
        if (inline) {
            emit(" ");
        } else {
            emitNewLine();
        }
    }

    /**
     * 处理字符串中的单引号
     */
    protected String stringLiteralWithSingleQuotes(String value) {
        StringBuilder result = new StringBuilder(value.length() + 2);
        final char singleQuote = '\'';
        result.append(singleQuote);
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == singleQuote) {
                result.append(singleQuote).append(singleQuote);
            } else {
                result.append(c);
            }
        }
        result.append(singleQuote);
        return result.toString();
    }

}
