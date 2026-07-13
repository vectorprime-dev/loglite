package com.loglite.cli.config;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Root structure persisted to {@code ~/.loglite.json}, holding one or more named
 * connection profiles and which one is currently active.
 */
public class CliConfig {

    public static final String DEFAULT_PROFILE = "default";

    private Map<String, ConnectionProfile> profiles = new LinkedHashMap<>();
    private String activeProfile = DEFAULT_PROFILE;

    public Map<String, ConnectionProfile> getProfiles() {
        return profiles;
    }

    public void setProfiles(Map<String, ConnectionProfile> profiles) {
        this.profiles = profiles;
    }

    public String getActiveProfile() {
        return activeProfile;
    }

    public void setActiveProfile(String activeProfile) {
        this.activeProfile = activeProfile;
    }

    public ConnectionProfile getActive() {
        return profiles.computeIfAbsent(activeProfile, name -> new ConnectionProfile());
    }
}
