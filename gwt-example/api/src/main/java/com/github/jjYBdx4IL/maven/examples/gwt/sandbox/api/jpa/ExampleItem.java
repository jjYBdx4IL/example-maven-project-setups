package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.api.jpa;

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

    @Id
    @GeneratedValue
    int id;

    @Basic
    String data1;

    @Basic
    String data2;

    @Version
    long version;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SomeEntityWithVersionAndIndex [id=");
        builder.append(id);
        builder.append(", data=");
        builder.append(data1);
        builder.append(", data2=");
        builder.append(data2);
        builder.append(", version=");
        builder.append(version);
        builder.append("]");
        return builder.toString();
    }

}
