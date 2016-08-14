package jwm.ir.entity;

import javax.persistence.*;

/**
 * Created by Jeff on 2016-07-24.
 */
@Entity
@Table(name = "validextensions")
public class ValidExtension {

    @Id
    @GeneratedValue
    private long extensionId;

    @Column(nullable = false)
    private int extType;

    @Column(nullable = false)
    private String ext;

    public ValidExtension() {}

    public ValidExtension(int extType, String ext) {
        this.ext = ext;
        this.extType = extType;
    }

    public String getExt() {
        return ext;
    }

}
