import util.Loggify;
import util.queries.EagerQueries;
import util.HttpRequest;
import util.IRequest;
import util.queries.LazyQueries;
import weather.WeatherService;
import weather.data.WeatherWebApi;
import weather.data.dto.LocationDto;
import weather.model.Location;

import java.util.function.Function;

import static java.lang.System.out;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
public class App {

    public static void main(String[] args) {
        lazy();
    }
    public static void eager() {
        IRequest req = new HttpRequest(); // new FileRequest();
        /**
         * req::getContent --> inner
         * "Requestin..."  --> msg
         */
        Function<String, Iterable<String>> log = Loggify.of(req::getContent, "Requesting...");
        WeatherWebApi api = new WeatherWebApi(log::apply);
        out.println("Searching...");
        Iterable<LocationDto> locals = api.search("oporto");
        out.println("Filtering...");
        locals = EagerQueries.filter(locals, l -> {out.println("filter...." + l); return l.getLatitude() > 0; });
        out.println("MApping...");
        Iterable<String> locs = EagerQueries.map(locals, l -> {out.println("map..." + l); return l.getRegion();});
        // out.println(locs.iterator().next());
    }

    public static void lazy() {
        IRequest req = new HttpRequest(); // new FileRequest();
        /**
         * req::getContent --> inner
         * "Requestin..."  --> msg
         */
        Function<String, Iterable<String>> log = Loggify.of(req::getContent, "Requesting...");
        WeatherService api = new WeatherService(new WeatherWebApi(log::apply));
        out.println("Searching...");
        Iterable<Location> locals = api.search("oporto");
        out.println("Filtering...");
        locals = LazyQueries.filter(locals, l -> {out.println("filter...." + l); return l.getLatitude() > 0; });
        Location loc = locals.iterator().next();
        out.println(loc); // Print first LocationDto in north hemisphere

        // Get the past weather for loc
        loc.getLast30daysWeather().forEach(out::println);
    }
}

