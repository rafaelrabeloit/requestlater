package com.neptune.api.requestlater.domain;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
@Table(name = "_response")
@XmlRootElement
public class Response extends DomainTemplate {

    /**
     * 
     */
    private static final long serialVersionUID = -1405742943228752633L;

    final static Logger logger = LogManager.getLogger(Response.class);

    private Map<String, String> headers;

    private String locale;
    private String protocolVersion;
    private String reasonPhrase;
    private Integer statusCode;
    private String content;

    @InjectLinkNoFollow
    private Request request;
    private UUID requestId;

    @InjectLinks({
            @InjectLink(value = "requests/${instance.requestId}/responses/${instance.id}", rel = "self"),
            @InjectLink(value = "requests/${instance.requestId}/responses", rel = "responses") })
    @XmlJavaTypeAdapter(LinkAdapter.class)
    private List<Link> links;

    public Response() {
        super();

        this.headers = new HashMap<String, String>();
    }

    public Response(UUID id) {
        this();

        this.setId(id);
    }

    @Transient
    @XmlElement(name = "_links")
    public List<Link> getLinks() {
        return links;
    }

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "value")
    @CollectionTable(name = "_response_headers", joinColumns = @JoinColumn(name = "response_id"))
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    @Column
    public String getLocale() {
        return this.locale;
    }

    @Column(name = "protocol_version")
    public String getProtocolVersion() {
        return this.protocolVersion;
    }

    @Column(name = "reason_phrase")
    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "request_id", referencedColumnName = "id")
    @XmlTransient
    public Request getRequest() {
        return request;
    }

    @Column(name = "request_id", columnDefinition = "binary(16)", insertable = false, updatable = false)
    public UUID getRequestId() {
        return this.requestId;
    }

    @Column(name = "status_code")
    public Integer getStatusCode() {
        return this.statusCode;
    }

    @Column(length = 1024 * 128)
    public String getContent() {
        return this.content;
    }

    /**
     * Populate this instance of response with parameters of a HTTPResponse.
     * 
     * @param response
     *            HTTPResponse instance to copy from
     */
    public void peel(HttpResponse response) {
        HttpEntity entity = response.getEntity();

        for (Header header : response.getAllHeaders()) {
            this.headers.put(header.getName(), header.getValue());
        }

        this.setLocale(response.getLocale().toString());
        this.setProtocolVersion(
                response.getStatusLine().getProtocolVersion().toString());
        this.setReasonPhrase(response.getStatusLine().getReasonPhrase());
        this.setStatusCode(response.getStatusLine().getStatusCode());

        try {
            this.setContent(EntityUtils.toString(entity));
        } catch (ParseException e) {
            logger.error("Error Parsing entity body from HTTPResponse.", e);
            // TODO: Treat and unit test this exception
        } catch (IOException e) {
            logger.error("IO Error parsing entity body from HTTPResponse.", e);
            // TODO: Treat and unit test this exception
        }
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public void setReasonPhrase(String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

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
