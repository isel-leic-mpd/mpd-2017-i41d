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
import weather.data.dto.LocationDto;
import weather.model.Location;
import weather.model.WeatherInfo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;


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
    protected Location dtoToLocation(LocationDto loc) {
        return new Location(
                loc.getCountry(),
                loc.getRegion(),
                loc.getLatitude(),
                loc.getLongitude(),
                last30daysWeatherSupplier(loc),
                (from, to) -> pastWeather(loc.getLatitude(), loc.getLongitude(), from, to).join());
    }

    private Supplier<Stream<WeatherInfo>> last30daysWeatherSupplier(LocationDto loc) {
        /**
         * Immediately dispatch request for last 30 days weather asynchronously.
         */
        CompletableFuture<Stream<WeatherInfo>> promise =
                last30daysWeather(loc.getLatitude(), loc.getLongitude());
        boolean[] firstReq = { true };
        /**
         * The supplier returns the result of the promise on first get.
         * Further invocations will call last30daysWeather() again which in turn
         * calls the pastWeather() of this WeatherServiceCache, which gets past
         * weather from cache.
         */
        return () -> {
            Stream<WeatherInfo> res = firstReq[0]
                    ? promise.join()
                    : last30daysWeather(loc.getLatitude(), loc.getLongitude()).join(); // ALREADY on cache.
            firstReq[0] = false;
            return res;
        };
    }

    @Override
    public CompletableFuture<Stream<WeatherInfo>> pastWeather(double lat, double log, LocalDate from, LocalDate to) {
        Coords location = new Coords(lat, log);
        Map<LocalDate, WeatherInfo> past = getOrCreate(location, cache);
        List<LocalDate> keys = Stream
                .iterate(from, prev -> prev.plusDays(1))
                .limit(ChronoUnit.DAYS.between(from, to.plusDays(1)))
                .collect(toList());
        if (past.keySet().containsAll(keys)) {
            CompletableFuture<Stream<WeatherInfo>> res = new CompletableFuture<>();
            res.complete(keys.stream().map(past::get));
            return res;
        }
        CompletableFuture<Stream<WeatherInfo>> values = super.pastWeather(lat, log, from, to);
        Iterator<LocalDate> keysIter = keys.iterator();
        return values.thenApply(vs -> vs
                .peek(item -> past.put(keysIter.next(), item)));
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