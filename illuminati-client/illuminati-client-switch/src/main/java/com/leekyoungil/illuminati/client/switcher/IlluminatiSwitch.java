package com.leekyoungil.illuminati.client.switcher;

import com.leekyoungil.illuminati.client.switcher.http.IlluminatiSwitchHttp;
import com.leekyoungil.illuminati.client.switcher.http.impl.IlluminatiSwitchHttpImpl;
import com.leekyoungil.illuminati.common.http.IlluminatiHttpClient;

public class IlluminatiSwitch {

    public static Thread ILLUMINATI_SWITCH_THREAD;

    private final static IlluminatiSwitchHttp ILLUMINATI_SWITCH_HTTP = new IlluminatiSwitchHttpImpl(new IlluminatiHttpClient(), "");

    static {
        Runnable runnable = new Runnable() {
            public void run() {
                while (true) {

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {

                    }
                }
            }
        };

        ILLUMINATI_SWITCH_THREAD = new Thread(runnable);
        ILLUMINATI_SWITCH_THREAD.setName("ILLUMINATI_SWITCH_THREAD");
        ILLUMINATI_SWITCH_THREAD.setDaemon(true);
        ILLUMINATI_SWITCH_THREAD.start();
    }
}