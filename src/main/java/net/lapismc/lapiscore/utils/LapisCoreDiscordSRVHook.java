/*
 * Copyright 2025 Benjamin Martin
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

import github.scarsz.discordsrv.DiscordSRV;

public class LapisCoreDiscordSRVHook {

    /**
     * Send a message to Discord via the DiscordSRV API
     *
     * @param channelName The name of the DiscordSRV channel to push to, defaults to global if empty or null
     * @param msg         The message to be sent
     */
    public static void pushMessageToDiscord(String channelName, String msg) {
        if (channelName == null || channelName.isEmpty()) {
            channelName = "global";
        }
        DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channelName).sendMessage(msg);
    }

}
