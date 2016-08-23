package jwm.ir.service;

/**
 * Created by Jeff on 2016-08-22.
 */
public interface InverseDocumentFrequencyCalculator {
    double calculate(int numberOfIndexedDocuments, int queryTermDocumentFrequency);
}
