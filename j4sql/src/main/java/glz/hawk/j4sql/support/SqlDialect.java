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
package glz.hawk.j4sql.support;

import glz.hawkframework.core.helper.MapHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;
import static glz.hawkframework.core.support.StateSupport.stateNotNull;

/**
 * This enum is responsible for
 *
 * @author Hawk
 */
public enum SqlDialect {
    ORACLE, MYSQL, DEFAULT;

    private final static Map<String, SqlDialect> nameMap = MapHelper.<String, SqlDialect>builder().put(ORACLE.name(), ORACLE).put(MYSQL.name(), MYSQL).put(DEFAULT.name(), DEFAULT).buildHashMap();

    public static @Nullable SqlDialect fromName(@Nullable String name) {
        if (name == null) return null;
        return nameMap.get(name);
    }

    public static @Nonnull SqlDialect parseBysName(@Nonnull String name) {
        return stateNotNull(fromName(argNotNull(name)), () -> String.format("Found no SqlDialect named %s", name));
    }
}
