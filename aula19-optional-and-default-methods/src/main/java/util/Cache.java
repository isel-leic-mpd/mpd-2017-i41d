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

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Miguel Gamboa
 *         created on 18-04-2017
 */
public class Cache<T,R> {
    public static <T,R> Function<T,R> memoize(Function<T,R> src) {
        final Map<T, R> data = new HashMap<>();
        return key -> {
            R res = data.get(key);
            if(res == null) {
                res = src.apply(key);
                data.put(key, res);
            }
            return res;
        };
    }
}
