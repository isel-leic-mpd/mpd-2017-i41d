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
import util.queries.NaiveQueries;
import util.WeatherPredicate;
import weather.data.WeatherWebApi;
import weather.data.dto.WeatherInfoDto;

import java.time.LocalDate;

import static org.junit.Assert.*;
import static util.queries.NaiveQueries.*;

/**
 * @author Miguel Gamboa
 *         created on 15-03-2017
 */
public class NaiveQueriesTest {
    /**
     * Tests 1st Approach queries. NaiveQueries::filterCloudy
     */
    @Test
    public void testFilterCloudyDays(){
        WeatherWebApi dataSrc = new WeatherWebApi(new FileRequest());
        Iterable<WeatherInfoDto> data = dataSrc.pastWeather(41.15, -8.6167, LocalDate.of(2017,02,01), LocalDate.of(2017,02,28));
        data = NaiveQueries.filterCloudy(data);
        assertEquals(4, count(data));

    }

    /**
     * Tests 1st Approach queries. NaiveQueries::filterRainy
     */
    @Test
    public void testFilterRainyDays(){
        WeatherWebApi dataSrc = new WeatherWebApi(new FileRequest());
        Iterable<WeatherInfoDto> data = dataSrc.pastWeather(41.15, -8.6167, LocalDate.of(2017,02,01), LocalDate.of(2017,02,28));
        data = NaiveQueries.filterRainy(data);
        assertEquals(14, count(data));
    }

    /**
     * Tests 2nd Approach queries, parametrize the description filter criteria.
     * VALUE parameterization
     */
    @Test
    public void testFilterByDesc(){
        WeatherWebApi dataSrc = new WeatherWebApi(new FileRequest());
        Iterable<WeatherInfoDto> data = dataSrc.pastWeather(41.15, -8.6167, LocalDate.of(2017,02,01), LocalDate.of(2017,02,28));
        assertEquals(4, count(filterDesc(data, "cloud")));
        assertEquals(14, count(filterDesc(data, "rain")));
        assertEquals(7, count(filterDesc(data, "sunny")));
    }

    /**
     * Tests 3rd Approach queries, parametrize the filter criteria.
     * Creates a new class implementation of WeatherPredicate for each criteria.
     * BEHAVIOR parameterization
     */
    @Test
    public void testFilterWithInnerClasses(){
        WeatherWebApi dataSrc = new WeatherWebApi(new FileRequest());
        Iterable<WeatherInfoDto> data = dataSrc.pastWeather(41.15, -8.6167, LocalDate.of(2017,02,01), LocalDate.of(2017,02,28));
        assertEquals(4, count(filter(data, new WeatherDescriptionPredicate("cloud"))));
        assertEquals(14, count(filter(data, new WeatherDescriptionPredicate("rain"))));
        assertEquals(7, count(filter(data, new WeatherDescriptionPredicate("sunny"))));
        assertEquals(10, count(filter(data, new WeatherDryDays())));
    }

    /**
     * Tests 3rd Approach queries, parametrize the filter criteria using
     * lambdas to create new implementations of WeatherPredicate.
     */
    @Test
    public void testFilterWithLambdas(){
        WeatherWebApi dataSrc = new WeatherWebApi(new FileRequest());
        Iterable<WeatherInfoDto> data = dataSrc.pastWeather(41.15, -8.6167, LocalDate.of(2017,02,01), LocalDate.of(2017,02,28));
        assertEquals(4, count(filter(data, item -> item.getDescription().toLowerCase().contains("cloudy"))));
        assertEquals(14, count(filter(data, item -> item.getDescription().toLowerCase().contains("rain"))));
        assertEquals(7, count(filter(data, item -> item.getDescription().toLowerCase().contains("sunny"))));
        assertEquals(10, count(filter(data, item -> item.getPrecipMM() == 0)));
    }

    /**
     * Auxiliary WeatherPredicate implementation to filter WeatherInfoDto objects
     * by their description.
     */
    static class WeatherDescriptionPredicate implements WeatherPredicate {

        private final String desc;

        public WeatherDescriptionPredicate(String desc) {
            this.desc = desc;
        }

        @Override
        public boolean test(WeatherInfoDto item) {
            return item.getDescription().toLowerCase().contains(desc);
        }
    }

    /**
     * Auxiliary WeatherDatePredicate implementation to filter WeatherInfoDto objects
     * by date.
     */
    static class WeatherDryDays implements WeatherPredicate {

        @Override
        public boolean test(WeatherInfoDto item) {
            return item.getPrecipMM() == 0;
        }
    }
}
