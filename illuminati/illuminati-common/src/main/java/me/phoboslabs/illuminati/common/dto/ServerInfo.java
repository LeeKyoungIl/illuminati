package me.phoboslabs.illuminati.common.dto;

import com.google.gson.annotations.Expose;
import me.phoboslabs.illuminati.common.dto.enums.MappingType;
import me.phoboslabs.illuminati.common.util.StringObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * Created by leekyoungil (leekyoungil@gmail.com) on 10/07/2017.
 *
 * get spring info is very expensive process. Because of this, it has to be executed only once.
 */
public class ServerInfo {

    private static final Logger SERVER_INFO_LOGGER = LoggerFactory.getLogger(ServerInfo.class);

    @Expose private String domain;
    @Expose private int serverPort = 0;

    @Expose @GroupMapping(mappingType = MappingType.KEYWORD) private String hostName;
    @Expose @GroupMapping(mappingType = MappingType.KEYWORD) private String serverIp;

    public ServerInfo () {}

    public ServerInfo (final boolean init) {
        if (init) {
            this.init();
        }
    }

    private void init () {
        try {
            final InetAddress ip = InetAddress.getLocalHost();

            this.serverIp = ip.getHostAddress();
            this.hostName = ip.getHostName();
        } catch (UnknownHostException ex) {
            SERVER_INFO_LOGGER.error("Sorry. check your spring network. ({})", ex.getMessage(), ex);
        }
    }

    public String getServerIp() {
        return this.serverIp;
    }

    public boolean isAlreadySetServerDomainAndPort () {
        return StringObjectUtils.isValid(this.domain) && this.serverPort > 0;
    }

    private final static String DOMAIN_KEYWORD = "domain";
    private final static String SERVER_PORT_KEYWORD = "serverPort";

    private boolean isKeywordValidatedOnStaticInfo(final Map<String, Object> staticInfo, final String keyword) {
        return staticInfo.containsKey(keyword) && staticInfo.get(keyword) != null;
    }

    public void setStaticInfoFromRequest(final Map<String, Object> staticInfo) {
        if (this.isKeywordValidatedOnStaticInfo(staticInfo, DOMAIN_KEYWORD)) {
            this.domain = (String) staticInfo.get(DOMAIN_KEYWORD);
        }
        if (this.isKeywordValidatedOnStaticInfo(staticInfo, SERVER_PORT_KEYWORD)) {
            this.serverPort = (Integer) staticInfo.get(SERVER_PORT_KEYWORD);
        }
    }
}
