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

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by baltasarb on 3/18/2017.
 */
public class IteratorDistinct<T> implements Iterator<T> {

    private Iterator<T> data;
    private ArrayList<T> distinctData;
    private T nextItem;

    public IteratorDistinct(Iterator<T> data){
        this.data=data;
        distinctData = new ArrayList<>();
        nextItem = getNext();
    }

    private T getNext() {
        T item;
        while(data.hasNext()) {
            item = data.next();
            if(!distinctData.contains(item)) {
                distinctData.add(item);
                return item;
            }
        }
        return null;
    }

    @Override
    public boolean hasNext() {
        return nextItem != null;
    }

    @Override
    public T next() {
        T currentItem = nextItem;
        nextItem = getNext();
        return currentItem;
    }
}
