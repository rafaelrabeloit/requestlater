package com.neptune.api.requestlater.domain;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
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
@Table(name = "_requests")
@XmlRootElement
public class Request extends DomainTemplate {

    /**
     * 
     */
    private static final long serialVersionUID = -7118587426722305792L;

    final static Logger logger = LogManager.getLogger(Request.class);

    private Map<String, String> headers;
    private HttpMethods method;
    private String targetUri;
    private String content;

    @InjectLinkNoFollow
    private List<Response> responses;

    @InjectLinkNoFollow
    private Schedule schedule;
    private UUID scheduleId;

    @InjectLinks({
            @InjectLink(value = "schedules/${instance.scheduleId}/requests/${instance.id}", rel = "self"),
            @InjectLink(value = "schedules/${instance.scheduleId}/requests", rel = "sibilings"),
            @InjectLink(value = "schedules/${instance.scheduleId}", rel = "schedule"),
            @InjectLink(value = "requests/${instance.id}/responses", rel = "responses") })
    @XmlJavaTypeAdapter(LinkAdapter.class)
    private List<Link> links;

    public Request() {
        super();

        this.headers = new HashMap<String, String>();
        this.responses = new LinkedList<Response>();
    }

    public Request(UUID id) {
        this();

        this.setId(id);
    }

    @Transient
    @XmlElement(name = "_links")
    public List<Link> getLinks() {
        return links;
    }

    @Column(length = 1024 * 128)
    public String getContent() {
        return content;
    }

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "value")
    @CollectionTable(name = "_request_headers", joinColumns = @JoinColumn(name = "request_id"))
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    @Column
    public HttpMethods getMethod() {
        return this.method;
    }

    @OneToMany(mappedBy = "request", targetEntity = Response.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @XmlTransient
    public List<Response> getResponses() {
        return this.responses;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "schedule_id", referencedColumnName = "id")
    @XmlTransient
    public Schedule getSchedule() {
        return this.schedule;
    }

    @Column(name = "schedule_id", columnDefinition = "binary(16)", insertable = false, updatable = false)
    public UUID getScheduleId() {
        return this.scheduleId;
    }

    @Column(name = "target_uri")
    public String getTargetUri() {
        return this.targetUri;
    }

    /**
     * Process this request.
     */
    public void process() {
        logger.debug("Request Processing: " + this);

        HttpUriRequest request = this.build();

        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(request);

            Response resp = new Response();
            resp.peel(response);
            resp.setRequest(this);

            logger.debug("HTTPRequest completed, with response: " + resp);

            // JPA ensures that this will persist() when update
            // Memory Storage will handle this in DAO
            this.getResponses().add(resp);

        } catch (ClientProtocolException e) {
            // TODO: Treat and unit test this exception
            logger.error("Error Executing HTTPRequest.", e);
        } catch (IOException e) {
            // TODO: Treat and unit test this exception
            logger.error("Error Executing HTTPRequest.", e);
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                logger.error("Error Closing response.", e);
            }
        }
    }

    /**
     * Builds the HTTPRequest based on properties of this instance.
     * 
     * @return HttpUriRequest instance
     */
    HttpUriRequest build() {
        HttpUriRequest ret;

        switch (method) {
        case POST:
            ret = new HttpPost(this.targetUri);
            break;
        case DELETE:
            ret = new HttpDelete(this.targetUri);
            break;
        case PUT:
            ret = new HttpPut(this.targetUri);
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

        headers.forEach((key, val) -> {
            ret.addHeader(key, val);
        });

        return ret;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setMethod(HttpMethods method) {
        this.method = method;
    }

    public void setResponses(List<Response> responses) {
        this.responses = responses;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public void setTargetUri(String targetUri) {
        this.targetUri = targetUri;
    }

    public void setScheduleId(UUID scheduleId) {
        this.scheduleId = scheduleId;
    }

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
