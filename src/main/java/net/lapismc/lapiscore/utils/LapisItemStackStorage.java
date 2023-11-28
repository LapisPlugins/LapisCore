/*
 * Copyright 2023 Benjamin Martin
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

package net.lapismc.lapiscore.utils;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * A class for storing ItemStacks in yaml configs
 * This is designed to be used with whole inventories, e.g. a whole chest or a players whole inventory
 */
public class LapisItemStackStorage {

    /**
     * Save items into a yaml config at the given path
     * The config must still be saved before the items are stored
     *
     * @param yaml  The yaml config to save the items into
     * @param path  The path to save at
     * @param items The items to save
     */
    public void saveItems(YamlConfiguration yaml, String path, ItemStack[] items) {
        yaml.set(path, items);
    }

    /**
     * Load previously stored ItemStacks from a yaml config
     *
     * @param yaml The config file to load from
     * @param path The path that the items were stored at
     * @return an array of ItemStacks that were stored
     */
    public ItemStack[] loadItems(YamlConfiguration yaml, String path) {
        //Create a list to store the ItemStacks into
        List<ItemStack> items = new ArrayList<>();
        //Get the list from the config
        List<?> list = yaml.getList(path);
        //If the list is null, we return an empty list
        if (list == null)
            return new ItemStack[0];
        //Loop over the list
        for (Object obj : list) {
            //If it's an ItemStack, we add it to the list
            // Otherwise we add a null, this is necessary to keep spacing since empty slots are stored as nulls
            if (obj instanceof ItemStack)
                items.add((ItemStack) obj);
            else
                items.add(null);
        }
        //Convert the ArrayList to an array for returning
        return items.toArray(new ItemStack[0]);
    }

}
