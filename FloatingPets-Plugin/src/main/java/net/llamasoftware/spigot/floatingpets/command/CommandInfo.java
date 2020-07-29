package net.llamasoftware.spigot.floatingpets.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInfo {

    String name();

    String[] aliases() default {};

    int minimumArguments() default 0;

    boolean inGame() default false;

    boolean list() default true;

    boolean petContext() default true;

    boolean activePets() default true;

}