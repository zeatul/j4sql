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

package glz.hawk.j4sql.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public abstract class TypeUtils {

    public static boolean matchJdbcType(Object value) {
        // null 值总是有效的（可以使用 setNull）
        if (value == null) {
            return true;
        }

        // 字符串类型
        if (value instanceof String) {
            return true;
        }

        //基本类型
        if (value.getClass().isPrimitive()) {
            return true;
        }

        // 基本数据类型及其包装类
        if (value instanceof Integer || value instanceof Long || value instanceof Double || value instanceof Float || value instanceof Short || value instanceof Byte || value instanceof Boolean) {
            return true;
        }



        // 日期和时间类型
        if (value instanceof java.util.Date) {
            return true;
        }

        if (value instanceof LocalDate || value instanceof LocalDateTime || value instanceof LocalTime) {
            return true;
        }

        // 大数字类型
        if (value instanceof java.math.BigDecimal) {
            return true;
        }

        // 二进制数据
        if (value instanceof byte[]) {
            return true;
        }

        // 流类型
        if (value instanceof java.io.InputStream ||
            value instanceof java.io.Reader) {
            return true;
        }

        // SQL 特定类型
        if (value instanceof java.sql.Array || value instanceof java.sql.Blob || value instanceof java.sql.Clob || value instanceof java.sql.Ref || value instanceof java.sql.Struct || value instanceof java.sql.SQLXML || value instanceof java.sql.RowId) {
            return true;
        }

        // 其他可能支持的类型取决于具体的 JDBC 驱动程序
        return false;
    }
}
