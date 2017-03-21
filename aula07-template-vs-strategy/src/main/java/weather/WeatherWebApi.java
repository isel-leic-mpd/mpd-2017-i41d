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

import util.IRequest;
import util.queries.LazyQueries;
import weather.model.Location;
import weather.model.WeatherInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import static util.queries.LazyQueries.*;

/**
 * @author Miguel Gamboa
 *         created on 07-03-2017
 */
public class WeatherWebApi {

    private static final String WEATHER_TOKEN;
    private static final String WEATHER_HOST = "http://api.worldweatheronline.com";
    private static final String WEATHER_PAST = "/premium/v1/past-weather.ashx";
    private static final String WEATHER_PAST_ARGS =
            "?q=%s&date=%s&enddate=%s&tp=24&format=csv&key=%s";
    private static final String WEATHER_SEARCH="/premium/v1/search.ashx?query=%s";
    private static final String WEATHER_SEARCH_ARGS="&format=tab&key=%s";

    static {
        try {
            URL keyFile = ClassLoader.getSystemResource("worldweatheronline-app-key.txt");
            if(keyFile == null) {
               throw new IllegalStateException(
                       "YOU MUST GOT a KEY in developer.worldweatheronline.com and place it in src/main/resources/worldweatheronline-app-key.txt");
            } else {
                InputStream keyStream = keyFile.openStream();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(keyStream))) {
                    WEATHER_TOKEN = reader.readLine();
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private final IRequest req;

    public WeatherWebApi(IRequest req) {
        this.req = req;
    }

    /**
     * E.g. http://api.worldweatheronline.com/free/v2/search.ashx?query=oporto&format=tab&key=*****
     */

    public Iterable<Location> search(String query) {
        String url = WEATHER_HOST + WEATHER_SEARCH + WEATHER_SEARCH_ARGS;
        url = String.format(url, query, WEATHER_TOKEN);
        Iterable<String> iterable =	filter(
                req.getContent(url),
                (String s) -> !s.startsWith("#"));
        return map(iterable,Location::valueOf);
    }

    /**
     * E.g. http://api.worldweatheronline.com/free/v2/search.ashx?query=oporto&format=tab&key=*****
     */
    public Iterable<WeatherInfo> pastWeather(
            double lat,
            double log,
            LocalDate from,
            LocalDate to
    ) {
        String query = lat + "," + log;
        String path = WEATHER_HOST + WEATHER_PAST +
                String.format(WEATHER_PAST_ARGS, query, from, to, WEATHER_TOKEN);
        Iterable<String> stringIterable = LazyQueries.filter(req.getContent(path),s->!s.startsWith("#"));
        Iterable<String>filtered = LazyQueries.skip(stringIterable,1);	//  Skip line: Not Available
        Predicate<String> isEvenLine = new Predicate<String>() {
            int counter = 0;
            @Override
            public boolean test(String s) {
                counter++;
                return counter%2==0;
            }
        };
        filtered = LazyQueries.filter(filtered,isEvenLine);//even lines filter
        return LazyQueries.map(filtered, WeatherInfo::valueOf);//to weatherinfo objects
    }
}
