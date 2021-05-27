package helloworld.rest.jpa;

import java.io.Serializable;
import java.time.Instant;
import java.time.OffsetDateTime;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

@SuppressWarnings("serial")
@Entity
@Table
public class Article implements Serializable {

    public Article() {
    }

    @Id
    @GeneratedValue
    private Long id;

    @Basic(optional = false)
    private String text;

    @Basic(optional = false)
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE") // required, otherwise saved as timestamp without tz
    private Instant created;
    
    private OffsetDateTime modified;

    @Version
    private int version;

    public Long getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public OffsetDateTime getModified() {
        return modified;
    }

    public void setModified(OffsetDateTime modified) {
        this.modified = modified;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Article [id=").append(id).append(", text=").append(text).append(", created=").append(created)
                .append(", modified=").append(modified).append(", version=").append(version).append("]");
        return builder.toString();
    }
}
