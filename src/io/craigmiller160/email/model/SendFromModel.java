package io.craigmiller160.email.model;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by craig on 3/6/17.
 */
public class SendFromModel extends AbstractModel{

    public static final String HOST_PROP = "host";
    public static final String USERNAME_PROP = "Username";
    public static final String PASSWORD_PROP = "Password";
    public static final String PORT_PROP = "Port";
    public static final String AUTH_PROP = "Auth";
    public static final String START_TLS_PROP = "StartTLS";

    private String host;
    private String username;
    private String password;
    private int port;
    private boolean auth;
    private boolean startTLS;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        String old = this.host;
        this.host = host;
        firePropertyChange(HOST_PROP, old, host);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        String old = this.username;
        this.username = username;
        firePropertyChange(USERNAME_PROP, old, username);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        String old = this.password;
        this.password = password;
        firePropertyChange(PASSWORD_PROP, old, password);
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        int old = this.port;
        this.port = port;
        firePropertyChange(PORT_PROP, old, port);
    }

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        boolean old = this.auth;
        this.auth = auth;
        firePropertyChange(AUTH_PROP, old, auth);
    }

    public boolean isStartTLS() {
        return startTLS;
    }

    public void setStartTLS(boolean startTLS) {
        boolean old = this.startTLS;
        this.startTLS = startTLS;
        firePropertyChange(START_TLS_PROP, old, startTLS);
    }

    @Override
    public void validate() throws Exception {
        if(StringUtils.isEmpty(host)){
            throw new Exception("Email is missing SMTP Host");
        }
        else if(port == 0){
            throw new Exception("Email is missing SMTP Port");
        }
        else if(auth && StringUtils.isEmpty(username)){
            throw new Exception("Email needs sending account username");
        }
        else if(auth && StringUtils.isEmpty(password)){
            throw new Exception("Email needs sending account password");
        }
    }
}
