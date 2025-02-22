/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hop.core.config.plugin;

import java.lang.annotation.*;

/**
 * This annotation signals to the plugin system that the class is a configuration plugin.
 *
 * @author matt
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConfigPlugin {
  String CATEGORY_CONFIG = "config";
  String CATEGORY_RUN = "run";
  String CATEGORY_SEARCH = "search";
  String CATEGORY_IMPORT = "import";
  String CATEGORY_SERVER = "server";

  String id();

  String description() default "";

  String category() default CATEGORY_CONFIG;
}
