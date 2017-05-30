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
import java.util.stream.Stream;

import static java.time.LocalDate.now;

/**
 * @author Miguel Gamboa
 *         created on 29-03-2017
 */
public class WeatherService {

    private final WeatherWebApi api;

    public WeatherService(WeatherWebApi api) {
        this.api = api;
    }

    public WeatherService() {
        api = new WeatherWebApi(new HttpRequest());
    }

    public Stream<Location> search(String query) {
        return api.search(query).map(this::dtoToLocation);
    }

    private Location dtoToLocation(LocationDto loc) {
        return new Location(
                loc.getCountry(),
                loc.getRegion(),
                loc.getLatitude(),
                loc.getLongitude(),
                () -> last30daysWeather(loc.getLatitude(), loc.getLongitude()),
                (from, to) -> pastWeather(loc.getLatitude(), loc.getLongitude(), from, to));
    }

    public Stream<WeatherInfo> last30daysWeather(double lat, double log) {
        return api
                .pastWeather(lat, log, now().minusDays(30), now().minusDays(1))
                .map(WeatherService::dtoToWeatherInfo);
    }

    private static WeatherInfo dtoToWeatherInfo(WeatherInfoDto dto) {
        return new WeatherInfo(
                dto.getDate(),
                dto.getTempC(),
                dto.getDescription(),
                dto.getPrecipMM(),
                dto.getFeelsLikeC());
    }

    public Stream<WeatherInfo> pastWeather(double lat, double log, LocalDate from, LocalDate to) {
        return api
                .pastWeather(lat, log, from, to)
                .map(dto -> dtoToWeatherInfo(dto));
    }
}
