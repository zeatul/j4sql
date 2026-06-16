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

    /**
     *
     * @param mapperClass  the class of mapper
     * @param dataList     the data for batch execution
     * @param batchSize    the size of batch
     * @param sqlOperation the sqlOperation for executing one SQL
     * @param <M>          Mapper
     * @param <T>          Parameters
     */
    public <M, T> void execute(Class<M> mapperClass, List<T> dataList, int batchSize, BiConsumer<M, T> sqlOperation) {
        execute(mapperClass, dataList, batchSize, sqlOperation, EmptyBatchResultConsumer.INSTANCE);
    }

    /**
     *
     * @param mapperClass         the class of mapper
     * @param dataList            the data for batch execution
     * @param batchSize           the size of batch
     * @param sqlOperation        the sqlOperation for executing one SQL
     * @param batchResultConsumer the consumer for batch execution result, The first parameter is the number of SQL statements that have been executed before the current batch,
     *                            the second parameter is current batchResult.
     * @param <M>                 Mapper
     * @param <T>                 Parameters
     */
    public <M, T> void execute(Class<M> mapperClass, List<T> dataList, int batchSize, BiConsumer<M, T> sqlOperation, BiConsumer<Integer, BatchResult> batchResultConsumer) {
        argNotNull(mapperClass, "mapperClass");
        argNotEmpty(dataList, "dataList");
        argument(batchSize, b -> b > 0, b -> "The parameter['batchSize'] must be greater than 0.");
        argNotNull(sqlOperation, "sqlOperation");
        argNotNull(batchResultConsumer, "batchResultConsumer");
        List<BatchResult> result = new ArrayList<>();
        M mapper = batchSqlSessionTemplate.getMapper(mapperClass);
        for (int i = 0; i < dataList.size(); i += batchSize) {
            List<T> chunk = dataList.subList(i, Math.min(i + batchSize, dataList.size()));
            for (T t : chunk) {
                sqlOperation.accept(mapper, t);
            }
            List<BatchResult> batchResultList = batchSqlSessionTemplate.flushStatements();
            final int finalI = i;
            batchResultList.forEach(batchResult -> {
                batchResultConsumer.accept(finalI * batchSize, batchResult);
            });
            batchSqlSessionTemplate.clearCache();
        }
    }
}
