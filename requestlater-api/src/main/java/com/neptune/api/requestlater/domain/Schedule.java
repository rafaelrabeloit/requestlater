package com.neptune.api.requestlater.domain;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.persistence.Access;
import javax.persistence.AccessType;
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
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.ical.compat.jodatime.DateTimeIterable;
import com.google.ical.compat.jodatime.DateTimeIterator;
import com.google.ical.compat.jodatime.DateTimeIteratorFactory;
import com.neptune.api.requestlater.DataExtractor;
import com.neptune.api.template.adapter.LinkAdapter;
import com.neptune.api.template.domain.DomainTemplate;

/**
 * Schedule Model.
 * 
 * @author Rafael R. Itajuba
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = "_schedules")
@XmlRootElement
public class Schedule extends DomainTemplate implements Delayed, Runnable {

    private static final long serialVersionUID = -3131395094924167679L;

    static final Logger LOGGER = LogManager.getLogger(Schedule.class);

    // time for this schedule to fire
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "incoming_time", nullable = true)
    private Date incomingTime;

    // the last time it was fired
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_time", nullable = true)
    private Date lastTime;

    // if it is active at the moment or not
    @Column(name = "active", nullable = false)
    private Boolean active;

    // the rule for this scheduling
    @Column(name = "at", nullable = false, length = 255)
    private String at;

    // variables for this schedule
    @Transient
    private Map<String, List<String>> variables;

    // request associates to it
    @InjectLinkNoFollow
    @OneToMany(mappedBy = "schedule", targetEntity = Request.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Request> requests;

    // links for HATEOAS
    @InjectLinks({
            @InjectLink(value = "schedules/${instance.id}", rel = "self"),
            @InjectLink(value = "schedules/${instance.id}/requests", rel = "requests") })
    @XmlJavaTypeAdapter(LinkAdapter.class)
    @Transient
    private List<Link> links;

    /**
     * Default construct. Used for Injection and for default values (including
     * random UUID)
     */
    public Schedule() {
        super();

        this.active = Boolean.TRUE;

        this.requests = new HashSet<Request>();
        this.variables = new HashMap<>();
    }

    /**
     * Constructor with id. TODO: Should be on api-template?
     * 
     * @param id
     */
    public Schedule(UUID id) {
        this();

        this.setId(id);
    }

    /**
     * Constructor with 'at' rule
     * 
     * @param at
     *            rule for recurrence or scheduling
     */
    public Schedule(String at) {
        this();

        this.setAt(at);
    }

    /**
     * Get links injected for HATEAOS
     * 
     * @return list of links, to be included in json
     */
    @XmlElement(name = "_links")
    public List<Link> getLinks() {
        return this.links;
    }

    /**
     * @return Active state for this schedule
     */
    public Boolean getActive() {
        return this.active;
    }

    /**
     * Set active state for element. It also sets incoming time to null if
     * deactivating this schedule.
     * 
     * @param value
     *            boolean value for active state.
     */
    public void setActive(Boolean value) {
        this.active = value;
        if (!this.active) {
            this.incomingTime = null;
        }
    }

    /**
     * Get the closes time that this schedule is appointed for. Null if this
     * schedule is inactive. Read-only by the API.
     * 
     * @return Date when this schedule should fire. Null if it is inactive.
     */
    public Date getIncomingTime() {
        return this.incomingTime;
    }

    /**
     * Get the last time that this schedule was fired. Read-only by the API.
     * 
     * @return Date when this schedule last fired. Null if there was none.
     */
    public Date getLastTime() {
        return this.lastTime;
    }

    /**
     * Set 'at' value for this schedule. Can be a simple date or a full RRule.
     * 
     * @return String containg date or RRule.
     */
    public String getAt() {
        return this.at;
    }

    /**
     * Set 'at' value for this schedule. Can be a simple date or a full RRule.
     * It will fire the calculation of IncomingTime and make LastTime null.
     * 
     * @param value
     *            RRule or date saying when this schedule is appointed for.
     */
    public void setAt(String value) {
        this.at = value;
        this.lastTime = null;

        if (this.at == null || this.at.length() == 0) {
            this.setActive(false);
            return;
        }

        if (value.startsWith("RRULE:")) {
            this.nextIncomingTime();
        } else {
            this.incomingTime = DateTimeFormat
                    .forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ")
                    .withLocale(Locale.ROOT)
                    .withChronology(ISOChronology.getInstanceUTC())
                    .parseDateTime(value).toDate();
        }
    }

    /**
     * Get the map of variables associated with this schedule.
     * 
     * @return The map with all variable names pointing to a list of possible
     *         values
     */
    @XmlTransient
    @JsonIgnore
    public Map<String, List<String>> getVariables() {
        return this.variables;
    }

    /**
     * Return the set of children objects for this element. TODO: should be
     * removed?
     * 
     * @return set of children objects
     */
    @XmlTransient
    @JsonIgnore
    public Set<Request> getRequests() {
        return this.requests;
    }

    /**
     * Set a new set of children objects for this element. TODO: should be
     * removed?
     * 
     * @param set
     *            set of children objects
     */
    public void setRequests(Set<Request> set) {
        this.requests = set;
    }

    /**
     * Get delay until the firing of this schedule.
     */
    @XmlTransient
    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(incomingTime.getTime() - System.currentTimeMillis(),
                MILLISECONDS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Delayed o) {
        if (o == this) {
            return 0;
        }
        long diff = this.getDelay(MILLISECONDS) - o.getDelay(MILLISECONDS);
        return (diff < 0) ? -1 : (diff > 0) ? 1 : 0;
    }

    /**
     * Run this schedule, processing each request.
     */
    @Override
    public void run() {
        LOGGER.debug("Firing " + this);

        // In JPA, this will be Lazy Load, and the data will load accordingly
        // In Memory Storage, requests is ensured to be always referenced
        // correctly
        LOGGER.debug("Running Requests: " + this.requests);
        for (Request req : this.requests) {
            req.process();
        }

        // calculate the next schedule time based on recurrence rule
        nextIncomingTime();
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

    /**
     * Calculate the next incoming time for this schedule. TODO: should be
     * private
     */
    public void nextIncomingTime() {
        LOGGER.trace("nextIncomingTime {");

        if (!this.getActive()) {
            LOGGER.warn("Trying to get nextIncoming time of "
                    + "*deactivated element*.");
            LOGGER.trace("nextIncomingTime }");
            return;
        }

        if (this.at == null || this.at.length() == 0) {
            this.setActive(false);
            LOGGER.warn("Trying to get nextIncoming time of "
                    + "*element without at property*. "
                    + "Element deactivated.");
            LOGGER.trace("nextIncomingTime }");
            return;
        }

        // if the foreseen time is still to come, then it is still valid and use
        // it instead
        if (this.incomingTime != null && this.incomingTime.getTime() > DateTime
                .now().withMillisOfSecond(0).getMillis()) {
            LOGGER.debug("incomingTime " + incomingTime + " still valid. "
                    + "Keep using it.");
            LOGGER.trace("nextIncomingTime }");
            return;
        }

        // if it is NOT a RRule, we must deactivate
        if (!this.at.startsWith("RRULE:")) {
            this.setActive(false);
            LOGGER.warn("Trying to get nextIncoming time of "
                    + "*that is not a recurrence rule*. "
                    + "Element deactivated.");
            LOGGER.trace("nextIncomingTime }");
            return;
        }

        // if last time is null, then base is 'now'
        DateTime base = new DateTime(this.getLastTime()).withMillisOfSecond(0);

        try {
            // generate a iteratable range of date times
            DateTimeIterable range = DateTimeIteratorFactory
                    .createDateTimeIterable(this.at, base, DateTimeZone.UTC,
                            true);

            // get an iterator to range of date times
            DateTimeIterator it = range.iterator();

            // iterate until a date that is greater than now and not null
            base = null;
            while (it.hasNext()) {
                base = it.next();
                LOGGER.debug("next it " + base);
                DateTime now = DateTime.now().plusSeconds(1)
                        .withMillisOfSecond(0);
                if (base != null && base.isAfter(now)) {
                    LOGGER.debug("base found at " + base + " as after " + now);
                    break;
                } else {
                    base = null;
                }
            }

            // if base is null, then we are out of dates
            if (base != null) {
                // turns this time in the last occurrence
                if (this.incomingTime != null) {
                    this.lastTime = this.incomingTime;
                    LOGGER.debug(
                            "Making " + this.incomingTime + " the last time");
                }

                // the schedule is the next time
                this.incomingTime = base.toDate();
                LOGGER.debug("New incomingTime is " + this.incomingTime);
            } else {
                // If there is NOT another iteration for this watcher, then
                // disable it!
                this.setActive(false);
                LOGGER.debug("No more elements in the RRule. Deactivating.");
            }
        } catch (ParseException e) {
            LOGGER.error("There was an error parsing '" + this.at + "'. "
                    + "Deactivating.");
            this.setActive(false);
        }

        LOGGER.trace("nextIncomingTime }");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Schedule [incomingTime=" + incomingTime + ", lastTime="
                + lastTime + ", active=" + active + ", at=" + at
                + ", variables=" + variables + ", getId()=" + getId()
                + ", getCreatedOn()=" + getCreatedOn() + "]";
    }
}
