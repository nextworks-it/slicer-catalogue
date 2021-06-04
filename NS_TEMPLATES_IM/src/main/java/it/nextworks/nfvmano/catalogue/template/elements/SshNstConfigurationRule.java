package it.nextworks.nfvmano.catalogue.template.elements;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;

import javax.persistence.Entity;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
public class SshNstConfigurationRule extends NstConfigurationRule{
    private String ipAddress;
    private int port = 22;
    private String username;
    private String password;
    private String command;

    public SshNstConfigurationRule() {
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String user) {
        this.username = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void isValid() throws MalformattedElementException {
        super.isValid();
        if(ipAddress == null) throw new MalformattedElementException("NST configuration rule without ip address");
        if(username == null) throw new MalformattedElementException("NST configuration rule without user");
        if(password == null) throw new MalformattedElementException("NST configuration rule without password");
        if(command == null) throw new MalformattedElementException("NST configuration rule without command");
    }

    @Override
    public String toString() {
        return "SshNstConfigurationRule{" +
                "ipAddress='" + ipAddress + '\'' +
                ", port='" + port + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", command='" + command + '\'' +
                ", id=" + id +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", nsdId='" + nsdId + '\'' +
                ", params=" + params + '\'' +
                '}';
    }
}
