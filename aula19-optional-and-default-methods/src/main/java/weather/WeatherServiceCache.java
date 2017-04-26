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

package weather;

import weather.data.WeatherWebApi;
import weather.model.WeatherInfo;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static util.queries.LazyQueries.containsAll;
import static util.queries.LazyQueries.iterate;
import static util.queries.LazyQueries.map;
import static util.queries.LazyQueries.peek;

/**
 * @author Miguel Gamboa
 *         created on 18-04-2017
 */
public class WeatherServiceCache extends WeatherService{

    public WeatherServiceCache(WeatherWebApi api) {
        super(api);
    }

    private final Map<Coords, Map<LocalDate, WeatherInfo>> cache = new HashMap<>();

    @Override
    public Iterable<WeatherInfo> pastWeather(double lat, double log, LocalDate from, LocalDate to) {
        Coords location = new Coords(lat, log);
        Map<LocalDate, WeatherInfo> past = getOrCreate(location, cache);
        Iterable<LocalDate> keys = iterate(from, to, prev -> prev.plusDays(1));
        return () -> {
            if (containsAll(past.keySet(), keys)) return map(keys, past::get).iterator();
            Iterable<WeatherInfo> values = super.pastWeather(lat, log, from, to);
            Iterator<LocalDate> keysIter = keys.iterator();
            return peek(values, item -> past.put(keysIter.next(), item)).iterator();
        };
    }

    private static Map<LocalDate, WeatherInfo> getOrCreate(
            Coords coords,
            Map<Coords, Map<LocalDate, WeatherInfo>> cache)
    {
        Map<LocalDate, WeatherInfo> weathers = cache.get(coords);
        if(weathers == null) {
            weathers = new HashMap<>();
            cache.put(coords, weathers);
        }
        return weathers;
    }
}

class Coords {
    final double lat;
    final double log;

    public Coords(double lat, double log) {
        this.lat = lat;
        this.log = log;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coords coord = (Coords) o;

        if (Double.compare(coord.lat, lat) != 0) return false;
        return Double.compare(coord.log, log) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(lat);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(log);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}