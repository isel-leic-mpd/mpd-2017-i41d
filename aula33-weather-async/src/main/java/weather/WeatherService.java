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

package weather;

import util.HttpRequest;
import weather.data.WeatherWebApi;
import weather.data.dto.LocationDto;
import weather.data.dto.WeatherInfoDto;
import weather.model.Location;
import weather.model.WeatherInfo;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static java.time.LocalDate.now;

/**
 * @author Miguel Gamboa
 *         created on 29-03-2017
 */
public class WeatherService implements AutoCloseable{

    private final WeatherWebApi api;

    public WeatherService(WeatherWebApi api) {
        this.api = api;
    }

    public WeatherService() {
        api = new WeatherWebApi(new HttpRequest());
    }

    public CompletableFuture<Stream<Location>> search(String query) {
        return api.search(query)
                .thenApply(str -> str
                    .map(this::dtoToLocation));
    }

    protected Location dtoToLocation(LocationDto loc) {
        return new Location(
                loc.getCountry(),
                loc.getRegion(),
                loc.getLatitude(),
                loc.getLongitude(),
                last30daysWeather(loc.getLatitude(), loc.getLongitude())::join,
                (from, to) -> pastWeather(loc.getLatitude(), loc.getLongitude(), from, to).join());
    }

    public CompletableFuture<Stream<WeatherInfo>> last30daysWeather(double lat, double log) {
        return this.pastWeather(lat, log, now().minusDays(30), now().minusDays(1));
    }

    private static WeatherInfo dtoToWeatherInfo(WeatherInfoDto dto) {
        return new WeatherInfo(
                dto.getDate(),
                dto.getTempC(),
                dto.getDescription(),
                dto.getPrecipMM(),
                dto.getFeelsLikeC());
    }

    public CompletableFuture<Stream<WeatherInfo>> pastWeather(double lat, double log, LocalDate from, LocalDate to) {
        return api
                .pastWeather(lat, log, from, to)
                .thenApply(str -> str
                    .map(dto -> dtoToWeatherInfo(dto)));
    }

    @Override
    public void close() throws Exception {
        api.close();
    }
}
