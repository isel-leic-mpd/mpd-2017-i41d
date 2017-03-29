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
import util.queries.EagerQueries;
import util.queries.LazyQueries;
import weather.WeatherWebApi;
import weather.dto.WeatherInfo;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

/**
 * @author Miguel Gamboa
 *         created on 15-03-2017
 */
public class LazyQueriesTest {

    @Test
    public void testLazyFilterAndMapAndDistinct(){
        WeatherWebApi api = new WeatherWebApi(new FileRequest());
        Iterable<WeatherInfo> infos = api.pastWeather(41.15, -8.6167, LocalDate.of(2017,02,01),LocalDate.of(2017,02,28));
        infos = LazyQueries.filter(infos, info -> info.getDescription().toLowerCase().contains("sun"));
        // <=> Iterable<Integer> temps = EagerQueries.map(infos, info -> info.getTempC());
        // Iterable<Integer> temps = EagerQueries.map(infos, WeatherInfo::getTempC);
        // temps = EagerQueries.distinct(temps);
        assertEquals(8, EagerQueries.count(infos));
        infos.forEach(System.out::println);
    }
}
