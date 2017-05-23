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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class App {
    public static void main(String[] args) {
        int[] nrs = IntStream
                .range(0, 1024)
                .toArray();

        List<String> l = Arrays
                .stream(nrs)             // IntStream
                .mapToObj(Integer::new)  // Stream<Integer>
                .map(Object::toString)   // Stream<String>
                // .parallel()          // !!!!!!! EXCEPÇÃO => estado mutável
                .reduce(
                        new ArrayList<>(),
                        (a, e) -> { a.add(e); return a;},
                        (l1, l2) -> { l1.addAll(l2); return l1;}
                );
        System.out.println(l);

        l = Arrays
                .stream(nrs)             // IntStream
                .mapToObj(Integer::new)  // Stream<Integer>
                .map(Object::toString)   // Stream<String>
                .parallel()
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
                /*
                .collect(
                        () -> new ArrayList<>(),
                        (a, e) -> a.add(e),
                        (l1, l2) -> l1.addAll(l2)
                );*/
        System.out.println(l);
    }
}
