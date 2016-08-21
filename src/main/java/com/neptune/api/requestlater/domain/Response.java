package com.neptune.api.requestlater.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.hibernate.annotations.GenericGenerator;

import com.neptune.api.template.domain.DomainTemplate;

/**
 * Response Model
 * 
 * @author Rafael R. Itajuba
 */
@Entity
@Table(name = "_response")
@XmlRootElement
public class Response extends DomainTemplate implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private Date createdOn;

    private Map<String, String> headers;

    private Request request;

    public Response() {
        super();

        this.id = UUID.randomUUID();
        this.headers = new HashMap<String, String>();
    }

    public Response(UUID id) {
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

    @ElementCollection
    @MapKeyColumn(name = "response_id")
    @Column(name = "value")
    @CollectionTable(name = "_response_headers", joinColumns = @JoinColumn(name = "id"))
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "request_id", referencedColumnName = "id")
    @XmlTransient
    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public void peel(HttpResponse response) {
        HttpEntity entity = response.getEntity();
    }

}
