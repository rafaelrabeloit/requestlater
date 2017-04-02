package com.neptune.api.requestlater.domain;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinkNoFollow;
import org.glassfish.jersey.linking.InjectLinks;

import com.neptune.api.template.adapter.LinkAdapter;
import com.neptune.api.template.domain.DomainTemplate;

/**
 * Request Model.
 * 
 * @author Rafael R. Itajuba
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = "_requests")
@XmlRootElement
public class Request extends DomainTemplate implements Comparable<Request> {

    private static final long serialVersionUID = -7118587426722305792L;

    static final Logger LOGGER = LogManager.getLogger(Request.class);

    // target that will receive this request
    @Column(name = "target_uri")
    private String targetUri;

    // method that should be used
    @Column
    private HttpMethods method;

    // content that must be used
    @Column(length = 1024 * 128, nullable = true)
    private String content;

    // priority for this request is a priority queue
    @Column
    private Integer priority;

    // headers that must be included in the request
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "value")
    @CollectionTable(name = "_request_headers", joinColumns = @JoinColumn(name = "request_id"))
    private Map<String, String> headers;

    // rules that must be used to extract data from its responses
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "rule")
    @CollectionTable(name = "_request_extractors", joinColumns = @JoinColumn(name = "request_id"))
    private Map<String, String> extractors;

    // responses (children)
    @InjectLinkNoFollow
    @OneToMany(mappedBy = "request", targetEntity = Response.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Response> responses;

    // parent
    @InjectLinkNoFollow
    @ManyToOne(optional = false)
    @JoinColumn(name = "schedule_id", referencedColumnName = "id")
    private Schedule schedule;

    // parent id, for link injection
    @Column(name = "schedule_id", columnDefinition = "binary(16)", insertable = false, updatable = false)
    private UUID scheduleId;

    @InjectLinks({
            @InjectLink(value = "schedules/${instance.scheduleId}/requests/${instance.id}", rel = "self"),
            @InjectLink(value = "schedules/${instance.scheduleId}/requests", rel = "sibilings"),
            @InjectLink(value = "schedules/${instance.scheduleId}", rel = "schedule"),
            @InjectLink(value = "requests/${instance.id}/responses", rel = "responses") })
    @XmlJavaTypeAdapter(LinkAdapter.class)
    @Transient
    private List<Link> links;

    /**
     * Default construct. Used for Injection and for default values (including
     * random UUID)
     */
    public Request() {
        super();

        this.headers = new HashMap<String, String>();
        this.responses = new LinkedList<Response>();

        this.extractors = new HashMap<String, String>();

        this.method = HttpMethods.GET;
    }

    /**
     * Constructor with id. TODO: Should be on api-template?
     * 
     * @param id
     */
    public Request(UUID id) {
        this();

        this.setId(id);
    }

    /**
     * Get links injected for HATEAOS
     * 
     * @return list of links, to be included in json
     */
    @XmlElement(name = "_links")
    public List<Link> getLinks() {
        return links;
    }

    public String getTargetUri() {
        return this.targetUri;
    }

    public void setTargetUri(String value) {
        this.targetUri = value;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer value) {
        this.priority = value;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String value) {
        this.content = value;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public void setHeaders(Map<String, String> map) {
        this.headers = map;
    }

    public Map<String, String> getExtractors() {
        return this.extractors;
    }

    public void setExtractors(Map<String, String> map) {
        this.extractors = map;
    }

    public HttpMethods getMethod() {
        return this.method;
    }

    public void setMethod(HttpMethods value) {
        this.method = value;
    }

    /**
     * Return the set of children objects for this element. TODO: should be
     * removed?
     * 
     * @return list of children objects
     */
    @XmlTransient
    public List<Response> getResponses() {
        return this.responses;
    }

    /**
     * Set a new list of children objects for this element. TODO: should be
     * removed?
     * 
     * @param list
     *            list of children objects
     */
    public void setResponses(List<Response> list) {
        this.responses = list;
    }

    /**
     * Returns the parent object of this element. TODO: should be removed?
     * 
     * @return Parent Object
     */
    @XmlTransient
    public Schedule getSchedule() {
        return this.schedule;
    }

    /**
     * Set the parent object of this element. TODO: should be removed?
     * 
     * @param value
     *            The parent object
     */
    public void setSchedule(Schedule value) {
        this.schedule = value;
    }

    /**
     * Returns the parent id of this element. TODO: should be removed?
     * 
     * @return Parent Id
     */
    public UUID getScheduleId() {
        return this.scheduleId;
    }

    /**
     * Set the parent id of this element. TODO: should be removed?
     * 
     * @param value
     *            The parent id
     */
    public void setScheduleId(UUID value) {
        this.scheduleId = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Request o) {
        return this.getPriority().compareTo(o.getPriority());
    }

    /**
     * Process this request.
     */
    public void process() {
        LOGGER.debug("Request Processing: " + this);

        HttpUriRequest request = this.extract();

        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(request);

            Response resp = new Response();
            resp.setRequest(this);
            resp.feed(response);

            LOGGER.debug("HTTPRequest completed, with response: " + resp);

            // JPA ensures that this will persist() when update
            // Memory Storage will handle this in DAO
            this.responses.add(resp);

            // update global context with variables
            this.schedule.addVariables(resp, this);

        } catch (ClientProtocolException e) {
            // TODO: Treat and unit test this exception
            LOGGER.error("Error Executing HTTPRequest.", e);
        } catch (IOException e) {
            // TODO: Treat and unit test this exception
            LOGGER.error("Error Executing HTTPRequest.", e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    LOGGER.error("Error Closing response.", e);
                }
            }
        }
    }

    /**
     * Builds the HTTPRequest based on properties of this instance.
     * 
     * @return HttpUriRequest instance
     */
    HttpUriRequest extract() {
        HttpUriRequest ret;

        switch (method) {
        case POST:
            ret = new HttpPost(this.targetUri);
            break;
        case PUT:
            ret = new HttpPut(this.targetUri);
            break;
        case DELETE:
            ret = new HttpDelete(this.targetUri);
            break;
        case HEAD:
            ret = new HttpHead(this.targetUri);
            break;
        case OPTIONS:
            ret = new HttpOptions(this.targetUri);
            break;
        case TRACE:
            ret = new HttpTrace(this.targetUri);
            break;
        default:
            ret = new HttpGet(this.targetUri);
            break;
        }

        this.headers.forEach((key, val) -> {
            if (this.schedule != null
                    && this.schedule.getVariables().containsKey(val)) {
                ret.addHeader(key,
                        this.schedule.getVariables().get(val).get(0));
            } else {
                ret.addHeader(key, val);
            }
        });

        // TODO: http patch support
        if (this.method == HttpMethods.POST || this.method == HttpMethods.PUT) {
            HttpEntityEnclosingRequestBase requestWithEntity = (HttpEntityEnclosingRequestBase) ret;
            HttpEntity entity;

            try {
                entity = new StringEntity(this.formattedContent());
                requestWithEntity.setEntity(entity);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        return ret;
    }

    /**
     * Replace any occurrences of variables and return the resulting string.
     * 
     * @return The string with 'content' using variables values
     */
    private String formattedContent() {
        String formattedContent = content;

        for (String key : this.schedule.getVariables().keySet()) {
            formattedContent = formattedContent.replace(key,
                    this.schedule.getVariables().get(key).get(0));
        }

        return formattedContent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        String tmpContent = (content == null ? content
                : content.replaceAll(" ", "").replaceAll("\\t", ""));
        tmpContent = (tmpContent != null && tmpContent.length() > 255
                ? tmpContent.substring(0, 255) + "(...)" : tmpContent);

        return "Request [content=" + tmpContent + ", createdOn="
                + this.getCreatedOn() + ", headers=" + headers + ", id="
                + this.getId() + ", method=" + method + ", scheduleId="
                + scheduleId + ", targetUri=" + targetUri + "]";
    }
}
