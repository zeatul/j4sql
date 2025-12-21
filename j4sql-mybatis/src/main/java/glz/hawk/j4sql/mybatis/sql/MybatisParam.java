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

package glz.hawk.j4sql.mybatis.sql;

import glz.hawkframework.core.helper.StringHelper;
import glz.hawk.j4sql.support.ValueColumn;
import glz.hawk.j4sql.support.impl.AbstractSqlColumn;
import glz.hawk.j4sql.support.impl.DefaultAliasedNamedColumn;
import glz.hawk.j4sql.support.impl.DefaultNamedParameter;
import glz.hawk.j4sql.support.NamedParameter;
import org.apache.ibatis.type.JdbcType;

import javax.annotation.Nonnull;

import static glz.hawkframework.core.support.ArgumentSupport.argNotBlank;
import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class MybatisParam extends AbstractSqlColumn implements ValueColumn {

    private final String paramName;
    private final JdbcType jdbcType;
    private final boolean wrapByForeach;

    private MybatisParam(Builder builder) {
        this.paramName = builder.paramName;
        this.jdbcType = builder.jdbcType;
        this.wrapByForeach = builder.wrapByForeach;
    }

    public static Builder builder(String paramName) {
        return new Builder(paramName);
    }

    public static Builder builder(String paramName, boolean wrapByForeach) {
        return new Builder(paramName).setWrapByForeach(wrapByForeach);
    }

    public static Builder builder(String paramName, Class<?> paramClass) {
        return new Builder(paramName).setJdbcType(paramClass);
    }

    public static Builder builder(String paramName, JdbcType jdbcType) {
        return new Builder(paramName).setJdbcType(jdbcType);
    }

    public static Builder builder(String paramName, int jdbcType) {
        return new Builder(paramName).setJdbcType(jdbcType);
    }

    public static Builder builder(DefaultAliasedNamedColumn column) {
        return new Builder(column);
    }

    public MybatisParam prefix(String prefix) {
        return builder(this.paramName).setJdbcType(this.jdbcType).setWrapByForeach(this.wrapByForeach).setPrefix(prefix).build();
    }

    @Nonnull
    @Override
    public NamedParameter getValue() {
        if (!wrapByForeach) {
            return new DefaultNamedParameter(jdbcType == null ? String.format("#{%s}", paramName) : String.format("#{%s,jdbcType=%s}", paramName, jdbcType.name()));
        } else {
            return new DefaultNamedParameter(String.format("<foreach collection=\"%s\" item=\"item\" open=\"(\" separator=\",\" close=\")\">#{item}</foreach>", paramName));
        }
    }

    public String getParamName() {
        return paramName;
    }

    public JdbcType getJdbcType() {
        return jdbcType;
    }

    public static class Builder {
        private String paramName;
        private JdbcType jdbcType;
        private boolean wrapByForeach = false;

        private Builder(String paramName) {
            this.paramName = argNotBlank(paramName, "paramName");
        }

        private Builder(DefaultAliasedNamedColumn column) {
            this.paramName = argNotNull(column, "column").getSourceColumn().getColumnName();
        }


        public Builder setWrapByForeach(boolean wrapByForeach) {
            this.wrapByForeach = wrapByForeach;
            return this;
        }

        public Builder setJdbcType(JdbcType jdbcType) {
            this.jdbcType = argNotNull(jdbcType, "jdbcType");
            return this;
        }

        public Builder setJdbcType(int jdbcType) {
            this.jdbcType = JdbcType.forCode(jdbcType);
            return this;
        }

        public Builder setJdbcType(Class<?> paramClass) {
            this.jdbcType = JdbcTypeHelper.toJdbcTye(argNotNull(paramClass, "paramClass"));
            return this;
        }

        public Builder setPrefix(String prefix) {
            if (StringHelper.isNotBlank(prefix)) {
                this.paramName = String.format("%s.%s", prefix, paramName);
            }
            return this;
        }

        public MybatisParam build() {
            return new MybatisParam(this);
        }
    }


}
