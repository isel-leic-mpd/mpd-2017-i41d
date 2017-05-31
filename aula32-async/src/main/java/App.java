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
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

class App{

    private static final Random random = new Random();

    public static void main(String [] args) throws Exception{
        testCalculatePriceAsync();
    }

    private static void testCalculatePriceAsync() {
        System.out.println("Requesting price for bag");
        calculatePriceAsync2("bag")
                .thenAccept(System.out::println);
        System.out.println("Requesting price for tablet");
        calculatePriceAsync2("tablet")
                .exceptionally(ex -> {
                    System.out.println("Pelase get another product");
                    return null;
                })
                .thenAccept(System.out::println)
                .join();
    }

    private static void testCalculatePriceCallback() {
        System.out.println("Requesting price for bag");
        calculatePrice("bag", (err, price) -> System.out.println(price));
        System.out.println("Requesting price for tablet");
        calculatePrice("tablet", (err, price) -> {
            if(err != null) {
                System.out.println("Pelase get another product");
            }
            else
                System.out.println(price);

        });
    }

    private static double calculatePrice(String product) {
        delay(3000);
        double res = random.nextDouble() * product.charAt(0) + product.charAt(1);
        return ((int) (res * 100)) / 100.0;
    }

    /**
     * The callback is invoked when it finishes calculating the price.
     */
    private static void calculatePrice(String product, BiConsumer<RuntimeException, Double> callback) {
        // !!!!! CUIDADO não fazer isto
        Thread th = new Thread(() -> {
            delay(3000);
            if(product.length() > 4 ) callback.accept(new RuntimeException("Illegal Product " + product), null);
            double res = random.nextDouble() * product.charAt(0) + product.charAt(1);
            double price = ((int) (res * 100)) / 100.0;
            callback.accept(null, price);
        });
        th.start();
    }

    private static CompletableFuture<Double> calculatePriceAsync(String product) {
        CompletableFuture<Double> promise = new CompletableFuture<>();
        // !!!!! CUIDADO não fazer isto
        new Thread(() -> {
            delay(3000);
            if(product.length() > 4 ) promise.completeExceptionally(new RuntimeException("Illegal Product " + product));
            double res = random.nextDouble() * product.charAt(0) + product.charAt(1);
            double price = ((int) (res * 100)) / 100.0;
            promise.complete(price);
        }).start();
        return promise;
    }


    private static CompletableFuture<Double> calculatePriceAsync2(String product) {
        CompletableFuture<Double> promise = CompletableFuture.supplyAsync(() -> {
            delay(3000);
            if(product.length() > 4 ) throw new RuntimeException("Illegal Product " + product);
            double res = random.nextDouble() * product.charAt(0) + product.charAt(1);
            double price = ((int) (res * 100)) / 100.0;
            return price;
        });
        return promise;
    }

    private static void delay(long milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}