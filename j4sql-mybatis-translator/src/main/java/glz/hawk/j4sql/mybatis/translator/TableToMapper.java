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

import glz.hawk.codepoet.java.AnnotationInstanceSpec;
import glz.hawk.codepoet.java.InterfaceSpec;
import glz.hawk.codepoet.java.JavaFile;
import glz.hawk.codepoet.java.type.ClassName;
import glz.hawk.codepoet.java.type.ParameterizedTypeName;
import glz.hawk.j4sql.mybatis.mapper.BaseDeleteMapper;
import glz.hawk.j4sql.mybatis.mapper.BaseInsertMapper;
import glz.hawk.j4sql.mybatis.mapper.BaseSelectMapper;
import glz.hawk.j4sql.mybatis.mapper.BaseUpdateMapper;
import glz.hawk.jdesigner.spec.base.Model;
import glz.hawk.jdesigner.spec.database.Table;
import glz.hawk.jdesigner.translator.Translator;
import org.apache.ibatis.annotations.Mapper;

import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class TableToMapper implements Translator<Table, JavaFile> {
    private final Translator<Model, String> mapperClassPackageTranslator;
    private final Translator<Table, String> mapperClassNameTranslator;
    private final Translator<Model, String> poClassPackageTranslator;
    private final Translator<Table, String> poClassNameTranslator;

    public TableToMapper(Translator<Model, String> mapperClassPackageTranslator, Translator<Table, String> mapperClassNameTranslator,
                         Translator<Model, String> poClassPackageTranslator, Translator<Table, String> poClassNameTranslator) {
        this.mapperClassPackageTranslator = argNotNull(mapperClassPackageTranslator, "mapperClassPackageTranslator");
        this.mapperClassNameTranslator = argNotNull(mapperClassNameTranslator, "mapperClassNameTranslator");
        this.poClassPackageTranslator = argNotNull(poClassPackageTranslator, "poClassPackageTranslator");
        this.poClassNameTranslator = argNotNull(poClassNameTranslator, "poClassNameTranslator");
    }

    @Nonnull
    @Override
    public JavaFile translate(@Nonnull Table table) {
        argNotNull(table, "table");
        String mapperClassName = mapperClassNameTranslator.translate(table);
        ClassName poClassName = poClassName(table);
        InterfaceSpec.Builder builder = InterfaceSpec.builder(mapperClassName)
            .addModifier(Modifier.PUBLIC)
            .addAnnotation(AnnotationInstanceSpec.builder(Mapper.class).build())
            .addSuperInterface(ParameterizedTypeName.of(BaseInsertMapper.class, poClassName))
            .addSuperInterface(BaseDeleteMapper.class)
            .addSuperInterface(BaseUpdateMapper.class)
            .addSuperInterface(ParameterizedTypeName.of(BaseSelectMapper.class, poClassName));
        return JavaFile.builder(mapperClassPackageTranslator.translate(table), builder.build()).build();
    }

    protected ClassName poClassName(Table table) {
        return ClassName.of(poClassPackageTranslator.translate(table), poClassNameTranslator.translate(table));
    }


}
