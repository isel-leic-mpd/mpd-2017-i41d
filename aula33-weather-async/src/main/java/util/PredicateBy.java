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

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Steven Brito
 *         created on 03-05-2017
 */
public interface PredicateBy<T> extends Predicate<T> {

    static <T, R extends Comparable<R>> PredicateBy<T> equalsBy(Function<T, R> func, R r) {
        return o1 -> func.apply(o1).compareTo(r) == 0;
    }

    default PredicateBy<T> not() {
        return o1 -> !test(o1);
    }

    default PredicateBy<T> and(PredicateBy<T> p) {
        return o1 -> test(o1) && p.test(o1);
    }
}
