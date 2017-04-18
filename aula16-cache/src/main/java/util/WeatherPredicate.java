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

import weather.data.dto.WeatherInfoDto;

/**
 * @author Miguel Gamboa
 *         created on 14-03-2017
 */

/**
 * This is a functional interface because it has one and only one method.
 * Thus we can assign a lambda expression to every place of WeatherPredicate
 * type.
 */
@FunctionalInterface
public interface WeatherPredicate {

    boolean test(WeatherInfoDto item);

    /*
     * Dá erro de compilação porque uma interface anotada com @FunctionalInterface
     * Só pode ter 1 método abstracto.
     *
     * void bar();
     */
}
