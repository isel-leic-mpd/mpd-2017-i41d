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

package util;

import java.util.Comparator;
import java.util.Spliterator;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Miguel Gamboa
 *         created on 09-05-2017
 */
public class StreamUtils {

    public static <T> Stream<T> filterEvenLine(Stream<T> src) {
        Spliterator<T> iter = src.spliterator();
        Spliterator<T> res = new AbstractSpliterator<T>(Long.MAX_VALUE, Spliterator.ORDERED ) {
            @Override
            public boolean tryAdvance(Consumer<? super T> action) {
                return iter.tryAdvance(item -> {})
                        ? iter.tryAdvance(action)
                        : false;
            }
        };
        return StreamSupport.stream(res, false);
    }

    public static <T> Stream<T> distinct(Stream<T> src, Comparator<T> cmp) {
        Spliterator<T> iter = src.spliterator();
        Spliterator<T> res = new AbstractSpliterator<T>(Long.MAX_VALUE, Spliterator.ORDERED ) {
            @Override
            public boolean tryAdvance(Consumer<? super T> action) {
                return false;
            }
        };
        return StreamSupport.stream(res, false);
    }
}
