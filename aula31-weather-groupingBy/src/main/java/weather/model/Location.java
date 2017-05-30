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

package weather.model;

import java.time.LocalDate;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author Miguel Gamboa
 *         created on 29-03-2017
 */
public class Location {
    private final String country;
    private final String region;
    private final double latitude;
    private final double longitude;
    private final Supplier<Stream<WeatherInfo>> last30daysWeather;
    private final BiFunction<LocalDate, LocalDate, Stream<WeatherInfo>> pastWeather;

    public Location(String country, String region, double latitude, double longitude, Supplier<Stream<WeatherInfo>> last30daysWeather, BiFunction<LocalDate, LocalDate, Stream<WeatherInfo>> pastWeather) {
        this.country = country;
        this.region = region;
        this.latitude = latitude;
        this.longitude = longitude;
        this.last30daysWeather = last30daysWeather;
        this.pastWeather = pastWeather;
    }

    public String getCountry() {
        return country;
    }

    public String getRegion() {
        return region;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Stream<WeatherInfo> getLast30daysWeather() {
        return last30daysWeather.get();
    }

    public Stream<WeatherInfo> getPastWeather(LocalDate from, LocalDate to) {
        return pastWeather.apply(from, to);
    }

    @Override
    public String toString() {
        return "Location{" +
                "country='" + country + '\'' +
                ", region='" + region + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", last30daysWeather=" + last30daysWeather +
                '}';
    }
}
