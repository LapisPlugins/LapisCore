/*
 * Copyright 2019 Benjamin Martin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.lapismc.lapiscore;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class LapisCoreClassLoader {

    private static final Method ADD_URL_METHOD;

    static {
        Method addUrlMethod;
        try {
            addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addUrlMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
        ADD_URL_METHOD = addUrlMethod;
    }

    private LapisCorePlugin core;

    /**
     * Setup the class loader
     *
     * @param core The plugin that you wish to use to class load
     */
    public LapisCoreClassLoader(LapisCorePlugin core) {
        this.core = core;
    }

    /**
     * Load classes from a jar file
     *
     * @param file The jar file that you wish to load
     */
    public void loadClasses(final File file) {
        if (!file.getName().endsWith(".jar"))
            throw new IllegalArgumentException("The file to class load must be a .jar file");
        ClassLoader classLoader = core.getPluginClassLoader();
        if (classLoader instanceof URLClassLoader) {
            try {
                ADD_URL_METHOD.invoke(classLoader, file.toURI().toURL());
            } catch (IllegalAccessException | InvocationTargetException | MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

}
