/*
 * Copyright (c) 2017, Miguel Gamboa
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package util;

import weather.model.WeatherInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * NaiveQueries provide utility methods that make queries over a sequence
 * of elements.
 * A sequence is considered an Iterable.
 *
 * @author Miguel Gamboa
 *         created on 14-03-2017
 */
public class NaiveQueries {

    /**
     * v1- Auxiliary method to filter cloudy days. Returns a new sequence with
     * WeatherInfo objects that were cloudy.
     * Eager approach.
     */
    public static Iterable<WeatherInfo> filterCloudy(Iterable<WeatherInfo> data) {
        List<WeatherInfo> res = new ArrayList<>();
        for (WeatherInfo item: data) {
            if(item.getDescription().toLowerCase().contains("cloud"))
                res.add(item);
        }
        return res;
    }

    public static Iterable<WeatherInfo> filterRainy(Iterable<WeatherInfo> data) {
        List<WeatherInfo> res = new ArrayList<>();
        for (WeatherInfo item: data) {
            if(item.getDescription().toLowerCase().contains("rain"))
                res.add(item);
        }
        return res;
    }

    /**
     * v2-
     */
    public static Iterable<WeatherInfo> filterDesc(Iterable<WeatherInfo> data, String query) {
        List<WeatherInfo> res = new ArrayList<>();
        for (WeatherInfo item: data) {
            if(item.getDescription().toLowerCase().contains(query))
                res.add(item);
        }
        return res;
    }

    /**
     * v3 -
     */
    public static Iterable<WeatherInfo> filter(Iterable<WeatherInfo> data, WeatherPredicate p) {
        List<WeatherInfo> res = new ArrayList<>();
        for (WeatherInfo item: data) {
            if(p.test(item))
                res.add(item);
        }
        return res;
    }

    public static int count(Iterable<WeatherInfo> data) {
        int size = 0;
        for (WeatherInfo item: data) {
            size++;
        }
        return size;
    }
}
