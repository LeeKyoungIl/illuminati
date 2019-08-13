package me.phoboslabs.illuminati.elasticsearch.infra.model;

public class EsInfo {

    private String user;
    private String pass;
    private String host;
    private int port;

    public String getUser() {
        return this.user;
    }

    public String getPass() {
        return this.pass;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return port;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
