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

package util.queries;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * @author Miguel Gamboa
 *         created on 15-03-2017
 */
public class LazyQueries {

    public static <T> Iterable<T> filter(Iterable<T> data, Predicate<T> p) {
        // A lambda corresponde à implementação de Iterable::iterator()
        return () -> new IteratorFilter(data.iterator(), p);
    }

    public static <T, R> Iterable<R> map(Iterable<T> data, Function<T, R> mapper) {

        return () -> new IteratorMap(data.iterator(), mapper);
    }

    public static <T> Iterable<T> distinct(Iterable<T> data) {
        return () -> new IteratorDistinct(data.iterator());
    }

    public static <T> Iterable<T> skip(Iterable<T> data, int nr) {
        return () -> {
            Iterator<T> iter = data.iterator();
            for (int i = 0; i < nr && iter.hasNext(); i++) iter.next();
            return iter;
        };
    }

    public static <T> int count(Iterable<T> data) {
        int size = 0;
        for (T item: data) {
            size++;
        }
        return size;
    }


    public static <T> String join(Iterable<T> src) {
        String res = "";
        for (T item: src) {
            res += item.toString();
        }
        return res;
    }

    public static <T> Iterable<T> peek(Iterable<T> src, Consumer<T> action) {
        return () -> new Iterator<T>() {
            final Iterator<T> iter = src.iterator();
            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public T next() {
                T curr = iter.next();
                action.accept(curr);
                return curr;
            }
        };
    }

    public static <T> Iterable<T> iterate(T from, T to, UnaryOperator<T> inc) {
        return () -> new Iterator<T>() {
            T next = from;
            boolean finish = false;

            @Override
            public boolean hasNext() {
                return !finish;
            }

            @Override
            public T next() {
                if(finish) throw new IndexOutOfBoundsException("Iteration overlaps the value of to: " + to);
                if(next.equals(to))
                    finish = true; // The to bound is inclusive. Finishes on next step.
                T curr = next;
                next = inc.apply(curr);
                return curr;
            }
        };
    }

    public static <T> boolean containsAll(Collection<T> col, Iterable<T> data) {
        for (T item:data) {
            if(!col.contains(item))
                return false;
        }
        return true;
    }

    public static <T> Optional<T> max(Iterable<T> src, Comparator<T> cmp) {
        Iterator<T> iter = src.iterator();
        if(!iter.hasNext()) return Optional.empty();
        T first = iter.next();
        while (iter.hasNext()) {
            T curr = iter.next();
            if(cmp.compare(curr, first) > 0)
                first = curr;
        }
        return Optional.of(first);
    }

    public static <T extends Comparable<T>> Optional<T> max(Iterable<T> src) {
        return max(src, (o1, o2) -> o1.compareTo(o2));
    }

    public static <T> Iterable<T> filterEvenLine(Iterable<T> src) {
        Iterator<T> iter = src.iterator();
        return () -> new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public T next() {
                if(iter.hasNext()){
                    iter.next();
                    return iter.next();
                }
                throw new IndexOutOfBoundsException();
            }
        };
    }
}
