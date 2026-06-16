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

package glz.hawk.j4sql.support.impl;

import glz.hawk.j4sql.support.Configuration;
import glz.hawk.j4sql.support.SqlDialect;

import static glz.hawkframework.core.support.ArgumentSupport.argNotBlank;
import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class DefaultConfiguration implements Configuration {

    private final SqlDialect dialect;
    private final String indent;
    private final String lineSeparator;
    private final String defaultSchema;
    private final boolean isInline;

    private DefaultConfiguration(Builder builder) {
        this.dialect = builder.dialect;
        this.indent = builder.indent;
        this.lineSeparator = builder.lineSeparator;
        this.defaultSchema = builder.defaultSchema;
        this.isInline = builder.isInline;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public SqlDialect getDialect() {
        return dialect;
    }

    @Override
    public String getIndent() {
        return indent;
    }

    @Override
    public String getLineSeparator() {
        return lineSeparator;
    }

    @Override
    public boolean isInline() {
        return isInline;
    }

    @Override
    public String getDefaultSchema() {
        return defaultSchema;
    }

    public static class Builder {
        private SqlDialect dialect = SqlDialect.DEFAULT;
        private String indent = "  ";
        private String lineSeparator = "\n";
        private String defaultSchema = null;
        private boolean isInline = false;

        private Builder() {
        }

        public Builder setDialect(SqlDialect dialect) {
            this.dialect = argNotNull(dialect, "dialect");
            return this;
        }

        public Builder setIndent(String indent) {
            this.indent = indent;
            return this;
        }

        public Builder setLineSeparator(String lineSeparator) {
            this.lineSeparator = lineSeparator;
            return this;
        }

        public Builder setDefaultSchema(String defaultSchema) {
            this.defaultSchema = defaultSchema == null ? null : argNotBlank(defaultSchema, "defaultSchema");
            return this;
        }

        public Builder setIsInline(boolean isInline) {
            this.isInline = isInline;
            return this;
        }

        public DefaultConfiguration build() {
            return new DefaultConfiguration(this);
        }
    }
}
