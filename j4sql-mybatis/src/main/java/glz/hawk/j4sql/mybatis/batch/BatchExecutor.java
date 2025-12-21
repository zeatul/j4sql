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

package glz.hawk.j4sql.mybatis.batch;

import org.apache.ibatis.executor.BatchResult;
import org.mybatis.spring.SqlSessionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static glz.hawkframework.core.support.ArgumentSupport.*;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class BatchExecutor {

    private final SqlSessionTemplate batchSqlSessionTemplate;

    public BatchExecutor(SqlSessionTemplate batchSqlSessionTemplate) {
        this.batchSqlSessionTemplate = argNotNull(batchSqlSessionTemplate, "batchSqlSessionTemplate");
    }

    public <M, T> void execute(Class<M> mapperClass, List<T> dataList, int batchSize, BiConsumer<M, T> operation) {
        execute(mapperClass, dataList, batchSize, operation, null);
    }

    public <M, T> void execute(Class<M> mapperClass, List<T> dataList, int batchSize, BiConsumer<M, T> operation, Consumer<BatchResult> batchResultConsumer) {
        argNotNull(mapperClass, "mapperClass");
        argNotEmpty(dataList, "dataList");
        argument(batchSize, b -> b > 0, b -> "The parameter['batchSize'] must be greater than 0.");
        argNotNull(operation, "operation");
        List<BatchResult> result = new ArrayList<>();
        M mapper = batchSqlSessionTemplate.getMapper(mapperClass);
        for (int i = 0; i < dataList.size(); i += batchSize) {
            List<T> chunk = dataList.subList(i, Math.min(i + batchSize, dataList.size()));
            for (T t : chunk) {
                operation.accept(mapper, t);
            }
            List<BatchResult> batchResultList = batchSqlSessionTemplate.flushStatements();
            if (batchResultConsumer != null) {
                batchResultList.forEach(batchResultConsumer);
            }
            batchSqlSessionTemplate.clearCache();
        }
    }
}
