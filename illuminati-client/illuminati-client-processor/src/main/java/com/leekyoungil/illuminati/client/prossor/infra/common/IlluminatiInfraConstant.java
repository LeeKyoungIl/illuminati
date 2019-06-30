package com.leekyoungil.illuminati.client.prossor.infra.common;

import java.util.concurrent.atomic.AtomicBoolean;

public class IlluminatiInfraConstant {

    volatile public static AtomicBoolean IS_CANCONNECT_TO_REMOTE_BROKER = new AtomicBoolean(false);
}
