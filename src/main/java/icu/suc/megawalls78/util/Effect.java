package icu.suc.megawalls78.util;

import java.util.function.Consumer;

public class Effect<T> {

    private final Consumer<T> consumer;

    private Effect(Consumer<T> consumer) {
        this.consumer = consumer;
    }

    public void play(T t) {
        consumer.accept(t);
    }

    public static <T> Effect<T> create(Consumer<T> consumer) {
        return new Effect<>(consumer);
    }
}
