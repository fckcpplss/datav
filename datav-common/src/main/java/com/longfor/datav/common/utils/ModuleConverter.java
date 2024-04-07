package com.longfor.datav.common.utils;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
/**
 * @author zhaoyl
 * @date 2024/1/29 10:35
 * @since 1.0
 */

public class ModuleConverter extends ClassicConverter {
    private static final int MAX_LENGTH = 20;

    public ModuleConverter() {
    }

    public String convert(ILoggingEvent event) {
        return event.getLoggerName().length() > 20 ? "" : event.getLoggerName();
    }
}
