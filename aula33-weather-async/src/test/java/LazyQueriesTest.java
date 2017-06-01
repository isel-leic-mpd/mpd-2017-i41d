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
import weather.data.dto.WeatherInfoDto;
import weather.model.Location;
import weather.model.WeatherInfo;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.LocalDate.of;
import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.maxBy;
import static java.util.stream.Collectors.minBy;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static util.Comparators.comparing;
import static util.PredicateBy.equalsBy;

/**
 * @author Miguel Gamboa
 *         created on 15-03-2017
 */
public class LazyQueriesTest {

    @Test
    public void testLazyFilterAndMapAndDistinct() {
        ICounter<String, CompletableFuture<Stream<String>>> req = Countify.of(new FileRequest()::getContent);
        WeatherWebApi api = new WeatherWebApi(req::apply);
        Stream<WeatherInfoDto> infos = api.pastWeather(41.15, -8.6167, of(2017, 02, 01), of(2017, 02, 28)).join();
        assertEquals(1, req.getCount());
        infos = infos.filter(info -> info.getDescription().toLowerCase().contains("sun"));
        assertEquals(1, req.getCount());
        Stream<Integer> temps = infos.map(info -> info.getTempC());
        assertEquals(1, req.getCount());
        temps = temps.distinct();
        assertEquals(1, req.getCount());
        assertEquals(5, temps.count());
        assertEquals(1, req.getCount());
    }

    @Test
    public void testMax() {
        WeatherService api = new WeatherService(new WeatherWebApi(new FileRequest()));
        Supplier<Stream<WeatherInfo>> infos = () -> api.pastWeather(41.15, -8.6167, of(2017, 02, 01), of(2017, 02, 28)).join();
        Optional<WeatherInfo> maxTemp = infos.get().max(comparing(WeatherInfo::getFeelsLikeC));
        assertEquals(14, maxTemp.get().getFeelsLikeC());
        maxTemp = infos.get().max(comparing(WeatherInfo::getTempC).invert().invert());
        assertEquals(21, maxTemp.get().getTempC());
        Optional<WeatherInfo> minTemp = infos.get().max(comparing(WeatherInfo::getTempC).invert());
        assertEquals(10.9, minTemp.get().getPrecipMM(), 0);
        minTemp = infos.get().max(
                comparing(WeatherInfo::getTempC).invert().thenBy(WeatherInfo::getPrecipMM));
        assertEquals(13.1, minTemp.get().getPrecipMM(), 0);
        minTemp.ifPresent(System.out::println);
    }

    @Test
    public void testFind() {
        WeatherService api = new WeatherService(new WeatherWebApi(new FileRequest()));
        Supplier<Stream<WeatherInfo>> infos = () -> api.pastWeather(41.15, -8.6167, of(2017, 02, 01), of(2017, 02, 28)).join();
        Optional<WeatherInfo> weather = infos.get().filter(equalsBy(WeatherInfo::getTempC, 20)).findFirst();
        assertEquals(16, weather.get().getDate().getDayOfMonth());

        weather = infos.get().filter(equalsBy(WeatherInfo::getTempC, 20).not().not()).findFirst();
        assertEquals(16, weather.get().getDate().getDayOfMonth());

        Predicate<WeatherInfo> sunnyAnd20degrees =
                equalsBy(WeatherInfo::getDescription, "Sunny")
                        .and(equalsBy(WeatherInfo::getTempC, 20));
        assertEquals(16, infos.get().filter(sunnyAnd20degrees).findFirst().get().getDate().getDayOfMonth());
    }

    @Test
    public void testMaxTempInLisbonOnMarch() {
        WeatherService api = new WeatherService(new WeatherWebApi(new FileRequest()));
        int maxtTemp = api
                .search("Lisbon")   // Stream<Location>
                .join().findFirst().get()  // Location
                .getPastWeather(    // Stream<WeatherInfo>
                        LocalDate.of(2017, 04, 01), LocalDate.of(2017, 04, 30))
                .mapToInt(WeatherInfo::getTempC)   // IntStream
                .max()                             // OptionalInt
                .getAsInt();                       // int
        assertEquals(30, maxtTemp);
    }

    @Test
    public void testMaxTempInLisbonAndPortoOnMarch() {
        WeatherService api = new WeatherService(new WeatherWebApi(new FileRequest()));
        int maxTemp = Stream
                .of("Lisbon", "Porto")             // Stream<String>
                .map(api::search)                  // Stream<Stream<Location>>
                .map(seq -> seq.join().findFirst().get()) // Stream<Location> [Lisbon, Porto]
                .map(loc -> loc.getPastWeather(    // Stream<Stream<WeatherInfo>>
                        LocalDate.of(2017, 04, 01), LocalDate.of(2017, 04, 30)))
                .map(seq -> seq.mapToInt(WeatherInfo::getTempC)) // Stream<IntStream>>
                .mapToInt(seq -> seq.max().getAsInt()) // IntStream
                .max()                             // OptionalInt
                .getAsInt();                       // int
        assertEquals(30, maxTemp);
    }

    @Test
    public void testMaxTempInLisbonAndPortoOnMarchWithFlatMap() {
        WeatherService api = new WeatherService(new WeatherWebApi(new FileRequest()));
        int maxTemp = Stream
                .of("Lisbon", "Porto")                          // Stream<String>
                .map(api::search)                           // Stream<Location>
                .flatMap(CompletableFuture::join)
                .filter(l -> l.getCountry().equals("Portugal")) // Stream<Location>
                .flatMap(loc -> loc.getPastWeather( // Stream<WeatherInfo>
                        LocalDate.of(2017, 04, 01), LocalDate.of(2017, 04, 30)))
                .mapToInt(WeatherInfo::getTempC)    // IntStream>
                .max()                              // OptionalInt
                .getAsInt();                        // int
        assertEquals(30, maxTemp);
    }

    @Test
    public void testCollectLisbonAprilTempsToString() {
        WeatherService api = new WeatherService(new WeatherWebApi(new FileRequest()));
        String temps = api.search("Lisbon")
                .join().findFirst().get()
                .getPastWeather(LocalDate.of(2017, 04, 01), LocalDate.of(2017, 04, 30))
                .map(WeatherInfo::getTempC)
                .map(Object::toString)
                .distinct()
                .sorted()
                .collect(joining(","));
        System.out.println(temps);
    }

    @Test
    public void testCollectLisbonAprilGroupByTemps() {
        WeatherService api = new WeatherService(new WeatherWebApi(new FileRequest()));
        Map<Integer, String> temps = api.search("Lisbon")
                .join().findFirst().get()
                .getPastWeather(LocalDate.of(2017, 04, 01), LocalDate.of(2017, 04, 30))
                .collect(groupingBy(
                        WeatherInfo::getTempC, // classifier
                        mapping(w -> w.getDate().getDayOfMonth() + "", joining(","))));
        temps.forEach((k, v) -> System.out.println(k + ": " + v));
    }

    @Test
    public void testCollectLisbonAprilGroupByDescs() {
        WeatherService api = new WeatherService(new WeatherWebApi(new FileRequest()));
        api.search("Lisbon")
                .join().findFirst().get()
                .getPastWeather(LocalDate.of(2017, 04, 01), LocalDate.of(2017, 04, 30))
                .collect(groupingBy(
                        WeatherInfo::getDescription,
                        mapping(w -> w.getTempC(), maxBy(Integer::compare))))
                .forEach((k, v) -> System.out.println(k + ": " + v));
    }
}