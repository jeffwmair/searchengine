package com.jwm.ir.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by Jeff on 2016-07-24.
 */
@Entity
@Table(name="summarydata_i")
public class SummaryData {

    public static final String ItemIndexedPageCount = "IndexedPageCount";

    @Id
    @Column(nullable = false)
    private String item;

    @Column(nullable = false)
    public long value;

    @Column(nullable = false)
    private Date updatedate;

    public void update(String itemName, long value) {
        this.item = itemName;
        this.value = value;
        this.updatedate = new Date();
    }

}
