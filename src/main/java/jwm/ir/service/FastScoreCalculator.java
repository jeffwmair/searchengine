package jwm.ir.service;

import java.util.Map;

public class FastScoreCalculator {

    private final WeightedTermFrequencyCalculator termFrequencyCalculator;

    public FastScoreCalculator(WeightedTermFrequencyCalculator termFrequencyCalculator) {
        this.termFrequencyCalculator = termFrequencyCalculator;
    }

    public double calculateScore(Map<String,Integer> queryTermFrequencies) {

        for(String term : queryTermFrequencies.keySet()) {
            
            double weightedFrequency = termFrequencyCalculator.calculateTermFrequency(queryTermFrequencies.get(term));
            //$q_idf = log(($numDocs/ $allTermIds[$qt]), 10);
            //$w_tq = $q_idf * $q_tf;

        }
        return 0;
    }

}
