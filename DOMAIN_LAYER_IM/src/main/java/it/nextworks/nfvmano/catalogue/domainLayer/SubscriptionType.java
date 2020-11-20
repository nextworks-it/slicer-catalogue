package it.nextworks.nfvmano.catalogue.domainLayer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets subscriptionType
 */
public enum SubscriptionType {
  DOMAIN_SUBSCRIPTION("DOMAIN_SUBSCRIPTION");

  private String value;

  SubscriptionType(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static SubscriptionType fromValue(String text) {
    for (SubscriptionType b : SubscriptionType.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}
