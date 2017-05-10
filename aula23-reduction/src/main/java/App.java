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

import util.StreamUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Spliterator;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.lang.System.out;
import static util.StreamUtils.filterEvenLine;

public class App {

    static final String SRC = "past-weather-porto-2017-02-01-2017-04-30.txt";

    public static void main(String[] args) throws URISyntaxException, IOException {
        Path p = Paths.get(ClassLoader.getSystemResource(SRC).toURI());
        Stream<String> lines = Files.lines(p);

        filterEvenLine( lines
                .filter(s -> !s.startsWith("#"))// Filter comments
                .skip(1)                        // Skip line: Not Available
            )                                   // Filter even lines
            .map(l -> l.substring(14, 16))      // Extract Temperature
            .map(Integer::parseInt)
            .max(Integer::compare)
            .ifPresent(out::println);
    }
}
