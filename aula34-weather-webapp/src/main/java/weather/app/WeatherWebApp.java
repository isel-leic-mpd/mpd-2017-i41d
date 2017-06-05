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
import util.HttpServer;

import static java.lang.ClassLoader.getSystemResource;

public class WeatherWebApp {
    public static void main(String[] args) throws Exception {
        /*
        try(IRequest http = new HttpRequest()) {
            WeatherService api = new WeatherServiceCache(new WeatherWebApi(http));
            List<String> cities = asList("Porto", "London", "Paris", "New%20York", "Barcelona");
            System.out.println("####################################");
            System.out.println("Warming Up....");
            cities
                    .stream()
                    .map(city -> api.search(city).join().findFirst().get().getLast30daysWeather().findFirst().get())
                    .forEach(l -> System.out.println(l));
        }
        */

        ServletHolder holderHome = new ServletHolder("static-home", DefaultServlet.class);
        String resPath = getSystemResource("public").toString();
        holderHome.setInitParameter("resourceBase", resPath);
        holderHome.setInitParameter("dirAllowed","true");
        holderHome.setInitParameter("pathInfoOnly","true");

        new HttpServer(3000)
                .addHandler("/ole", req -> "<h1>Titulo</h1><p>Ola Mundo</p>")
                .addHandler("/hello", req -> "<h1>Titulo</h1><p>Hello</p>")
                .addServletHolder("/public/*", holderHome)
                .run();
    }
}
