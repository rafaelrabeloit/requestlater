package com.neptune.api.requestlater.domain;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
@Table(name = "_schedules")
@XmlRootElement
public class Schedule extends DomainTemplate implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String randomId;

    private Date createdOn;

    private List<Request> requests;

    public Schedule() {
        super();

        this.createdOn = new Date();
        this.requests = new LinkedList<Request>();
    }

    public Schedule(Integer id, String randomId) {
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

    @OneToMany(mappedBy = "schedule", targetEntity = Request.class, fetch = FetchType.EAGER)
    public List<Request> getRequests() {
        return this.requests;
    }

    public void setRequests(List<Request> requests) {
        this.requests = requests;
    }
}
