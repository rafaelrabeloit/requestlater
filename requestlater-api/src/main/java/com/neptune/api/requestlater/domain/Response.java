package com.neptune.api.requestlater.domain;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinkNoFollow;
import org.glassfish.jersey.linking.InjectLinks;

import com.neptune.api.template.adapter.LinkAdapter;
import com.neptune.api.template.domain.DomainTemplate;

/**
 * Response Model.
 * 
 * @author Rafael R. Itajuba
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = "_response")
@XmlRootElement
public class Response extends DomainTemplate {

    private static final long serialVersionUID = -1405742943228752633L;

    static final Logger LOGGER = LogManager.getLogger(Response.class);

    // locale field in response
    @Column
    private String locale;

    // protocol version for a response
    @Column(name = "protocol_version")
    private String protocolVersion;

    // reason phrase
    @Column(name = "reason_phrase")
    private String reasonPhrase;

    // status code
    @Column(name = "status_code")
    private Integer statusCode;

    // response content
    @Column(length = 1024 * 128)
    private String content;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "value")
    @CollectionTable(name = "_response_headers", joinColumns = @JoinColumn(name = "response_id"))
    private Map<String, String> headers;

    @InjectLinkNoFollow
    @ManyToOne(optional = false)
    @JoinColumn(name = "request_id", referencedColumnName = "id")
    private Request request;

    @Column(name = "request_id", columnDefinition = "binary(16)", insertable = false, updatable = false)
    private UUID requestId;

    @InjectLinks({
            @InjectLink(value = "requests/${instance.requestId}/responses/${instance.id}", rel = "self"),
            @InjectLink(value = "requests/${instance.requestId}/responses", rel = "responses") })
    @XmlJavaTypeAdapter(LinkAdapter.class)
    @Transient
    private List<Link> links;

    /**
     * Default construct. Used for Injection and for default values (including
     * random UUID)
     */
    public Response() {
        super();

        this.headers = new HashMap<String, String>();
    }

    /**
     * Constructor with id. TODO: Should be on api-template?
     * 
     * @param id
     */
    public Response(UUID id) {
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

    public String getLocale() {
        return this.locale;
    }

    public void setLocale(String value) {
        this.locale = value;
    }

    public String getProtocolVersion() {
        return this.protocolVersion;
    }

    public void setProtocolVersion(String value) {
        this.protocolVersion = value;
    }

    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

    public void setReasonPhrase(String value) {
        this.reasonPhrase = value;
    }

    public Integer getStatusCode() {
        return this.statusCode;
    }

    public void setStatusCode(Integer value) {
        this.statusCode = value;
    }

    public String getContent() {
        return this.content;
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

    /**
     * Returns the parent object of this element. TODO: should be removed?
     * 
     * @return Parent Object
     */
    @XmlTransient
    public Request getRequest() {
        return request;
    }

    /**
     * Set the parent object of this element. TODO: should be removed?
     * 
     * @param value
     *            The parent object
     */
    public void setRequest(Request request) {
        this.request = request;
    }

    /**
     * Returns the parent id of this element. TODO: should be removed?
     * 
     * @return Parent Id
     */
    public UUID getRequestId() {
        return this.requestId;
    }

    /**
     * Set the parent id of this element. TODO: should be removed?
     * 
     * @param value
     *            The parent id
     */
    public void setRequestId(UUID value) {
        this.requestId = value;
    }

    /**
     * Populate this instance of response with parameters of a HTTPResponse.
     * 
     * @param response
     *            HTTPResponse instance to copy from
     */
    public void feed(HttpResponse response) {
        HttpEntity entity = response.getEntity();

        for (Header header : response.getAllHeaders()) {
            this.headers.put(header.getName(), header.getValue());
        }

        this.setLocale(response.getLocale().toString());
        this.setReasonPhrase(response.getStatusLine().getReasonPhrase());
        this.setStatusCode(response.getStatusLine().getStatusCode());
        this.setProtocolVersion(
                response.getStatusLine().getProtocolVersion().toString());

        try {
            this.setContent(EntityUtils.toString(entity));
        } catch (ParseException e) {
            LOGGER.error("Error Parsing entity body from HTTPResponse.", e);
            // TODO: Treat and unit test this exception
        } catch (IOException e) {
            LOGGER.error("*IO* Error parsing entity body from HTTPResponse.",
                    e);
            // TODO: Treat and unit test this exception
        }
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

        return "Response [createdOn=" + this.getCreatedOn() + ", headers="
                + headers + ", id=" + this.getId() + ", locale=" + locale
                + ", protocolVersion=" + protocolVersion + ", reasonPhrase="
                + reasonPhrase + ", requestId=" + requestId + ", statusCode="
                + statusCode + ", content='" + tmpContent + "')]";
    }

}
