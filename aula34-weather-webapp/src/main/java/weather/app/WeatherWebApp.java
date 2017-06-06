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

package weather.app;

import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletHolder;
import util.HttpRequest;
import util.HttpServer;
import weather.WeatherService;
import weather.WeatherServiceCache;
import weather.controllers.WeatherController;
import weather.data.WeatherWebApi;

import static java.lang.ClassLoader.getSystemResource;

public class WeatherWebApp {
    public static void main(String[] args) throws Exception {
        try(HttpRequest http = new HttpRequest()) {
            WeatherService service = new WeatherServiceCache(new WeatherWebApi(http));
            WeatherController weatherCtr = new WeatherController(service);

            ServletHolder holderHome = new ServletHolder("static-home", DefaultServlet.class);
            String resPath = getSystemResource("public").toString();
            holderHome.setInitParameter("resourceBase", resPath);
            holderHome.setInitParameter("dirAllowed", "true");
            holderHome.setInitParameter("pathInfoOnly", "true");

            int[] counter = {0};
            new HttpServer(3000)
                    .addHandler("/search", weatherCtr::getSearch)
                    .addHandler("/search/city", weatherCtr::searchCity)
                    .addHandler("/weather/*", weatherCtr::last30daysWeather)
                    .addServletHolder("/public/*", holderHome)
                    .run();
        }
    }
}
