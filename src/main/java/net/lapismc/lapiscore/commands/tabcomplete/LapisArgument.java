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

package net.lapismc.lapiscore.commands.tabcomplete;

import java.util.List;

/**
 * A class to represent arguments in a command
 * <p>
 * w/ children arguments that might follow this argument in a command
 */
public abstract class LapisArgument {

    private final String argument;
    private final LapisArgument parent;
    private final List<LapisArgument> children;

    public LapisArgument(String argument, List<LapisArgument> children, LapisArgument parent) {
        this.argument = argument;
        this.children = children;
        this.parent = parent;
    }

    /**
     * Override this to implement your own system of calculating children if you dont add them on initialization
     */
    public void calculateChildren() {
        //By default this does nothing to leave the list in the default state
    }

    /**
     * Get the strings that this argument represents
     *
     * @return the strings for this arg
     */
    public String getArgument() {
        return argument;
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
     *
     * @return A {@link List} of children arguments
     */
    public List<LapisArgument> getChildren() {
        calculateChildren();
        return children;
    }

    /**
     * Get the parent argument of this argument
     * Useful for getting the player or something from the previous argument
     *
     * @return The parent argument, that is the argument that should precede this one in a command
     */
    public LapisArgument getParent() {
        return parent;
    }

}
