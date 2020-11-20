package it.nextworks.nfvmano.catalogue.domainLayer;

import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;

import javax.persistence.Embeddable;

@Embeddable
public class DomainInterface {
    private String url;
    private int port;
    private boolean auth;
    private InterfaceType interfaceType;

    public DomainInterface(){}

    public DomainInterface(String url, int port, boolean auth, InterfaceType interfaceType){
        this.url=url;
        this.port=port;
        this.auth=auth;
        this.interfaceType=interfaceType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public InterfaceType getInterfaceType() {
        return interfaceType;
    }

    public void setInterfaceType(InterfaceType interfaceType) {
        this.interfaceType = interfaceType;
    }

    public void isValid() throws MalformattedElementException {
        if (this.url == null) {
            throw new MalformattedElementException("Domain interface url not set");
        } else if (this.port == 0) {
            throw new MalformattedElementException("Domain interface port not set");
        } else if (this.interfaceType == null) {
            throw new MalformattedElementException("Domain interface type not set");
        }
    }

}
