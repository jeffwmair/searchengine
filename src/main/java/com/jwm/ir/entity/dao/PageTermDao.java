package com.jwm.ir.entity.dao;

/**
 * Created by Jeff on 2016-08-05.
 */
public interface PageTermDao {
    void create(long pageId, String termValue, int termFrequency);
    boolean termsAlreadyExist(long pageId);
}
