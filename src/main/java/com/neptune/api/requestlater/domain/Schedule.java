package com.neptune.api.requestlater.domain;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.GenericGenerator;

import com.neptune.api.template.domain.DomainTemplate;

/**
 * Schedule Model
 * 
 * @author Rafael R. Itajuba
 */
@Entity
@Table(name = "_schedules")
@XmlRootElement
public class Schedule extends DomainTemplate
        implements java.io.Serializable, Delayed, Runnable {

    final static Logger logger = LogManager.getLogger(Schedule.class);

    private static final long serialVersionUID = 1L;

    private UUID id;
    private Date createdOn;

    private Long time;

    private List<Request> requests;

    public Schedule() {
        super();

        this.id = UUID.randomUUID();
        this.requests = new LinkedList<Request>();
    }

    public Schedule(UUID id) {
        this();

        this.id = id;
    }

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)", name = "id", unique = true, nullable = false, updatable = false)
    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on", nullable = false, updatable = false)
    @Override
    public Date getCreatedOn() {
        return createdOn;
    }

    @Override
    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    @PrePersist
    @Override
    protected void onCreate() {
        this.createdOn = new Date();
    }

    @Column
    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    @OneToMany(mappedBy = "schedule", targetEntity = Request.class, fetch = FetchType.EAGER)
    public List<Request> getRequests() {
        return this.requests;
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
        return unit.convert(time - System.currentTimeMillis(), MILLISECONDS);
    }

    @Override
    public void run() {
        logger.debug("Firing Schedule(" + id + ")");

        for (Request req : this.getRequests()) {
            req.process();
        }
    }

    @Override
    public void copy(Object t) throws IllegalArgumentException {
        if (this.getClass() == t.getClass()) {
            Schedule target = (Schedule) t;
            this.setCreatedOn(target.getCreatedOn());
            this.setId(target.getId());
            this.setTime(target.getTime());
        }
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Schedule other = (Schedule) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
