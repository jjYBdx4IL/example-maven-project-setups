package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.api.jpa;

import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.api.dto.ExampleItemDTO;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
@Entity
public class ExampleItem implements Serializable {

    /**
     * @return the data1
     */
    public String getData1() {
        return data1;
    }

    /**
     * @param data1 the data1 to set
     */
    public void setData1(String data1) {
        this.data1 = data1;
    }

    /**
     * @return the data2
     */
    public String getData2() {
        return data2;
    }

    /**
     * @param data2 the data2 to set
     */
    public void setData2(String data2) {
        this.data2 = data2;
    }

    @Id
    @GeneratedValue
    int id;

    @Basic
    private String data1;

    @Basic
    private String data2;

    @Version
    long version;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SomeEntityWithVersionAndIndex [id=");
        builder.append(id);
        builder.append(", data=");
        builder.append(getData1());
        builder.append(", data2=");
        builder.append(getData2());
        builder.append(", version=");
        builder.append(version);
        builder.append("]");
        return builder.toString();
    }

    public ExampleItemDTO toDTO() {
        return new ExampleItemDTO(data1, data2);
    }
    
    public static ExampleItem fromDTO(ExampleItemDTO dto) {
        ExampleItem item = new ExampleItem();
        item.setData1(dto.getData1());
        item.setData2(dto.getData2());
        return item;
    }
}
