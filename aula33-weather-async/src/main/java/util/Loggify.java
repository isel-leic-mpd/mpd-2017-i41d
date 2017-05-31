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

import static java.lang.System.out;

/**
 * @author Miguel Gamboa
 *         created on 24-03-2017
 */
public class Loggify {
    /**
     * First approach
     */
    /*
    public static <T,R> Function<T,R> of(Function<T,R> inner, String msg) {
        return t -> {
            out.println(msg);
            return inner.apply(t);
        };
    }
    */

    /**
     * 3r approach
     */
    public static <T,R> Function<T,R> of(Function<T,R> inner, String msg) {
        return inner.compose(arg -> {
            out.println(msg);
            return arg;
        });
    }

    /**
     * 2nd approach
     */
    /*
    public static <T,R> Function<T,R> of(Function<T,R> inner, String msg) {
        Function<T, T> outer = new Logger(msg);
        return outer.andThen(inner);
    }
    */

    static class Logger<T> implements Function<T, T> {
        final String msg;

        public Logger(String msg) {
            this.msg = msg;
        }

        public T apply(T arg) {
            out.println(msg);
            return arg;
        }
    }
}
