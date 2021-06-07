package it.nextworks.nfvmano.catalogue.template.elements;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
public class RestNstConfigurationRule extends NstConfigurationRule{

    private HttpMethod httpMethod;
    private String url;
    private String config;
    private HttpStatus expectedStatusCode;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Map<String, String> headers = new HashMap<>();

    public RestNstConfigurationRule() {
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public HttpStatus getExpectedStatusCode() {
        return expectedStatusCode;
    }

    public void setExpectedStatusCode(HttpStatus expectedStatusCode) {
        this.expectedStatusCode = expectedStatusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public void isValid() throws MalformattedElementException {
        super.isValid();
        if(httpMethod == null) throw new MalformattedElementException("NST configuration rule without httpMethod");
        if(url == null) throw new MalformattedElementException("NST configuration rule without url");
        if(config == null) throw new MalformattedElementException("NST configuration rule without config script");
        if(expectedStatusCode == null) throw new MalformattedElementException("NST configuration rule without expected status code");
    }

    @Override
    public String toString() {
        return "RestNstConfigurationRule{" +
                "httpMethod=" + httpMethod +
                ", url='" + url + '\'' +
                ", config='" + config + '\'' +
                ", expectedStatusCode=" + expectedStatusCode +
                ", headers=" + headers +
                ", id=" + id +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", nsdId='" + nsdId + '\'' +
                ", params=" + params + '\'' +
                '}';
    }
}
