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

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Miguel Gamboa
 *         created on 03-05-2017
 */
public interface Queryable<T> {

    abstract boolean tryAdvance(Consumer<T> action);

    static <T> Queryable<T> of(Iterable<T> src) {
        Iterator<T> iter = src.iterator();
        return action -> {
            if(iter.hasNext())
                action.accept(iter.next());
            return iter.hasNext();
        };
    }

    default <R> Queryable<R> map(Function<T, R> mapper) {
        /**
         * This is the implementation of the tryAdvance(Consumer<R>)
         * of the new Queryable<R>
         */
        return action -> {
            // Call the tryAdvance of data source.
            // The action is the end user operation e.g. System.out::println
            return tryAdvance(item -> action.accept(mapper.apply(item)));
        };
        // <=> return action -> tryAdvance(item -> action.accept(mapper.apply(item)));
    }

    default Queryable<T> skip(int nr){
        // Skip nr of items on data source
        for (int i = 0; i < nr; i++) tryAdvance(item ->{});
        return this::tryAdvance;
    }

    default Queryable<T> filter(Predicate<T> p){
        return action -> {
            boolean hasNext = false;
            boolean[] found = {false};
            do{
                hasNext = tryAdvance(item -> {
                    if(p.test(item)){
                        found[0] = true;
                        action.accept(item);
                    }
                });
            } while(found[0] == false && hasNext);
            return hasNext;
        };
    }

    default Optional<T> max(Comparator<T> cmp) {
        return Optional.empty();
    }

    default Queryable<T> distinct() {
        throw new NotImplementedException();
    }
}
