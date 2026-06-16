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

import glz.hawk.j4sql.support.ScalarColumn;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Zhang Peng
 */
public class DefaultScalarColumn extends AbstractSelectColumn implements ScalarColumn {

    private final Object value;
    private final Class<?> valueClass;

    private DefaultScalarColumn(Object value) {
        this.value = argNotNull(value, "value");
        this.valueClass = value.getClass();
    }

    private DefaultScalarColumn() {
        this.value = NullValue.INSTANCE;
        this.valueClass = NullValue.class;
    }


    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public Class<?> getValueClass() {
        return valueClass;
    }



    public static DefaultScalarColumn of(Long value) {
        return new DefaultScalarColumn(value);
    }

    public static DefaultScalarColumn of(long value) {
        return new DefaultScalarColumn(value);
    }

    public static DefaultScalarColumn of(Integer value) {
        return new DefaultScalarColumn(value);
    }

    public static DefaultScalarColumn of(int value) {
        return new DefaultScalarColumn(value);
    }

    public static DefaultScalarColumn ofNull() {
        return new DefaultScalarColumn(NullValue.INSTANCE);
    }

    public static DefaultScalarColumn ofEmpty() {
        return new DefaultScalarColumn(EmptyValue.INSTANCE);
    }

    public static DefaultScalarColumn of(String value) {
        return new DefaultScalarColumn(value);
    }

    public static DefaultScalarColumn of(Short value) {
        return new DefaultScalarColumn(value);
    }

    public static DefaultScalarColumn of(short value) {
        return new DefaultScalarColumn(value);
    }

    public static DefaultScalarColumn of(Byte value) {
        return new DefaultScalarColumn(value);
    }

    public static DefaultScalarColumn of(byte value) {
        return new DefaultScalarColumn(value);
    }

    public static DefaultScalarColumn of(LocalDateTime value) {
        return new DefaultScalarColumn(value);
    }

    public static DefaultScalarColumn of(LocalDate value) {
        return new DefaultScalarColumn(value);
    }

    public static DefaultScalarColumn of(LocalTime value) {
        return new DefaultScalarColumn(value);
    }

    public static DefaultScalarColumn of(BigDecimal value) {
        return new DefaultScalarColumn(value);
    }

    public static DefaultScalarColumn of(BigInteger value) {
        return new DefaultScalarColumn(value);
    }

    public static DefaultScalarColumn of(Character value) {
        return new DefaultScalarColumn(value);
    }
}
