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

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.System.out;
import static java.util.Arrays.stream;
import static util.StreamUtils.filterEvenLine;

public class App {

    static final String SRC = "past-weather-porto-2017-02-01-2017-04-30.txt";

    public static void main(String[] args) throws URISyntaxException, IOException {
        Path p = Paths.get(ClassLoader.getSystemResource(SRC).toURI());
        Stream<String> lines = Files.lines(p);
        int[] temperatures = parseTemperature(lines).toArray();
        // out.println(streamMax(lines));
        out.println(intStreamMax(stream(temperatures)));
        for (int i = 0; i < 10; i++) {
            // out.println(streamAverage1(stream(temperatures)));
            out.println(streamAverage2(stream(temperatures)));
            // out.println(streamAverage3(stream(temperatures)));
        }
    }

    /**
     * !!!! Side-effects => mutable shared state
     * !!!! Auxiliary int array od temperatures int[] temps
     * !!!! 2x iterate sequence!!!
     */
    static double streamAverage1(IntStream temperatures) {
        int[] temps = temperatures
                .parallel()
                .toArray();
        int[] sum = {0};
        stream(temps).parallel().forEach(t -> sum[0] += t);
        return ((double)sum[0])/temps.length;
    }

    /**
     * !!!! Side-effects => mutable shared state: sum and count
     */
    static double streamAverage2(IntStream temperatures) {
        int[] sum = {0};
        int[] count = {0};
        temperatures
                .parallel()
                .forEach(t -> {
                    sum[0] += t;
                    count[0]++;
                });
        return ((double)sum[0])/count[0];
    }

    static double streamAverage3(IntStream temperatures) {
        Averager avg = temperatures
                .mapToObj(nr -> new Averager(nr))
                .parallel()
                .reduce((prev, curr) -> prev.add(curr))
                .get();
        return ((double)avg.sum)/avg.count;
    }

    static class Averager{
        final int sum;
        final int count;

        public Averager(int sum, int count) {
            this.sum = sum;
            this.count = count;
        }

        public Averager(int nr) {
            sum = nr; count = 1;
        }

        public Averager add(Averager other) {
            return new Averager(this.sum + other.sum, this.count + other.count);
        }
    }

    static int streamMax(Stream<String> lines) {
        return filterEvenLine(lines
                .filter(s -> !s.startsWith("#"))// Filter comments
                .skip(1)                        // Skip line: Not Available
            )                                   // Filter even lines
                .map(l -> l.substring(14, 16))  // Extract Temperature
                .map(Integer::parseInt)         // -> Boxing
                .max(Integer::compare)          // -> Unboxing
                .get();
    }


    static int intStreamMax(IntStream temperatures) {
        return temperatures
            .max()                              // OptionalInt
            .getAsInt();
    }

    static IntStream parseTemperature(Stream<String> lines) {
        return filterEvenLine(lines
                .filter(s -> !s.startsWith("#"))// Filter comments
                .skip(1)                        // Skip line: Not Available
            )                                   // Filter even lines
            .map(l -> l.substring(14, 16))      // Extract Temperature
            .mapToInt(Integer::parseInt);
    }
}
