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

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Map;

import static glz.hawkframework.core.helper.FileReadHelper.streamOf;

/**
 * This class is responsible for
 *
 * @author Zhang Peng
 */
public abstract class YamlUtils {

    public static Map<String, String> readYaml(String resourcePath) {
        try (InputStream inputStream = streamOf(resourcePath)) {
            Yaml yaml = new Yaml();
            return yaml.loadAs(inputStream, Map.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
