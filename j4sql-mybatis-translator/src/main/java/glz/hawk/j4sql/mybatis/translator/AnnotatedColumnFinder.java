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

package glz.hawk.j4sql.mybatis.translator;

import glz.hawk.jdesigner.spec.database.Column;
import glz.hawk.jdesigner.spec.database.Table;
import glz.hawk.jdesigner.translator.database.TableHelper;

import java.lang.annotation.Annotation;
import java.util.Optional;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Zhang Peng
 */
public class AnnotatedColumnFinder implements ColumnFinder {

    private final Class<? extends Annotation> annotationClass;

    public AnnotatedColumnFinder(Class<? extends Annotation> annotationClass) {
        this.annotationClass = argNotNull(annotationClass, "annotationClass");
    }

    @Override
    public Optional<Column> find(Table table) {
        argNotNull(table, "table");
        return TableHelper.findColumn(table, annotationClass);
    }
}
