package com.jwm.ir.search;

/**
 * Created by Jeff on 2016-08-22.
 */
public class InverseDocumentFrequencyCalculatorImpl implements InverseDocumentFrequencyCalculator {

    @Override
    public double calculate(int numberOfIndexedDocuments, int queryTermDocumentFrequency) {
        return Math.log10(numberOfIndexedDocuments / queryTermDocumentFrequency );
    }
}
