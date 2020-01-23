package it.nextworks.nfvmano.catalogue.template.elements;

public class DomainLayerInterface {
    private String url;
    private int port;
    private boolean auth;
    private InterfaceType interfaceType;

    public DomainLayerInterface(){}

    public DomainLayerInterface(String url, int port, boolean auth, InterfaceType interfaceType){
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
}
