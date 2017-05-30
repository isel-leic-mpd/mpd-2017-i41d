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

import java.util.Random;

class App{

    private static final Random random = new Random();

    public static void main(String [] args) throws Exception{
        System.out.println(calculatePrice("bag"));
        System.out.println(calculatePrice("tablet"));
    }

    private static double calculatePrice(String product) {
        delay(3000);
        double res = random.nextDouble() * product.charAt(0) + product.charAt(1);
        return ((int) (res * 100)) / 100.0;
    }

    private static void delay(long milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}