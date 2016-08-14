package com.neptune.api.requestlater.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.neptune.api.template.domain.DomainTemplate;

/**
 * Request Model
 * 
 * @author Rafael Rabelo
 */
@Entity
@Table(name = "_requests")
@XmlRootElement
public class Request extends DomainTemplate implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String randomId;

    private Date createdOn;

    private Map<String, String> headers;

    private Schedule schedule;

    public Request() {
        super();

        this.createdOn = new Date();
        this.headers = new HashMap<String, String>();
    }

    public Request(Integer id, String randomId) {
        this();

        this.id = id;
        this.randomId = randomId;
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

    @ElementCollection
    @MapKeyColumn(name = "header")
    @Column(name = "value")
    @CollectionTable(name = "_headers", joinColumns = @JoinColumn(name = "id"))
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "schedule_id", referencedColumnName = "id")
    @XmlTransient
    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

}
