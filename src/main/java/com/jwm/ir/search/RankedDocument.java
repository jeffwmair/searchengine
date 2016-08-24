package com.jwm.ir.search;


/**
 * Created by Jeff on 2016-08-23.
 */
public class RankedDocument implements Document, Comparable<RankedDocument> {

    private final Document document;
    private final double ranking;
    public RankedDocument(double ranking, Document document) {
        this.document = document;
        this.ranking = ranking;
    }

    @Override
    public int getDocumentId() {
        return document.getDocumentId();
    }

    @Override
    public int getLength() {
        return document.getLength();
    }

    @Override
    public int getTermFrequency(String term) {
        return document.getTermFrequency(term);
    }

    public double getRanking() {
        return ranking;
    }

    @Override
    public int compareTo(RankedDocument o) {
        return (ranking < o.ranking) ? 1 : -1;
    }

    @Override
    public String toString() {
        return "RankedDocument with score '"+ranking+"'.  Document: '"+document+"'";
    }
}
