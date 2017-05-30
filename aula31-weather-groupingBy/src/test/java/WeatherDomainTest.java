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

import org.junit.Test;
import util.Countify;
import util.FileRequest;
import util.ICounter;
import weather.WeatherService;
import weather.WeatherServiceCache;
import weather.data.WeatherWebApi;
import weather.model.Location;
import weather.model.WeatherInfo;

import java.util.stream.Stream;

import static java.time.LocalDate.of;
import static org.junit.Assert.assertEquals;

/**
 * @author Miguel Gamboa
 *         created on 29-03-2017
 */
public class WeatherDomainTest {
    @Test
    public void testWeatherServiceLazy(){
        /**
         * Arrange WeatherService --> WeatherWebApi --> Countify --> FileRequest
         */
        ICounter<String, Stream<String>> req = Countify.of(new FileRequest()::getContent);
        WeatherService api = new WeatherService(new WeatherWebApi(req::apply));
        /**
         * Act and Assert
         */
        Stream<Location> locals = api.search("Porto");
        assertEquals(1, req.getCount());
        locals = locals.filter(l -> l.getLatitude() > 0 );
        assertEquals(1, req.getCount());
        Location loc = locals.findFirst().get();
        assertEquals(1, req.getCount());
        Stream<WeatherInfo> infos = loc.getPastWeather(of(2017,02,01), of(2017,02,28));
        assertEquals(2, req.getCount());
        infos = infos.filter(info -> info.getDescription().toLowerCase().contains("sun"));
        Stream<Integer> temps = infos.map(WeatherInfo::getTempC);
        temps = temps.distinct();
        assertEquals(2, req.getCount());
        /**
         * When we iterate over the pastWeather then we make one more request
         */
        assertEquals(5, temps.count()); // iterates all items
        assertEquals(2, req.getCount());
    }

    @Test
    public void testWeatherServiceLazyAndCache(){
        /**
         * Arrange WeatherService --> WeatherWebApi --> Countify --> FileRequest
         */
        ICounter<String, Stream<String>> req = Countify.of(new FileRequest()::getContent);
        // Function<String, Iterable<String>> cache = Cache.memoize(req);
        WeatherService api = new WeatherServiceCache(new WeatherWebApi(req::apply));
        /**
         * Act and Assert
         */
        Stream<Location> locals = api.search("Porto");
        assertEquals(1, req.getCount());
        locals = locals.filter(l -> l.getLatitude() > 0 );
        assertEquals(1, req.getCount());
        /**
         * Counts 1 request when iterates to get the first Location
         */
        Location loc = locals.iterator().next();
        assertEquals(1, req.getCount());
        Stream<WeatherInfo> infos = loc.getPastWeather(of(2017,02,01), of(2017,02,28));
        assertEquals(2, req.getCount());
        infos = infos.filter(info ->
                info.getDescription().toLowerCase().contains("sun"));
        Stream<Integer> temps = infos.map(WeatherInfo::getTempC);
        temps = temps.distinct();
        assertEquals(2, req.getCount());
        /**
         * When we iterate over the pastWeather then we make one more request
         */
        assertEquals(5, temps.count()); // iterates all items
        assertEquals(2, req.getCount());
        temps = api.search("Porto")
                .findFirst().get()
                .getPastWeather(of(2017,02,01), of(2017,02,28))
                .filter(info -> info.getDescription().toLowerCase().contains("sun"))
                .map(WeatherInfo::getTempC);
        assertEquals((long) 20, (long) temps.skip(2).findFirst().get()); // another iterator
        assertEquals(3, req.getCount());
        /**
         * getting a sub-interval of past weather should return from cache
         */
        infos = loc.getPastWeather(of(2017,02,05), of(2017,02,18));
        infos.iterator().next();
        assertEquals(3, req.getCount());
        /**
         * getting a new interval gets from IO
         */
        infos = loc.getPastWeather(of(2017,02,15), of(2017,03,15));
        infos.forEach((item) -> {}); // Consume all to add all itens in cache
        assertEquals(4, req.getCount());
        /**
         * getting a sub-interval of past weather should return from cache
         */
        infos = loc.getPastWeather(of(2017,02,20), of(2017,03,10));
        infos.iterator().next();
        assertEquals(4, req.getCount());
    }
}
