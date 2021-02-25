package it.nextworks.nfvmano.catalogue.domainLayer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;

import javax.validation.constraints.NotNull;
import java.util.Objects;


public class DomainCatalogueSubscriptionRequest {

  @JsonProperty("subscriptionType")
  private SubscriptionType subscriptionType = null;

  @JsonProperty("callbackURI")
  private String callbackURI = null;

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
    DomainCatalogueSubscriptionRequest experimentExecutionSubscriptionRequest = (DomainCatalogueSubscriptionRequest) o;
    return Objects.equals(this.subscriptionType, experimentExecutionSubscriptionRequest.subscriptionType) &&
        Objects.equals(this.callbackURI, experimentExecutionSubscriptionRequest.callbackURI);
  }

  @Override
  public int hashCode() {
    return Objects.hash(subscriptionType, callbackURI);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DomainCatalogueSubscriptionRequest {\n");
    
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
  }
}
