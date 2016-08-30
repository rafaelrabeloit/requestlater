package com.neptune.api.requestlater.domain;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private List<Request> requests;

    public Schedule() {
        super();

        this.requests = new LinkedList<Request>();
    }

    public Schedule(UUID id) {
        this();

        this.setId(id);
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "at_time")
    public Date getAtTime() {
        return atTime;
    }

    @OneToMany(mappedBy = "schedule", targetEntity = Request.class, fetch = FetchType.LAZY)
    @XmlTransient
    public List<Request> getRequests() {
        return this.requests;
    }

    public void setAtTime(Date atTime) {
        this.atTime = atTime;
    }

    public void setRequests(List<Request> requests) {
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
    public long getDelay(TimeUnit unit) {
        return unit.convert(atTime.getTime() - System.currentTimeMillis(),
                MILLISECONDS);
    }

    @Override
    public void run() {
        logger.debug("Firing Schedule(" + this.getId() + ")");

        // In JPA, this will be Lazy Loading, and the data will load accordingly
        // In Memory Storage, requests is ensured to be always referenced
        // correctly
        for (Request req : this.getRequests()) {
            req.process();
        }
    }

    @Override
    public String toString() {
        return "Schedule [id=" + this.getId() + ", createdOn="
                + this.getCreatedOn() + ", at=" + atTime + ", requests="
                + requests + "]";
    }

    @Override
    public void copy(Object t) throws IllegalArgumentException {
        if (this.getClass() == t.getClass()) {
            Schedule target = (Schedule) t;
            this.setCreatedOn(target.getCreatedOn());
            this.setId(target.getId());
            this.setAtTime(target.getAtTime());
        }
    }

}
