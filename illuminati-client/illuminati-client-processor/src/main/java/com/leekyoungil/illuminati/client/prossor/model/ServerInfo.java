package com.leekyoungil.illuminati.client.prossor.model;

import com.google.gson.annotations.Expose;
import com.leekyoungil.illuminati.client.prossor.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 10/07/2017.
 *
 * get server info is very expensive process. Because of this, it has to be executed only once.
 */
public class ServerInfo {

    private static final Logger SERVER_INFO_LOGGER = LoggerFactory.getLogger(ServerInfo.class);

    @Expose private String domain;
    @Expose private int serverPort = 0;

    @Expose private String hostName;
    @Expose private String serverIp;

    public ServerInfo () {}

    public ServerInfo (final boolean init) {
        if (init == true) {
            this.init();
        }
    }

    private void init () {
        try {
            final InetAddress ip = InetAddress.getLocalHost();

            this.serverIp = ip.getHostAddress();
            this.hostName = ip.getHostName();
        } catch (UnknownHostException ex) {
            SERVER_INFO_LOGGER.error("Sorry. check your server network. ("+ex.toString()+")");
        }
    }

    public String getServerIp() {
        return this.serverIp;
    }

    public boolean isAreadySetServerDomainAndPort () {
        if (!StringUtils.isValid(this.domain) || this.serverPort == 0) {
            return false;
        }

        return true;
    }

    public void setServerInfoFromRequest(HttpServletRequest request) {
        this.domain = request.getServerName();
        this.serverPort = request.getLocalPort();

    }
}
