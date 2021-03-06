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
import util.FileRequest;
import util.IRequest;
import weather.WeatherWebApi;
import weather.dto.WeatherInfo;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static util.queries.LazyQueries.*;

/**
 * @author Miguel Gamboa
 *         created on 15-03-2017
 */
public class LazyQueriesTest {

    class Counter implements IRequest {
        final IRequest req;
        int counter;

        public Counter(IRequest req) {
            this.req = req;
        }

        @Override
        public Iterable<String> getContent(String path) {
            counter++;
            return req.getContent(path);
        }
    }

    @Test
    public void testLazyFilterAndMapAndDistinct(){
        Counter req = new Counter(new FileRequest());
        WeatherWebApi api = new WeatherWebApi(req);
        Iterable<WeatherInfo> infos = api.pastWeather(41.15, -8.6167, LocalDate.of(2017,02,01),LocalDate.of(2017,02,28));
        assertEquals(0, req.counter);
        infos = filter(infos, info -> info.getDescription().toLowerCase().contains("sun"));
        assertEquals(0, req.counter);
        Iterable<Integer> temps = map(infos, info -> info.getTempC());
        assertEquals(0, req.counter);
        // temps = map(infos, WeatherInfo::getTempC);
        temps = distinct(temps);
        assertEquals(0, req.counter);
        assertEquals(5, count(temps));
        assertEquals(1, req.counter);
        assertEquals((long) 21, (long) skip(temps, 2).iterator().next());
        assertEquals(2, req.counter);
        temps.forEach(System.out::println);
        assertEquals(3, req.counter);
    }
}
