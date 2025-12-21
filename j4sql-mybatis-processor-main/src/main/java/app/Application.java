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

package app;

import glz.hawk.jdesigner.builder.ClasspathModelWarehouseBuilder;
import glz.hawk.jdesigner.builder.ResourcePathIndexesWarehouseBuilder;
import glz.hawk.jdesigner.builder.SimpleModelWarehouseProducer;
import glz.hawk.jdesigner.builder.context.ClasspathBuilderContextImpl;
import glz.hawk.jdesigner.builder.resource.ClasspathResourceScannerImpl;
import glz.hawk.jdesigner.builder.resource.ResourcePathCollector;
import glz.hawk.jdesigner.spec.manager.ModelWarehouse;

import java.util.Arrays;
import java.util.List;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class Application {

    public static void main(String[] args) {
        String[] scanPackages = new String[]{"hawk.example.bookstore.designer.meta", "hawk.example.bookstore.designer.database"};
        ModelWarehouse modelWarehouse = buildModelWarehouse(scanPackages);
//        ModelWarehouse modelWarehouse = buildModelWarehouse(scanPackages,new ResourcePathCollector[]{new BookstoreResourcePathIndex()});
        modelWarehouse.printAll();
    }

    protected static ModelWarehouse buildModelWarehouse(String[] scanPackages) {
        ClasspathBuilderContextImpl builderContext = new ClasspathBuilderContextImpl();
        List<String> scanPackageList = Arrays.asList(scanPackages);
        SimpleModelWarehouseProducer modelWarehouseProducer = new SimpleModelWarehouseProducer();
        ClasspathResourceScannerImpl resourceScanner = new ClasspathResourceScannerImpl();
        ClasspathModelWarehouseBuilder builder = new ClasspathModelWarehouseBuilder(builderContext, modelWarehouseProducer, resourceScanner, scanPackageList);
        return builder.build();
    }

    protected static ModelWarehouse buildModelWarehouse(String[] scanPackages, ResourcePathCollector[] resourcePathCollectors) {
        ClasspathBuilderContextImpl builderContext = new ClasspathBuilderContextImpl();
        SimpleModelWarehouseProducer modelWarehouseProducer = new SimpleModelWarehouseProducer();
        ClasspathResourceScannerImpl resourceScanner = new ClasspathResourceScannerImpl();
        ResourcePathIndexesWarehouseBuilder builder = new ResourcePathIndexesWarehouseBuilder(Application.class.getClassLoader(), builderContext, modelWarehouseProducer, resourcePathCollectors, scanPackages);
        return builder.build();
    }
}
