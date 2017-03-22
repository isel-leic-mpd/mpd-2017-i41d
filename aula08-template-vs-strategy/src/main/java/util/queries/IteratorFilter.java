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

import java.util.Iterator;
import java.util.function.Predicate;

/**
 * @author Miguel Gamboa
 *         created on 15-03-2017
 */
public class IteratorFilter<T> implements Iterator<T> {
    final Iterator<T> src;
    final Predicate<T> p;
    private T nextItem;

    public IteratorFilter(Iterator<T> src, Predicate<T> p) {
        this.src = src;
        this.p = p;
        this.nextItem = moveNext();
    }

    private T moveNext() {
        T item = null;
        while(src.hasNext()) {
            item = src.next();
            if(p.test(item))
                return item;
        }
        return null;
    }

    @Override
    public boolean hasNext() {
        return nextItem != null;
    }

    @Override
    public T next() {
        T curr = nextItem;
        nextItem = moveNext();
        return curr;
    }
}
