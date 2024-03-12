/*
 * Copyright 2024 Benjamin Martin
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

import org.ocpsoft.prettytime.Duration;
import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.units.JustNow;
import org.ocpsoft.prettytime.units.Millisecond;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PrettyTimeUtil {

    private final PrettyTime prettyTime;

    public PrettyTimeUtil(String locale, boolean removeJustNow) {
        Locale loc = new Locale(locale);
        prettyTime = new PrettyTime(loc);
        if (removeJustNow)
            prettyTime.removeUnit(JustNow.class);
        prettyTime.removeUnit(Millisecond.class);
    }

    public PrettyTimeUtil(String locale) {
        this(locale, true);
    }

    public PrettyTimeUtil() {
        this("en", true);
    }

    /**
     * Get the time difference between now and the time epoch given as human-readable text
     * This will include relative terms like "ago" and "from now" at the end
     *
     * @param epoch The time to calculate from, use of {@link System#currentTimeMillis()} is recommended
     * @param units How many units should be displayed in the string, 2 is recommended
     * @return a String with the human-readable time difference between now and the time given
     */
    public String getRelativeTimeDifference(Long epoch, int units) {
        return prettyTime.format(reduceDurationList(prettyTime.calculatePreciseDuration(new Date(epoch)), units));
    }

    /**
     * Get the time difference between now and the time epoch given as human-readable text
     * Will not include relative terms at the end of the string
     *
     * @param epoch The time to calculate from, use of {@link System#currentTimeMillis()} is recommended
     * @param units How many units should be displayed in the string, 2 is recommended
     * @return a String with the human-readable time difference between now and the time given
     */
    public String getCleanTimeDifference(Long epoch, int units) {
        return prettyTime.formatDuration(reduceDurationList(prettyTime.calculatePreciseDuration(new Date(epoch)), units));
    }

    /**
     * Used to take a full duration list of any length and reduce it to the target number of units
     *
     * @param durationList        The list from {@link PrettyTime#calculatePreciseDuration(Date)}
     * @param numberOfUnitsToKeep The number of the largest units we should keep, 2 is recommended
     * @return a list of the largest units from the provided list of the length requested
     */
    public List<Duration> reduceDurationList(List<Duration> durationList, int numberOfUnitsToKeep) {
        while (durationList.size() > numberOfUnitsToKeep) {
            Duration smallest = null;
            for (Duration current : durationList) {
                if (smallest == null || smallest.getUnit().getMillisPerUnit() > current.getUnit().getMillisPerUnit()) {
                    smallest = current;
                }
            }
            durationList.remove(smallest);
        }
        return durationList;
    }

}
