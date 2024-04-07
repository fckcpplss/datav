package com.longfor.datav.common.utils;

/**
 * @author zhaoyl
 * @date 2024/1/29 10:31
 * @since 1.0
 */
import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IPAddressConverter extends ClassicConverter {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(IPAddressConverter.class);
    private static String ipAddress;

    public IPAddressConverter() {
    }

    public String convert(ILoggingEvent event) {
        return ipAddress;
    }

    static {
        try {
            ipAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException var1) {
            log.error("fetch localhost host address failed", var1);
            ipAddress = "UNKNOWN";
        }

    }
}
