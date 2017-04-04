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

package weather.data;

import com.google.gson.Gson;
import util.IRequest;
import weather.data.dto.LocationDto;
import weather.data.dto.SearchDto;
import weather.data.dto.WeatherInfoDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.function.Predicate;

import static java.util.Arrays.asList;
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
    private static final String WEATHER_SEARCH_ARGS="&format=json&key=%s";

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
    private final Gson gson = new Gson();

    public WeatherWebApi(IRequest req) {
        this.req = req;
    }

    /**
     * E.g. http://api.worldweatheronline.com/free/v2/search.ashx?query=oporto&format=tab&key=*****
     */

    public Iterable<LocationDto> search(String query) {
        String path = WEATHER_HOST + WEATHER_SEARCH + WEATHER_SEARCH_ARGS;
        String url = String.format(path, query, WEATHER_TOKEN);
        Iterable<String> content = () -> req.getContent(url).iterator();
        SearchDto dto = gson.fromJson(join(content), SearchDto.class);
        return asList(dto.search_api.result);
    }

    /**
     * E.g. http://api.worldweatheronline.com/free/v2/search.ashx?query=oporto&format=tab&key=*****
     */
    public Iterable<WeatherInfoDto> pastWeather(
            double lat,
            double log,
            LocalDate from,
            LocalDate to
    ) {
        String query = lat + "," + log;
        String path = WEATHER_HOST + WEATHER_PAST +
                String.format(WEATHER_PAST_ARGS, query, from, to, WEATHER_TOKEN);
        Iterable<String> content = () -> req.getContent(path).iterator();
        Iterable<String> stringIterable = filter(content, s->!s.startsWith("#"));
        Iterable<String>filtered = skip(stringIterable,1);	//  Skip line: Not Available
        int[] counter = {0};
        Predicate<String> isEvenLine = item -> ++counter[0] % 2==0;
        filtered = filter(filtered,isEvenLine);//even lines filter
        return map(filtered, WeatherInfoDto::valueOf); //to weatherinfo objects
    }
}
