package com.neptune.api.requestlater.domain;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinkNoFollow;
import org.glassfish.jersey.linking.InjectLinks;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.neptune.api.template.adapter.LinkAdapter;
import com.neptune.api.template.domain.DomainTemplate;

/**
 * Schedule Model.
 * 
 * @author Rafael R. Itajuba
 */
@Entity
@Table(name = "_schedules")
@XmlRootElement
public class Schedule extends DomainTemplate implements Delayed, Runnable {

    /**
     * 
     */
    private static final long serialVersionUID = -3131395094924167679L;

    final static Logger logger = LogManager.getLogger(Schedule.class);

    private Date atTime;

    private Boolean active;

    @InjectLinkNoFollow
    private Set<Request> requests;

    @InjectLinks({
            @InjectLink(value = "schedules/${instance.id}", rel = "self"),
            @InjectLink(value = "schedules/${instance.id}/requests", rel = "requests") })
    @XmlJavaTypeAdapter(LinkAdapter.class)
    private List<Link> links;

    public Schedule() {
        super();

        this.atTime = new Date();
        this.active = Boolean.TRUE;

        this.requests = new HashSet<Request>();
    }

    public Schedule(UUID id) {
        this();

        this.setId(id);
    }

    @Column(name = "active", nullable = false)
    public Boolean getActive() {
        return active;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "at_time", nullable = false)
    public Date getAtTime() {
        return atTime;
    }

    @Transient
    @XmlElement(name = "_links")
    public List<Link> getLinks() {
        return links;
    }

    @Override
    @XmlTransient
    public long getDelay(TimeUnit unit) {
        return unit.convert(atTime.getTime() - System.currentTimeMillis(),
                MILLISECONDS);
    }

    @OneToMany(mappedBy = "schedule", targetEntity = Request.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @XmlTransient
    @JsonIgnore
    public Set<Request> getRequests() {
        return this.requests;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setAtTime(Date atTime) {
        this.atTime = atTime;
    }

    public void setRequests(Set<Request> requests) {
        this.requests = requests;
    }

    @Override
    public int compareTo(Delayed o) {
        if (o == this) {
            return 0;
        }
        long diff = this.getDelay(MILLISECONDS) - o.getDelay(MILLISECONDS);
        return (diff < 0) ? -1 : (diff > 0) ? 1 : 0;
    }

    @Override
    public void run() {
        logger.debug("Firing " + this);

        // In JPA, this will be Lazy Load, and the data will load accordingly
        // In Memory Storage, requests is ensured to be always referenced
        // correctly
        logger.debug("Running Requests: " + this.getRequests());
        for (Request req : this.getRequests()) {
            req.process();
        }
    }

    @Override
    public String toString() {
        return "Schedule [id=" + this.getId() + ", createdOn="
                + this.getCreatedOn() + ", at=" + atTime + "]";
    }

}
