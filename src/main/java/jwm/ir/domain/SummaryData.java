package jwm.ir.domain;

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

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    @Id
    @Column(nullable = false)
    private String item;

    @Column(nullable = false)
    private long val;

    @Column(nullable = false)
    private Date updatedate;

}
