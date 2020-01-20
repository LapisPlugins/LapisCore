/*
 * Copyright 2020 Benjamin Martin
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

package net.lapismc.lapiscore.commands;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to represent arguments in a command
 * <p>
 * w/ children arguments that might follow this argument in a command
 */
public abstract class LapisArgument {

    private String argument;
    private List<LapisArgument> children;

    /**
     * Register this argument with no default children
     *
     * @param argument The string of the argument
     */
    public LapisArgument(String argument) {
        this(argument, new ArrayList<>());
    }

    /**
     * Register this argument with no children but add it as a child of parent
     * This initializer will add it's self as a child of the parent
     *
     * @param argument The string of the argument
     * @param parent   The parent of this argument
     */
    public LapisArgument(String argument, LapisArgument parent) {
        this(argument);
        parent.addChild(this);
    }

    /**
     * Register this argument with children pre-defined
     *
     * @param argument The string of the argument
     * @param children The children arguments of this argument
     */
    public LapisArgument(String argument, List<LapisArgument> children) {
        this.argument = argument;
        this.children = new ArrayList<>();
        this.children.addAll(children);
    }

    /**
     * Get the string that this argument represents
     *
     * @return the string for this arg
     */
    public String getArgument() {
        return argument;
    }

    /**
     * Override this to implement your own system of calculating children if you dont add them on initialization
     */
    public void calculateChildren() {

    }

    /**
     * Add a child argument to this argument
     *
     * @param argument The argument that should be added as a child
     */
    public void addChild(LapisArgument argument) {
        children.add(argument);
    }

    /**
     * Get the current children of this argument
     * You should probably run {@link #calculateChildren()} to generate the children
     *
     * @return A {@link List} of children arguments
     */
    public List<LapisArgument> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return argument;
    }
}
