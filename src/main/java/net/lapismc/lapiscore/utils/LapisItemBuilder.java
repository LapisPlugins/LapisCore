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

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LapisItemBuilder {

    Material mat;
    String name = "";
    int amount = 1;
    OfflinePlayer owner;
    PotionType potionType;
    List<String> lore = new ArrayList<>();

    /**
     * Initialize a new item builder based on a material
     *
     * @param mat The material of the item you wish to make
     */
    public LapisItemBuilder(Material mat) {
        this.mat = mat;
    }

    /**
     * Initialize a new item builder with a player head
     *
     * @param offlinePlayer The owner of the head
     */
    public LapisItemBuilder(OfflinePlayer offlinePlayer) {
        mat = Material.PLAYER_HEAD;
        owner = offlinePlayer;
    }

    /**
     * Set the name of the item
     *
     * @param name The new name of the item
     * @return The new {@link LapisItemBuilder}
     */
    public LapisItemBuilder setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Set the amount for the resulting {@link ItemStack}
     *
     * @param amount The amount you wish to have
     * @return The new {@link LapisItemBuilder}
     */
    public LapisItemBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    /**
     * Add lore, this will be appended to any exiting lore
     *
     * @param lore The lore to add
     * @return The new {@link LapisItemBuilder}
     */
    public LapisItemBuilder addLore(String... lore) {
        this.lore.addAll(Arrays.asList(lore));
        return this;
    }

    /**
     * Set lore, this will overwrite any existing lore
     *
     * @param lore The lore for the item
     * @return the new {@link LapisItemBuilder}
     */
    public LapisItemBuilder setLore(String... lore) {
        this.lore.clear();
        this.lore.addAll(Arrays.asList(lore));
        return this;
    }

    /**
     * Set the material to wool of the given color
     *
     * @param color The color of wool you wish to have
     * @return The new {@link LapisItemBuilder}
     */
    public LapisItemBuilder setWoolColor(WoolColor color) {
        mat = Material.getMaterial(color.name() + "_WOOL");
        return this;
    }

    /**
     * Set the type of potion, e.g. regeneration, strength etc.
     *
     * @param potionType The potion type to apply
     * @return the new {@link LapisItemBuilder}
     */
    public LapisItemBuilder setPotionType(PotionType potionType) {
        this.potionType = potionType;
        return this;
    }

    /**
     * Build the item based on the set variables in the builder
     *
     * @return the ItemStack requested
     */
    public ItemStack build() {
        ItemStack i = new ItemStack(mat);
        ItemMeta meta = i.getItemMeta();
        if (meta != null) {
            if (name != null && !name.equals("")) {
                meta.setDisplayName(name);
            }
            if (owner != null && meta instanceof SkullMeta) {
                ((SkullMeta) meta).setOwningPlayer(owner);
            }
            if (potionType != null && meta instanceof PotionMeta) {
                ((PotionMeta) meta).setBasePotionData(new PotionData(potionType));
            }
            if (!lore.isEmpty()) {
                meta.setLore(lore);
            }
            i.setItemMeta(meta);
        }
        i.setAmount(amount);
        return i;
    }

    public enum WoolColor {
        WHITE, ORANGE, MAGENTA, LIGHT_BLUE, YELLOW, LIME, PINK, GRAY, LIGHT_GRAY, CYAN, PURPLE, BLUE, BROWN, GREEN, RED, BLACK
    }
}
