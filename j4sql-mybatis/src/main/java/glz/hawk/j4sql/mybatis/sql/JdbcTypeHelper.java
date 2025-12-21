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

import org.apache.ibatis.type.JdbcType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class JdbcTypeHelper {

    private static final Set<Class<?>> NUMERIC_TYPES = new HashSet<>();

    static {
        // 基本数字类型
        NUMERIC_TYPES.add(byte.class);
        NUMERIC_TYPES.add(short.class);
        NUMERIC_TYPES.add(int.class);
        NUMERIC_TYPES.add(long.class);
        NUMERIC_TYPES.add(float.class);
        NUMERIC_TYPES.add(double.class);
        // 数字包装类
        NUMERIC_TYPES.add(Byte.class);
        NUMERIC_TYPES.add(Short.class);
        NUMERIC_TYPES.add(Integer.class);
        NUMERIC_TYPES.add(Long.class);
        NUMERIC_TYPES.add(Float.class);
        NUMERIC_TYPES.add(Double.class);

        //
        NUMERIC_TYPES.add(BigDecimal.class);
        NUMERIC_TYPES.add(BigInteger.class);
    }


    public static JdbcType toJdbcTye(Class<?> clazz) {
        argNotNull(clazz, "clazz");
        if (String.class.isAssignableFrom(clazz)) {
            return JdbcType.VARCHAR;
        } else if (NUMERIC_TYPES.contains(clazz) || Number.class.isAssignableFrom(clazz)) {
            return JdbcType.DECIMAL;
        } else if (LocalDateTime.class.isAssignableFrom(clazz) || java.util.Date.class.isAssignableFrom(clazz)) {
            return JdbcType.TIMESTAMP;
        } else if (LocalDate.class.isAssignableFrom(clazz) || java.sql.Date.class.isAssignableFrom(clazz)) {
            return JdbcType.DATE;
        } else if (LocalTime.class.isAssignableFrom(clazz) || java.sql.Time.class.isAssignableFrom(clazz)) {
            return JdbcType.TIME;
        } else if (clazz == byte[].class || clazz == Byte[].class) {
            return JdbcType.BLOB;
        } else {
            throw new IllegalArgumentException(String.format("Found no JdbcType matched %s", clazz.getCanonicalName()));
        }
    }
}
