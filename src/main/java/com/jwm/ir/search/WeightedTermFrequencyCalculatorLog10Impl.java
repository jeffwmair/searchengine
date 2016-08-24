package com.jwm.ir.search;

/**
 * Created by Jeff on 2016-08-18.
 *  Implementation of WeightedTermFrequency calculation that uses log base 10.
 */
public class WeightedTermFrequencyCalculatorLog10Impl implements WeightedTermFrequencyCalculator {
    @Override
    public double calculateTermFrequency(int rawTermFrequency) {

        if (rawTermFrequency == 0) {
            return 0;
        }

        return 1 + Math.log10(rawTermFrequency);
    }
}
