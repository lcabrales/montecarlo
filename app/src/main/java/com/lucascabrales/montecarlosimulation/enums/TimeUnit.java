package com.lucascabrales.montecarlosimulation.enums;

/**
 * Created by lucascabrales on 12/3/17.
 */

public enum TimeUnit {
    SECONDS("Segundos"),
    MINUTES("Minutos"),
    HOURS("Horas");

    public String name;
    public static final String KEY = "TimeUnitKey";

    TimeUnit(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static TimeUnit fromString(String text) {
        for (TimeUnit b : TimeUnit.values()) {
            if (b.name.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}

