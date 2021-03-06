import java.util.Random;
import java.util.Spliterator;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.lang.System.out;
import static java.util.Spliterator.ORDERED;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
public class App {

    public static void main(String[] args) {
        Stream<String> s = Stream.of("ola", "isel", "super");
        s.forEach(out::println);

        // Stream<String> e = Stream.<String>empty();
        // <=> parametro generico String é inferido
        Stream<String> e = Stream.empty();
        e.forEach(out::println);

        s = Stream.of("ola", "isel", "super");
        concat(s, Stream.of("o", "maior"))
                .forEach(out::println);
        // > ola
        // > isel
        // > super
        // > o
        // > maior

        /*
        Random r = new Random();
        IntStream
                .generate(r::nextInt)
                .limit(7)
                .forEach(out::println);
        */
        fibonacci1().limit(23).forEach(nr -> out.print(nr + " "));
        System.out.println();
        fibonacci2().limit(23).forEach(nr -> out.print(nr + " "));
        // > 0 1 1 2 3 5 8 13 21
    }

    /**
     * !!!!! Estado Mutavel  !!!! => Error prone
     */
    private static IntStream fibonacci1() {
        final int[] nrs = {0, 1}; // referência imutável
        return IntStream.generate(() -> {
            int tmp = nrs[1];
            nrs[1] = nrs[0] + nrs[1];
            nrs[0] = tmp;
            return nrs[0];
        });
    }

    private static IntStream fibonacci2() {
        // 1. usar o iterate()
        // 2. Sem partilha de estado mutável
        // Dica: tem que criar uma classe Auxiliar
        return Stream
                .iterate(new Fibonacci(), Fibonacci::calculate)
                .mapToInt(val-> val.second);
    }

    static class Fibonacci{
        final int first, second;

        public Fibonacci() {
            this(0, 1);
        }
        public Fibonacci(int first,int second){
            this.first = first;
            this.second = second;
        }
        public Fibonacci calculate(){
            return new Fibonacci(second,first + second);
        }
    }

    private static <T> Stream<T> concat(Stream<T> first, Stream<T> second) {
        Spliterator<T> iter1 = first.spliterator();
        Spliterator<T> iter2 = second.spliterator();
        Spliterator<T> iter = new AbstractSpliterator<T>(Long.MAX_VALUE, Spliterator.ORDERED) {
            boolean firstFinished = false;
            @Override
            public boolean tryAdvance(Consumer<? super T> action) {
                if(!firstFinished && iter1.tryAdvance(action)) return true;
                firstFinished = true;
                return iter2.tryAdvance(action);
            }
        };
        return StreamSupport.stream(iter, false);
    }


}
