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

package glz.hawk.j4sql.mybatis.writer;

import glz.hawk.j4sql.mybatis.sql.MybatisParam;
import glz.hawk.j4sql.support.Configuration;
import glz.hawk.j4sql.support.NamedParameter;
import glz.hawk.j4sql.support.impl.SqlClause;
import glz.hawk.j4sql.writer.impl.SqlWriterImpl;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class MybatisSqlWriterImpl extends SqlWriterImpl<MybatisBuilderContext> {


    public MybatisSqlWriterImpl(Appendable out, Configuration configuration, MybatisBuilderContext builderContext) {
        super(out, configuration, builderContext);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void emitValue(Object value) throws IOException {
        SqlClause sqlClause = builderContext.getBuildStack().peek();
        assert (sqlClause != null);
        if (!sqlClause.parameterable()) {
            super.emitValue(value);
            return;
        }

        if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            if (length <= 0) {
                throw new IllegalArgumentException("The array must not be empty.");
            }
            emit("(");
            String parameterName = builderContext.nextParameterName();
            builderContext.addParameter(parameterName, value);
            emitElementSqlParameter(parameterName, 0, Array.get(value, 0).getClass());
            for (int i = 1; i < length; i++) {
                emitAndIndent(", ");
                emitElementSqlParameter(parameterName, i, Array.get(value, i).getClass());
            }
            emit(")");
        } else if (value instanceof Collection) {
            Collection<Object> collection = (Collection<Object>) value;
            if (collection.isEmpty()) {
                throw new IllegalArgumentException("The collection must not be empty.");
            }
            emit("(");
            String parameterName = builderContext.nextParameterName();
            builderContext.addParameter(parameterName, value);
            Iterator<Object> it = collection.iterator();
            int index = 0;
            while (it.hasNext()) {
                if (index == 0) {
                    emitElementSqlParameter(parameterName, index, it.next().getClass());
                } else {
                    emitAndIndent(", ");
                    emitElementSqlParameter(parameterName, index, it.next().getClass());
                }
                index++;
            }
            emit(")");
        } else if (value instanceof NamedParameter) {
            emitNamedParameter((NamedParameter) value);
        } else {
            emitSingleValueSqlParameter(value);
        }
    }


    protected void emitSingleValueSqlParameter(Object value) throws IOException {
        if (value instanceof MybatisParam) {
            emitSourceColumn((MybatisParam) value);
        } else {
            String parameterName = builderContext.nextParameterName();
            builderContext.addParameter(parameterName, value);
            emitSourceColumn(MybatisParam.builder(parameterName, value.getClass()).setPrefix(builderContext.getParamPrefix()).build());
        }
    }

    protected void emitElementSqlParameter(String parameterName, int index, Class<?> valueClass) throws IOException {
        emitSourceColumn(MybatisParam.builder(String.format("%s[%d]", parameterName, index), valueClass).setPrefix(builderContext.getParamPrefix()).build());
    }
}
