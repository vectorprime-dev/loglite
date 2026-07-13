package com.loglite.slf4j;

import com.loglite.core.MDC;
import org.slf4j.spi.MDCAdapter;

import java.util.Map;

/**
 * Bridges SLF4J's {@link MDCAdapter} contract onto {@link com.loglite.core.MDC}.
 */
public class LogliteMDCAdapter implements MDCAdapter {

    @Override
    public void put(String key, String val) {
        MDC.put(key, val);
    }

    @Override
    public String get(String key) {
        return MDC.get(key);
    }

    @Override
    public void remove(String key) {
        MDC.remove(key);
    }

    @Override
    public void clear() {
        MDC.clear();
    }

    @Override
    public Map<String, String> getCopyOfContextMap() {
        return MDC.getCopyOfContextMap();
    }

    @Override
    public void setContextMap(Map<String, String> contextMap) {
        MDC.clear();
        if (contextMap != null) {
            contextMap.forEach(MDC::put);
        }
    }
}
