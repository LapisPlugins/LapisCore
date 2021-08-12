/*
 * Copyright 2021 Benjamin Martin
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

package net.lapismc.lapiscore.permissions;

/**
 * This class is used to represent a setting that can be set for each permission
 */
public class LapisPermission {

    private final String name;
    private int defaultValue = 0;

    /**
     * Create a permission with the given name
     *
     * @param name This name will be used to find values in the config.yml
     */
    public LapisPermission(String name) {
        this.name = name;
    }

    /**
     * Overloads {@link #LapisPermission(String)} with a default value
     *
     * @param name         This name will be used to find values in the config.yml
     * @param defaultValue The value that this permission should default to, useful for complex permissions
     */
    public LapisPermission(String name, int defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    /**
     * Get the name of this permission
     *
     * @return Returns the name of this permission
     */
    public String getName() {
        return name;
    }

    /**
     * Get the default state for this permission, definined in the plugin implementing it
     * defaults to 0 if not set by plugin
     *
     * @return default value for permission
     */
    public int getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String toString() {
        return name;
    }

}
