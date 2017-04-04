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
import weather.data.WeatherWebApi;
import weather.model.Location;
import weather.model.WeatherInfo;

import java.time.LocalDate;

import static java.lang.System.out;
import static java.time.LocalDate.of;
import static org.junit.Assert.assertEquals;
import static util.queries.LazyQueries.count;
import static util.queries.LazyQueries.distinct;
import static util.queries.LazyQueries.filter;
import static util.queries.LazyQueries.map;
import static util.queries.LazyQueries.skip;

/**
 * @author Miguel Gamboa
 *         created on 29-03-2017
 */
public class WeatherDomainTest {
    @Test
    public void testWeatherService(){
        /**
         * Arrange WeatherService --> WeatherWebApi --> Countify --> FileRequest
         */
        ICounter<String, Iterable<String>> req = Countify.of(new FileRequest()::getContent);
        WeatherService api = new WeatherService(new WeatherWebApi(req::apply));
        /**
         * Act and Assert
         * Counts 0 request while iterator() is not consumed
         */
        Iterable<Location> locals = api.search("Porto");
        assertEquals(0, req.getCount());
        locals = filter(locals, l -> l.getLatitude() > 0 );
        assertEquals(0, req.getCount());
        /**
         * Counts 1 request when iterate to get the first Location
         */
        Location loc = locals.iterator().next();
        assertEquals(1, req.getCount());
        Iterable<WeatherInfo> infos = api.pastWeather(loc.getLatitude(), loc.getLongitude(), of(2017,02,01), of(2017,02,28));
        assertEquals(1, req.getCount());
        infos = filter(infos, info -> info.getDescription().toLowerCase().contains("sun"));
        assertEquals(1, req.getCount());
        Iterable<Integer> temps = map(infos, WeatherInfo::getTempC);
        assertEquals(1, req.getCount());
        temps = distinct(temps);
        assertEquals(1, req.getCount());
        /**
         * When we iterate over the pastWeather then we make one more request
         */
        assertEquals(5, count(temps)); // iterates all items
        assertEquals(2, req.getCount());
        assertEquals((long) 21, (long) skip(temps, 2).iterator().next()); // another iterator
        assertEquals(3, req.getCount());
        temps.forEach(System.out::println); // iterates all items
        assertEquals(4, req.getCount());
    }
}
