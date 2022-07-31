package me.phoboslabs.illuminati.common.util;

import java.net.Socket;

public class NetworkUtil {

    public static boolean canIConnect(String hostName, int portNumber) {
        try (Socket socket = new Socket(hostName, portNumber)) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
