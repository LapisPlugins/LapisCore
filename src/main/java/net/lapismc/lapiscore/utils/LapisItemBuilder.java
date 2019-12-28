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

package net.lapismc.lapiscore.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

public class LapisItemBuilder {

    Material mat;
    byte data = 0;
    String name = "";
    int amount = 1;

    public LapisItemBuilder(Material mat) {
        this.mat = mat;
    }

    public LapisItemBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public LapisItemBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public LapisItemBuilder woolColor(WoolColor color) {
        CompatibleMaterial compMat = CompatibleMaterial.matchXMaterial(color.name() + "_WOOL");
        mat = compMat.parseMaterial();
        data = compMat.getData();
        return this;
    }

    public ItemStack build() {
        ItemStack i = new ItemStack(mat);
        if (data != 0) {
            MaterialData matData = new MaterialData(mat);
            matData.setData(data);
            i.setData(matData);
        }
        ItemMeta meta = i.getItemMeta();
        if (!name.equals("")) {
            if (meta != null)
                meta.setDisplayName(name);
        }
        i.setItemMeta(meta);
        i.setAmount(amount);
        return i;
    }

    public enum WoolColor {
        WHITE, ORANGE, MAGENTA, LIGHT_BLUE, YELLOW, LIME, PINK, GRAY, LIGHT_GRAY, CYAN, PURPLE, BLUE, BROWN, GREEN, RED, BLACK
    }
}
