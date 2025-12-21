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

import glz.hawk.jdesigner.extension.column.*;
import glz.hawkframework.dao.function.*;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * This class is responsible for
 *
 * @author Zhang Peng
 */
public class DefaultColumnFunctionMapSupplier implements Supplier<Map<Class<? extends Annotation>, Class<? extends ColumnFunction>>> {

    private final Map<Class<? extends Annotation>, Class<? extends ColumnFunction>> columnFunctionMap = new HashMap<>();

    public DefaultColumnFunctionMapSupplier(){
        columnFunctionMap.put(RecordId.class, RecordIdFunction.class);
        columnFunctionMap.put(RecordVersion.class, RecordVersionFunction.class);
        columnFunctionMap.put(RecordCreateDateTime.class, RecordCreateDateTimeFunction.class);
        columnFunctionMap.put(RecordCreateUserId.class, RecordCreateUserIdFunction.class);
        columnFunctionMap.put(RecordUpdateDateTime.class, RecordUpdateDateTimeFunction.class);
        columnFunctionMap.put(RecordUpdateUserId.class, RecordUpdateUserIdFunction.class);
    }

    @Override
    public Map<Class<? extends Annotation>, Class<? extends ColumnFunction>> get() {
        return Collections.unmodifiableMap(columnFunctionMap);
    }
}
