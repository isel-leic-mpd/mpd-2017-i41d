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
import util.queries.LazyQueries;
import util.queries.Queryable;
import util.queries.StreamUtils;
import weather.data.dto.WeatherInfoDto;

import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Comparator.comparing;
import static org.junit.Assert.assertEquals;
import static util.queries.LazyQueries.filter;
import static util.queries.LazyQueries.map;
import static util.queries.LazyQueries.max;
import static util.queries.LazyQueries.skip;
import static util.queries.StreamUtils.filterEvenLine;
import static util.queries.StreamUtils.distinct;

/**
 * @author Miguel Gamboa
 *         created on 15-03-2017
 */
public class LazyQueriesTest {

    static final String path = "past-weather.ashx-q-41.15--8.6167-date-2017-02-01-enddate-2017-04-30";
    IRequest req = new FileRequest();

    @Test
    public void testFileQueriesIterable() {
        Iterable<String> data = req.getContent(path);
        Iterable<String> lines =
                filter(data, s -> !s.startsWith("#")); // Filter comments
        lines = skip(lines, 1);                        // Skip line: Not Available
        lines = LazyQueries.filterEvenLine(lines);                 // Filter even lines
        Iterable<WeatherInfoDto> weather = map(lines, WeatherInfoDto::valueOf);
        Integer maxTemp = max(map(weather, WeatherInfoDto::getTempC)).get();
        /*
         * Assert
         */
        assertEquals(maxTemp.intValue(), 27);
    }

    @Test
    public void testFileQueriesStream() {
        Iterable<String> data = req.getContent(path);
        Supplier<Stream<WeatherInfoDto>> weather = () ->
                filterEvenLine(StreamSupport
                    .stream(data.spliterator(), false)
                    .filter(s -> !s.startsWith("#"))// Filter comments
                    .skip(1)                        // Skip line: Not Available
                )                                   // Filter even lines
                .map(WeatherInfoDto::valueOf);
        int maxTemp = weather.get()
                .map(WeatherInfoDto::getTempC)
                .max(Integer::compare)
                .get();
        long size = StreamUtils
                .distinct(
                    weather.get(),
                    comparing(WeatherInfoDto::getTempC))
                .count();
        assertEquals(maxTemp, 27);
        assertEquals(size, 18);
    }

    @Test
    public void testFileQueriesQueryable() {
        Iterable<String> data = req.getContent(path);
        Integer maxTemp = Queryable
                .of(data)
                .filter(s -> !s.startsWith("#"))// Filter comments
                .skip(1)                       //  Skip line: Not Available
                .filterEvenLine()             // Filter even lines
                .map(WeatherInfoDto::valueOf)
                .map(WeatherInfoDto::getTempC)
                .max(Integer::compare)
                .get();
        assertEquals(maxTemp.intValue(), 27);
    }
    @Test
    public void testFileQueriesQueryableDistinct() {
        Iterable<String> data = req.getContent(path);
        int size = Queryable
                .of(data)
                .filter(s -> !s.startsWith("#"))// Filter comments
                .skip(1)                        // Skip line: Not Available
                .filterEvenLine()               // Filter even lines
                .map(WeatherInfoDto::valueOf)
                .map(WeatherInfoDto::getTempC)
                .distinct()
                .count();
        assertEquals(size, 18);
    }
}
