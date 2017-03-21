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

/**
 * Created by baltasarb on 3/18/2017.
 */
public class IteratorSkip<T> implements Iterator<T> {

    private Iterator<T> iterator;

    public IteratorSkip(Iterator<T> iterator, int nr) {
        this.iterator = iterator;
        for (int i = 0; i < nr && iterator.hasNext(); i++) {
            iterator.next();
        }
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public T next() {
        return iterator.next();
    }
}
