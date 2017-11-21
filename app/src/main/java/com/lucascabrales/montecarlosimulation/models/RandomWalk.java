package com.lucascabrales.montecarlosimulation.models;

/**
 * Created by lucascabrales on 11/20/17.
 */

public class RandomWalk {
    public float[] xArray;
    public float[] yArray;

    public RandomWalk(int iterations) {
        xArray = new float[iterations + 1];
        yArray = new float[iterations + 1];

        xArray[0] = 0;
        yArray[0] = 0;
    }
}
