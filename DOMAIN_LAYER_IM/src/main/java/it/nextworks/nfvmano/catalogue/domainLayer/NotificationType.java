package it.nextworks.nfvmano.catalogue.domainLayer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum NotificationType {
    DOMAIN_ONBOARDING("DOMAIN_ONBOARDING"),
    DOMAIN_REMOVAL("DOMAIN_REMOVAL");

    private String value;

    NotificationType(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static NotificationType fromValue(String text) {
        for (NotificationType b : NotificationType.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }
}