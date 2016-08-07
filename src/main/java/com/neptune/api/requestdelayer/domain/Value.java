package com.neptune.api.requestdelayer.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;

import com.neptune.api.template.domain.DomainTemplate;

import org.glassfish.jersey.linking.InjectLinks;

/**
 * Job model
 * 
 * @author Rafael Rabelo
 */
@Entity
@Table(name = "_values")
@XmlRootElement
public class Value extends DomainTemplate implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    @InjectLinks({
            @InjectLink(value = "/values/${instance.resourceId}", rel = "self", style = Style.RELATIVE_PATH),
            @InjectLink(value = "/values/${instance.resourceId}/watcher", rel = "watcher", style = Style.RELATIVE_PATH),
            @InjectLink(value = "/watchers/${instance.watcherId}/values", rel = "siblings", style = Style.RELATIVE_PATH) })
    @XmlElement(name = "_links")
    // @XmlJavaTypeAdapter(ListLinkAdapter.class)
    private List<Link> links;

    private Integer id;
    private String randomId;

    private String watcherId;

    private String value;
    private Date createdOn;

    public Value() {
        super();

        this.createdOn = new Date();
    }

    public Value(Integer id, String randomId, String watcherId) {
        super();

        this.id = id;
        this.randomId = randomId;
        this.watcherId = watcherId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    @XmlTransient
    @Override
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "random_id", unique = false, nullable = false, updatable = false, length = 7)
    @XmlTransient
    @Override
    public String getRandomId() {
        return this.randomId;
    }

    public void setRandomId(String randomId) {
        this.randomId = randomId;
    }

    @Column(name = "watcher_id", unique = false, nullable = false, length = 18)
    @XmlElement(name = "watcher")
    public String getWatcherId() {
        return watcherId;
    }

    public void setWatcherId(String watcherId) {
        this.watcherId = watcherId;
    }

    @Column(name = "value", nullable = false, length = 255)
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on", nullable = false, updatable = false)
    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    @PrePersist
    protected void onCreate() {
        this.createdOn = new Date();
    }

    /**
     * Value should be extracted always in this format. This is a build method
     * 
     * @param watcher
     * @return
     * @throws IOException
     */
    // public static Value extract(Watcher watcher) throws IOException {
    // Extractor extractor = new Extractor(watcher.getTarget(),
    // watcher.getSelector());
    // String data = extractor.request().extract();
    //
    // Value value = new Value();
    // value.setValue(data);
    // value.setWatcherId(watcher.getResourceId());
    //
    // return value;
    // }
}
