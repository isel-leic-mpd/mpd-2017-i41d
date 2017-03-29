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
import static util.queries.EagerQueries.*;

/**
 * @author Miguel Gamboa
 *         created on 15-03-2017
 */
public class EagerQueriesTest {

    @Test
    public void testEagerFilter(){
        IRequest fileReq = new FileRequest();
        int[] counter = {0};
        IRequest req = path -> () -> {
            counter[0]++;
            return  fileReq.getContent(path).iterator();
        };
        WeatherWebApi api = new WeatherWebApi(req);
        Iterable<WeatherInfo> infos = api.pastWeather(41.15, -8.6167, LocalDate.of(2017,02,01),LocalDate.of(2017,02,28));
        assertEquals(0, counter[0]);
        infos = filter(infos, info -> info.getDescription().toLowerCase().contains("sun"));
        assertEquals(1, counter[0]);
        // <=> Iterable<Integer> temps = EagerQueries.map(infos, info -> info.getTempC());
        Iterable<Integer> temps = map(infos, WeatherInfo::getTempC);
        assertEquals(1, counter[0]);
        temps = distinct(temps);
        assertEquals(1, counter[0]);
        assertEquals(5, count(temps));
        assertEquals(1, counter[0]);
        temps.forEach(System.out::println);
        assertEquals(1, counter[0]);
    }
}
