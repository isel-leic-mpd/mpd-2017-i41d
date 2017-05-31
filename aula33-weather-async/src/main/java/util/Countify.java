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

/**
 * @author Miguel Gamboa
 *         created on 24-03-2017
 */
public class Countify {

    static class Counter<T, R> implements ICounter<T, R> {
        private int count=0;
        private final Function<T, R> inner;

        public Counter(Function<T, R> inner) {
            this.inner = inner;
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public void reset() {
            count =0;
        }

        @Override
        public R apply(T arg) {
            count++;
            return inner.apply(arg);
        }
    }

    public static <T,R> ICounter of(Function<T,R> inner) {
        return new Counter<>(inner);
    }
}
