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

package weather.controllers;

import weather.WeatherService;
import weather.model.Location;

import javax.servlet.http.HttpServletRequest;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.nio.file.Files.*;

/**
 * @author Miguel Gamboa
 *         created on 06-06-2017
 */
public class WeatherController {

    private final WeatherService api;
    private final String getSearchView = load("views/search.html");
    private final String searchCityView = load("views/searchCity.html");
    private final String searchCityRow = load("views/searchCityRow.html");
    private final String weather = load("views/weather.html");
    private final String weatherRow = load("views/weatherRow.html");

    static final class LatLog {
        final double lat, log;
        public LatLog(double lat, double log) {
            this.lat = lat; this.log = log;
        }
    }
    private final ConcurrentHashMap<LatLog, CompletableFuture<String>>
            last30daysViewsCache = new ConcurrentHashMap<>();

    public WeatherController(WeatherService api) {
        this.api = api;
    }

    public String getSearch(HttpServletRequest req) {
        return getSearchView;
    }

    public String searchCity(HttpServletRequest req) {
        String city = req.getParameter("name");
        String rows = api
                .search(city)
                .join()
                // Dispatch non-blocking request of Last 30 days weather
                .peek(l -> last30daysWeather(l.getLatitude(), l.getLongitude()))
                .map(l -> String.format(searchCityRow,
                        l.getCountry(),
                        l.getRegion(),
                        linkForLocation(l)))
                .collect(Collectors.joining());
        return String.format(searchCityView, rows);
    }

    public String last30daysWeather(HttpServletRequest request) {
        String[] parts = request.getPathInfo().split("/");
        double lat = Double.parseDouble(parts[1]);
        double log = Double.parseDouble(parts[2]);
        return last30daysWeather(lat, log).join();
    }

    public CompletableFuture<String> requestLast30daysWeather(double lat, double log) {
        return api
                .last30daysWeather(lat, log)
                .thenApply(past -> past
                        .map(wi -> String.format(weatherRow,
                                wi.getDate(),
                                wi.getTempC(),
                                wi.getFeelsLikeC(),
                                wi.getDescription(),
                                wi.getPrecipMM()))
                        .collect(Collectors.joining()))
                .thenApply(rows -> String.format(weather, rows));
    }

    /**
     * First tries load from last30daysViewsCache.
     * If it does not exist look in file system: loadLast30daysWeather(lat, log);
     * Otherwise request to service: requestLast30daysWeather(lat, log);
     */
    private CompletableFuture<String> last30daysWeather(double lat, double log) {
        LatLog coords = new LatLog(lat, log);
        CompletableFuture<String> last30days = last30daysViewsCache.get(coords);
        if (last30days == null) {
            String view = loadLast30daysWeather(lat, log); // Search on file system
            if(view == null) { // Not in file system
                last30days = requestLast30daysWeather(lat, log) // Request to service
                        .thenApply(v -> writeLast30daysWeather(lat, log, v)); // Save on disk
            }
            else { // Already in File System
                last30days = new CompletableFuture<>();
                last30days.complete(view);
            }
            last30daysViewsCache.putIfAbsent(coords, last30days); // Save on cache
        }
        return last30days;
    }

    private static String loadLast30daysWeather(double lat, double log) {
        String path = lat + log + ".html";
        URL url = ClassLoader.getSystemResource(path);
        return url == null ? null : load(url);
    }

    private static String writeLast30daysWeather(double lat, double log, String view) {
        String path = lat + log + ".html";
        try(FileWriter fw = new FileWriter(path)) {
            fw.write(view);
            fw.flush();
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return view;
    }

    private static String linkForLocation(Location l) {
        return String.format("<a href=\"/weather/%s/%s/last30days/\">%s</a>",
                l.getLatitude(), l.getLongitude(), l.getRegion());
    }

    private static String load(String file) {
        return load(ClassLoader.getSystemResource(file));
    }
    private static String load(URL url) {
        try {
            URI uri = url.toURI();
            Path path = Paths.get(uri);
            return lines(path).collect(Collectors.joining());
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }


}
