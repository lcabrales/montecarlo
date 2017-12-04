package com.lucascabrales.montecarlosimulation.helpers;

import com.lucascabrales.montecarlosimulation.enums.Distribution;

import java.util.Random;

/**
 * Created by lucascabrales on 12/3/17.
 */

public class RandomGenerator {

    private Random mRandom;
    private Distribution mDistribution;
    private Double mMean, mDeviance, mMax;

    public RandomGenerator(String distribution, Double mean, Double deviance, Double max) {
        mRandom = new Random();
        mDistribution = Distribution.fromString(distribution);

        mMean = mean;
        mDeviance = deviance;
        mMax = max;
    }

    public RandomGenerator(String distribution, Double mean, Double deviance) {
        this(distribution, mean, deviance, null);
    }

    public RandomGenerator(String distribution, Double max) {
        this(distribution, null, null, max);
    }

    public int getNextValue() {
        switch (mDistribution) {
            case NORMAL:
                //RETORNA UN VALOR MAYOR QUE CERO SEGUN LA DISTRIBUCION NORMAL
                int normal = (int) (mRandom.nextGaussian() * mMean + mDeviance);
                return normal > 0 ? normal : 1;
            case POISSON:
                //RETORNA UN VALOR SEGUN LA DISTRIBUCION POISSON
                return getPoissonRandom(mMean);
            case UNIFORM:
                //RETORNA UN VALOR SEGUN LA DISTRIBUCION UNIFORME
                return (int) (mRandom.nextDouble() * mMax);
            default:
                return 0;
        }
    }

    /*
    * A random variable with Poisson distribution is equal to the number of times a given event
    * occurs within a fixed interval when the lengths of the intervals between events are independent
    * random variables with exponential distribution
    */
    private static int getPoissonRandom(double mean) {
        Random r = new Random();
        double L = Math.exp(-mean);
        int k = 0;
        double p = 1.0;
        do {
            p = p * r.nextDouble();
            k++;
        } while (p > L);
        return k - 1;
    }
}
