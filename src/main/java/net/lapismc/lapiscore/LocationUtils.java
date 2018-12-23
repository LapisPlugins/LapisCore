/*
 * Copyright 2018 Benjamin Martin
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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * A util for parsing Location objects to and from String objects
 * Primarily used for storing locations in configs
 */
public class LocationUtils {

    /**
     * Creates a config safe String from a Location
     *
     * @param loc The location you wish to parse
     * @return Returns a string that holds all the information of the Location provided
     */
    public String parseLocationToString(Location loc) {
        return loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + ","
                + loc.getZ() + "," + loc.getPitch() + "," + loc.getYaw();
    }

    /**
     * Creates a Location object from a String
     *
     * @param s The String created by the {@link #parseLocationToString(Location)} method
     * @return Returns a Location object, Null if the String is null or "" or if the world doesn't exist
     */
    public Location parseStringToLocation(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        Location loc;
        String[] args = s.split(",");
        String worldName = args[0];
        if (Bukkit.getServer().getWorld(worldName) == null) {
            return null;
        }
        World world = Bukkit.getServer().getWorld(worldName);
        try {
            float pitch = Float.parseFloat(args[4]);
            float yaw = Float.parseFloat(args[5]);
            double x = Double.parseDouble(args[1]);
            double y = Double.parseDouble(args[2]);
            double z = Double.parseDouble(args[3]);
            loc = new Location(world, x, y, z, yaw, pitch);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
        return loc;
    }

}
