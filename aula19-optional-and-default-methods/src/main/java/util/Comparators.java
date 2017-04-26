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
import java.util.function.Function;

/**
 * @author Miguel Gamboa
 *         created on 26-04-2017
 */
public class Comparators {
    public static <T, R extends Comparable<R>> ComparatorBy<T> comparing(Function<T, R> prop) {
        return (o1, o2) -> prop.apply(o1).compareTo(prop.apply(o2));
    }

    public interface ComparatorBy<T> extends Comparator<T> {

        public default ComparatorBy<T> invert() {
            return (o1, o2) -> compare(o2, o1);
        }

        public default <R extends Comparable<R>> ComparatorBy<T> thenBy(Function<T, R> prop) {
            Comparator<T> then = Comparators.comparing(prop); // Comparador criado 1 x
            return (o1, o2) -> {
                int res = compare(o1, o2);
                if(res != 0) return res;
                return then.compare(o1, o2); // Captura da variável then
            };
        }
    }
}

/*
public class Comparators {
    public static <T, R extends Comparable<R>> ComparatorBy<T> comparing(Function<T, R> prop) {
        Comparator<T> cmp = (o1, o2) -> prop.apply(o1).compareTo(prop.apply(o2));
        return new ComparatorBy<T>(cmp);
    }

    public static class ComparatorBy<T> implements Comparator<T> {
        final Comparator<T> cmp;

        public ComparatorBy(Comparator<T> cmp) {
            this.cmp = cmp;
        }

        @Override
        public int compare(T o1, T o2) {
            return cmp.compare(o1, o2);
        }

        public ComparatorBy<T> invert() {
            return new ComparatorBy<>((o1, o2) -> cmp.compare(o2, o1));
        }

        public <R extends Comparable<R>> ComparatorBy<T> thenBy(Function<T, R> prop) {
            Comparator<T> then = Comparators.comparing(prop); // Comparador criado 1 x
            return new ComparatorBy<>((o1, o2) -> {
                int res = compare(o1, o2);
                if(res != 0) return res;
                return then.compare(o1, o2); // Captura da variável then
            });
        }
    }
}
*/