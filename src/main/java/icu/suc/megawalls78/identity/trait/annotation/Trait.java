package icu.suc.megawalls78.identity.trait.annotation;

import icu.suc.megawalls78.identity.trait.passive.NullPassive;
import icu.suc.megawalls78.identity.trait.passive.Passive;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Trait {

    String value() default "";

    float cost() default -1;

    long cooldown() default -1;

    long duration() default -1;

    int charge() default -1;

    Class<? extends Passive> internal() default NullPassive.class;
}
