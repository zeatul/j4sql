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

package glz.hawk.j4sql.mybatis.annotation;

import glz.hawk.jdesigner.spec.database.Table;
import glz.hawk.jdesigner.translator.Translator;

import javax.annotation.Nonnull;

import static glz.hawk.jdesigner.translator.convert.NameConverter.UPPER_UNDERSCORE_TO_UPPER_CAMEL;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class DefaultMapperClassNameTranslator implements Translator<Table, String> {
    @Nonnull
    @Override
    public String translate(@Nonnull Table table) {
        return UPPER_UNDERSCORE_TO_UPPER_CAMEL.convertTo(table.getName().toUpperCase()) + "Mapper";
    }
}
