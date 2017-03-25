package com.neptune.api.requestlater.domain;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.neptune.api.requestlater.DataExtractor;
import com.neptune.api.template.adapter.LinkAdapter;
import com.neptune.api.template.domain.DomainTemplate;

import com.google.ical.compat.jodatime.DateTimeIterable;
import com.google.ical.compat.jodatime.DateTimeIterator;
import com.google.ical.compat.jodatime.DateTimeIteratorFactory;

/**
 * Schedule Model.
 * 
 * @author Rafael R. Itajuba
 */
@Entity
@Table(name = "_schedules")
@XmlRootElement
public class Schedule extends DomainTemplate implements Delayed, Runnable {

    private static final long serialVersionUID = -3131395094924167679L;

    static final Logger LOGGER = LogManager.getLogger(Schedule.class);

    private Date atTime;

    private Boolean active;

    private Date ocurrence;

    private String recurrence;

    private Map<String, List<String>> variables;

    @InjectLinkNoFollow
    private Set<Request> requests;

    @InjectLinks({
            @InjectLink(value = "schedules/${instance.id}", rel = "self"),
            @InjectLink(value = "schedules/${instance.id}/requests", rel = "requests") })
    @XmlJavaTypeAdapter(LinkAdapter.class)
    private List<Link> links;

    public Schedule() {
        super();

        this.active = Boolean.TRUE;

        this.requests = new HashSet<Request>();
        this.variables = new HashMap<>();
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
    @Column(name = "at_time", nullable = true)
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

    @XmlTransient
    @JsonIgnore
    @Transient
    public Map<String, List<String>> getVariables() {
        return this.variables;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ocurrence", nullable = false)
    public Date getOcurrence() {
        if (this.ocurrence == null) {
            this.ocurrence = new Date();
        }
        return this.ocurrence;
    }

    @Column(name = "recurrence", nullable = true, length = 255)
    public String getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(String recurrence) {
        this.recurrence = recurrence;
    }

    public void setOcurrence(Date ocurrence) {
        this.ocurrence = ocurrence;
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
        LOGGER.debug("Firing " + this);

        // In JPA, this will be Lazy Load, and the data will load accordingly
        // In Memory Storage, requests is ensured to be always referenced
        // correctly
        LOGGER.debug("Running Requests: " + this.getRequests());
        for (Request req : this.getRequests()) {
            req.process();
        }

        // calculate the next schedule time based on recurrence rule
        foresee();
    }

    @Override
    public String toString() {
        return "Schedule [id=" + this.getId() + ", createdOn="
                + this.getCreatedOn() + ", at=" + atTime + "]";
    }

    /**
     * Add (or update) variables in this "global", using a local context rules
     * that is a request, based on its response.
     * 
     * @param response
     *            response from where data will be extracted
     * @param context
     *            request from that response, that contains rules
     */
    public void addVariables(Response response, Request context) {
        Map<String, List<String>> variables;

        // read response's headers and add as variables
        variables = response.getHeaders().entrySet().stream().collect(Collectors
                .toMap(Map.Entry::getKey, e -> Arrays.asList(e.getValue())));
        this.variables.putAll(variables);

        // extract data using requests rules
        variables = DataExtractor.extractWithSelector(response.getContent(),
                context.getExtractors());
        this.variables.putAll(variables);

    }

    // TODO: Treat parse exception
    public void foresee() {
        if (this.recurrence == null || this.recurrence.length() == 0) {
            this.setActive(false);
            return;
        }
        
        DateTime base = new DateTime(this.getOcurrence());

        if (this.atTime != null
                && this.atTime.getTime() > DateTime.now().getMillis())
            // if the foreseen time is still to come, then it is still valid
            return;

        if (this.recurrence != null) {
            try {
                DateTimeIterable range = DateTimeIteratorFactory
                        .createDateTimeIterable(this.recurrence, base,
                                DateTimeZone.UTC, true);
                DateTimeIterator it = range.iterator();

                base = null;
                while (it.hasNext()) {
                    base = it.next();
                    if (base != null && !base.isBeforeNow())
                        break;
                }

                if (base != null) {
                    // turns this time in the last occurrence
                    if (this.atTime != null)
                        this.ocurrence = this.atTime;

                    // the schedule is the next time
                    this.atTime = new Date(base.getMillis());
                } else {
                    // If there is NOT another iteration for this watcher, then
                    // disable it!
                    this.active = false;
                }
            } catch (ParseException e) {
                System.out.print(e);
            }
        }
    }
}
