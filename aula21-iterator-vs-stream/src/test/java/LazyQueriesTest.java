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
import util.IRequest;
import util.queries.Queryable;
import weather.data.WeatherWebApi;
import weather.data.dto.WeatherInfoDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.junit.Assert.assertEquals;
import static util.queries.LazyQueries.*;

/**
 * @author Miguel Gamboa
 *         created on 15-03-2017
 */
public class LazyQueriesTest {

    static final String path = "past-weather.ashx-q-41.15--8.6167-date-2017-02-01-enddate-2017-04-30";

    @Test
    public void testFileQueriesIterable() {
        /*
         * Arrange
         */
        int[] counter = {0};
        Predicate<String> isEvenLine = item -> ++counter[0] % 2 == 0;
        IRequest req = new FileRequest();
        Iterable<String> data = req.getContent(path);
        /*
         * Act
         */
        Iterable<String> lines = filter(data, s -> !s.startsWith("#")); // Filter comments
        lines = skip(lines, 1);    //  Skip line: Not Available
        lines = filter(lines, isEvenLine); // Filter even lines
        Iterable<WeatherInfoDto> weather = map(lines, WeatherInfoDto::valueOf);
        Integer maxTemp = max(map(weather, WeatherInfoDto::getTempC)).get();
        /*
         * Assert
         */
        assertEquals(maxTemp.intValue(), 27);
    }

    @Test
    public void testFileQueriesStream() {
        /*
         * Arange
         */
        int[] counter = {0};
        Predicate<String> isEvenLine = item -> ++counter[0] % 2 == 0;
        IRequest req = new FileRequest();
        Iterable<String> data = req.getContent(path);
        /*
         * Act
         */
        Integer maxTemp = StreamSupport
                .stream(data.spliterator(), false)
                .filter(s -> !s.startsWith("#"))// Filter comments
                .skip(1)                       //  Skip line: Not Available
                .filter(isEvenLine)             // Filter even lines
                .map(WeatherInfoDto::valueOf)
                .map(WeatherInfoDto::getTempC)
                .max(Integer::compare)
                .get();
        assertEquals(maxTemp.intValue(), 27);
    }

    @Test
    public void testFileQueriesQueryable() {
        /*
         * Arange
         */
        int[] counter = {0};
        Predicate<String> isEvenLine = item -> ++counter[0] % 2 == 0;
        IRequest req = new FileRequest();
        Iterable<String> data = req.getContent(path);
        /*
         * Act
         */
        Integer maxTemp = Queryable
                .of(data)
                .filter(s -> !s.startsWith("#"))// Filter comments
                .skip(1)                       //  Skip line: Not Available
                .filter(isEvenLine)             // Filter even lines
                .map(WeatherInfoDto::valueOf)
                .map(WeatherInfoDto::getTempC)
                .max(Integer::compare)
                .get();
        assertEquals(maxTemp.intValue(), 27);
    }
}
