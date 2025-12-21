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

package glz.hawk.j4sql.support;

/**
 * This class is responsible for
 * SqlTable可以是物理表,视图，或者一段查询结果形成的虚拟表
 *
 * @author Hawk
 */
public interface SqlTable {
    <T extends SqlTable> AliasedSqlTable<T> as(String aliasName);
}
