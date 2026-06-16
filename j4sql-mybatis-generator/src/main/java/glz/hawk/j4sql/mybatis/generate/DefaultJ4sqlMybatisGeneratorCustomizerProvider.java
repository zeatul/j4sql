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

package glz.hawk.j4sql.mybatis.generate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static glz.hawkframework.core.support.ArgumentSupport.argNotBlank;
import static glz.hawkframework.core.support.ArgumentSupport.argument;

/**
 * This class is responsible for
 *
 * @author Zhang Peng
 */
public class DefaultJ4sqlMybatisGeneratorCustomizerProvider implements Function<String, J4sqlMybatisGeneratorCustomizer> {

    private final Set<String> supportDialects = new HashSet<>(Arrays.asList("mysql", "oracle"));

    @Override
    public DefaultJ4sqlMybatisGeneratorCustomizer apply(String dialect) {
        return new DefaultJ4sqlMybatisGeneratorCustomizer(check(dialect));
    }

    protected String check(String dialect) {
        return argument(argNotBlank(dialect, "dialect"), supportDialects::contains, d -> String.format("Unsupported Dialect: %s", d));
    }
}
