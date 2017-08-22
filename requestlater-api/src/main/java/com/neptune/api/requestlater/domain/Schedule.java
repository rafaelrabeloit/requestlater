package com.neptune.api.requestlater.domain;

import java.sql.Date;
import java.text.ParseException;
import java.time.Clock;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
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
import com.google.ical.compat.javautil.DateIterable;
import com.google.ical.compat.javautil.DateIterator;
import com.google.ical.compat.javautil.DateIteratorFactory;
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

    /**
     * DateTime format accepted for this schedule mechanism.
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    /**
     * Logger
     */
    static final Logger LOGGER = LogManager.getLogger(Schedule.class);

    // Formatter used to convert strings to Instant (other options are zoneless)
    private static final DateTimeFormatter FMT = new DateTimeFormatterBuilder()
            .appendPattern(DATE_FORMAT).toFormatter();

    // serial version uid
    private static final long serialVersionUID = -3131395094924167679L;

    // time for this schedule to fire
    @Column(name = "incoming_time", nullable = true)
    private Instant incomingTime;

    // the last time it was fired
    @Column(name = "last_time", nullable = true)
    private Instant lastTime;

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

    // system (or mocked) clock instance
    @Transient
    private Clock clock;

    /**
     * Default construct. Used for Injection and for default values (including
     * random UUID)
     */
    public Schedule() {
        super();

        this.active = Boolean.TRUE;

        this.requests = new HashSet<Request>();
        this.variables = new HashMap<>();
        this.clock = Clock.systemUTC();
    }

    /**
     * Constructor with clock. Used mostly for testing.
     * 
     * @param clock
     *            used to control this schedule
     */
    public Schedule(Clock clock) {
        this();

        this.clock = clock;
    }

    /**
     * Constructor with id. TODO: Should be on api-template?
     * 
     * @param id
     *            for this element
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
     * @return Instant when this schedule should fire. Null if it is inactive.
     */
    public Instant getIncomingTime() {
        return this.incomingTime;
    }

    /**
     * Get the last time that this schedule was fired. Read-only by the API.
     * 
     * @return Instant when this schedule last fired. Null if there was none.
     */
    public Instant getLastTime() {
        return this.lastTime;
    }

    /**
     * Set 'at' value for this schedule. Can be a simple date or a full RRule.
     * 
     * @return String containing date or RRule.
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
            this.incomingTime = FMT.parse(value, Instant::from);
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
    public long getDelay(TimeUnit unit) { // TODO: consider TimeUnit
        LOGGER.trace("getDelay (" + unit + ") {");
        long delay = Instant.now(this.clock).until(this.incomingTime,
                ChronoUnit.MILLIS);
        LOGGER.trace("getDelay } -> " + delay);
        return delay;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Delayed o) {
        if (o == this) {
            return 0;
        }
        long diff = this.getDelay(null) - o.getDelay(null); // TODO: consider
                                                            // TimeUnit
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

        // if last time is null, then base is 'now'
        Instant base = this.clock.instant().truncatedTo(ChronoUnit.SECONDS);

        // if the foreseen time is still to come, then it is still valid and use
        // it instead
        if (this.incomingTime != null && this.incomingTime.isAfter(base)) {
            LOGGER.debug("incomingTime " + this.incomingTime + " still valid "
                    + "against " + base + ". Then we are gonna keep using it.");
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

        // if last time is not null, then it is the base
        if (this.getLastTime() != null) {
            base = this.getLastTime();
        }

        try {
            // generate a iteratable range of date times
            DateIterable range = DateIteratorFactory.createDateIterable(this.at,
                    Date.from(base), TimeZone.getTimeZone("UTC"), true);

            // get an iterator to range of date times
            DateIterator it = range.iterator();

            // iterate until a date that is greater than now and not null
            base = null;
            while (it.hasNext()) {
                base = it.next().toInstant();
                LOGGER.debug("next it " + base);
                Instant now = Instant.now(this.clock).plusSeconds(1)
                        .truncatedTo(ChronoUnit.SECONDS);
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
                this.incomingTime = base;
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
