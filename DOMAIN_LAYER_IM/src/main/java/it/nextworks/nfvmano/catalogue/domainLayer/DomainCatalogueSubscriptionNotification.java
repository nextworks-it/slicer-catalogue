/*
* Copyright 2018 Nextworks s.r.l.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package it.nextworks.nfvmano.catalogue.domainLayer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.nextworks.nfvmano.libs.ifa.common.InterfaceMessage;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;

import java.util.Objects;

public class DomainCatalogueSubscriptionNotification {

	@JsonProperty("notificationType")
	private NotificationType type;

	@JsonProperty("domain")
	private Domain domain;

	public DomainCatalogueSubscriptionNotification() { }

	public DomainCatalogueSubscriptionNotification(NotificationType type, Domain domain) {
		this.type = type;
		this.domain = domain;
	}

	public NotificationType getType() {
		return type;
	}

	public void setType(NotificationType type) {
		this.type = type;
	}

	public Domain getDomain() {
		return domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		DomainCatalogueSubscriptionNotification experimentExecutionStateChangeNotification = (DomainCatalogueSubscriptionNotification) o;
		return Objects.equals(this.type, experimentExecutionStateChangeNotification.type) &&
				Objects.equals(this.domain, experimentExecutionStateChangeNotification.domain);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, domain);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class DomainCatalogueSubscriptionNotification {\n");

		sb.append("    type: ").append(toIndentedString(type)).append("\n");
		sb.append("    currentStatus: ").append(toIndentedString(domain)).append("\n");
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
		if (type == null)
			throw new MalformattedElementException("experimentExecutionId cannot be null");
		if(domain == null)
			throw new MalformattedElementException("currentStatus cannot be null");
	}
}
