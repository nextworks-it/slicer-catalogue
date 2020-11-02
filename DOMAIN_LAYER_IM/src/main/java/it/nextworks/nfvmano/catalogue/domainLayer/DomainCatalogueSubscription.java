package it.nextworks.nfvmano.catalogue.domainLayer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class DomainCatalogueSubscription {

  @Id
  @GeneratedValue
  @JsonIgnore
  private Long id;

  @JsonProperty("subscriptionId")
  private String subscriptionId = null;

  @JsonProperty("subscriptionType")
  private SubscriptionType subscriptionType = null;

  @JsonProperty("callbackURI")
  private String callbackURI = null;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getSubscriptionId() {
    return subscriptionId;
  }

  public void setSubscriptionId(String subscriptionId) {
    this.subscriptionId = subscriptionId;
  }

  public SubscriptionType getSubscriptionType() {
    return subscriptionType;
  }

  public void setSubscriptionType(SubscriptionType subscriptionType) {
    this.subscriptionType = subscriptionType;
  }

  public String getCallbackURI() {
    return callbackURI;
  }

  public void setCallbackURI(String callbackURI) {
    this.callbackURI = callbackURI;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DomainCatalogueSubscription experimentExecutionSubscription = (DomainCatalogueSubscription) o;
    return Objects.equals(this.id, experimentExecutionSubscription.id) &&
        Objects.equals(this.subscriptionId, experimentExecutionSubscription.subscriptionId)    &&
        Objects.equals(this.subscriptionType, experimentExecutionSubscription.subscriptionType) &&
        Objects.equals(this.callbackURI, experimentExecutionSubscription.callbackURI);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, subscriptionType, callbackURI, subscriptionId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DomainCatalogueSubscription {\n");

    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    subscriptionId: ").append(toIndentedString(subscriptionId)).append("\n");
    sb.append("    subscriptionType: ").append(toIndentedString(subscriptionType)).append("\n");
    sb.append("    callbackURI: ").append(toIndentedString(callbackURI)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

  @JsonIgnore
  public void isValid() throws MalformattedElementException {
    if(subscriptionType == null)
      throw new MalformattedElementException("subscriptionType cannot be null");
    if(callbackURI == null)
      throw new MalformattedElementException("callbackURI cannot be null");
    if(subscriptionId == null)
      throw new MalformattedElementException("subscriptionId cannot be null");
  }
}
