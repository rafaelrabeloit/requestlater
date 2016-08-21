package com.neptune.api.requestlater.domain;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

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
import org.hibernate.annotations.GenericGenerator;

import com.neptune.api.template.domain.DomainTemplate;

/**
 * Request Model
 * 
 * @author Rafael R. Itajuba
 */
@Entity
@Table(name = "_requests")
@XmlRootElement
public class Request extends DomainTemplate implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private Date createdOn;

    private String targetUri;
    private HttpMethod method;
    private Map<String, String> headers;
    private String body;

    private Schedule schedule;

    private List<Response> responses;

    public Request() {
        super();

        this.id = UUID.randomUUID();
        this.headers = new HashMap<String, String>();
        this.responses = new LinkedList<Response>();
    }

    public Request(UUID id) {
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
    @MapKeyColumn(name = "request_id")
    @Column(name = "value")
    @CollectionTable(name = "_request_headers", joinColumns = @JoinColumn(name = "id"))
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "schedule_id", referencedColumnName = "id")
    @XmlTransient
    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    @OneToMany(mappedBy = "request", targetEntity = Response.class, fetch = FetchType.EAGER)
    public List<Response> getResponses() {
        return this.responses;
    }

    public void setResponses(List<Response> responses) {
        this.responses = responses;
    }

    /**
     * Process this request
     */
    public void process() {
        HttpUriRequest request = this.build();

        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(request);

            Response resp = new Response();
            resp.peel(response);

            this.getResponses().add(resp);

        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        } finally {
            try {
                response.close();
            } catch (IOException e) {
            }
        }
    }

    private HttpUriRequest build() {
        HttpUriRequest ret;

        switch (method) {
        case HttpPost:
            ret = new HttpPost(this.targetUri);
            break;
        case HttpDelete:
            ret = new HttpDelete(this.targetUri);
            break;
        case HttpPut:
            ret = new HttpPut(this.targetUri);
            break;
        case HttpHead:
            ret = new HttpHead(this.targetUri);
            break;
        case HttpOptions:
            ret = new HttpOptions(this.targetUri);
            break;
        case HttpTrace:
            ret = new HttpTrace(this.targetUri);
            break;
        default:
            ret = new HttpGet(this.targetUri);
            break;
        }

        return ret;
    }
}
