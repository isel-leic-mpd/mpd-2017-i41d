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
import util.Comparators;
import util.Countify;
import util.FileRequest;
import util.ICounter;
import util.queries.LazyQueries;
import weather.WeatherService;
import weather.data.WeatherWebApi;
import weather.data.dto.WeatherInfoDto;
import weather.model.WeatherInfo;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;

import static java.time.LocalDate.of;
import static org.junit.Assert.assertEquals;
import static util.Comparators.*;
import static util.Comparators.comparing;
import static util.queries.LazyQueries.*;

/**
 * @author Miguel Gamboa
 *         created on 15-03-2017
 */
public class LazyQueriesTest {

    @Test
    public void testLazyFilterAndMapAndDistinct(){
        ICounter<String, Iterable<String>> req = Countify.of(new FileRequest()::getContent);
        WeatherWebApi api = new WeatherWebApi(req::apply);
        Iterable<WeatherInfoDto> infos = api.pastWeather(41.15, -8.6167, of(2017,02,01), of(2017,02,28));
        assertEquals(0, req.getCount());
        infos = filter(infos, info -> info.getDescription().toLowerCase().contains("sun"));
        assertEquals(0, req.getCount());
        Iterable<Integer> temps = map(infos, info -> info.getTempC());
        assertEquals(0, req.getCount());
        // temps = map(infos, WeatherInfoDto::getTempC);
        temps = distinct(temps);
        assertEquals(0, req.getCount());
        assertEquals(5, count(temps));
        assertEquals(1, req.getCount());
        assertEquals((long) 21, (long) skip(temps, 2).iterator().next());
        assertEquals(2, req.getCount());
        temps.forEach(System.out::println);
        assertEquals(3, req.getCount());
    }

    @Test
    public void testMax() {
        WeatherService api = new WeatherService(new WeatherWebApi(new FileRequest()));
        Iterable<WeatherInfo> infos = api
                .pastWeather(41.15, -8.6167, of(2017, 02, 01), of(2017, 02, 28));
        Optional<WeatherInfo> maxTemp = max(infos, comparing(WeatherInfo::getFeelsLikeC));
        assertEquals(14, maxTemp.get().getFeelsLikeC());
        maxTemp = max(infos, comparing(WeatherInfo::getTempC).invert().invert());
        assertEquals(21, maxTemp.get().getTempC());
        Optional<WeatherInfo> minTemp = max(infos, comparing(WeatherInfo::getTempC).invert());
        assertEquals(10.9, minTemp.get().getPrecipMM(), 0);
        minTemp = max(
                infos,
                comparing(WeatherInfo::getTempC).invert().thenBy(WeatherInfo::getPrecipMM));
        assertEquals(13.1, minTemp.get().getPrecipMM(), 0);
        minTemp.ifPresent(System.out::println);
    }
}
