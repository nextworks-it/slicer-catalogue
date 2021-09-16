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
package it.nextworks.nfvmano.catalogue.template.elements;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.nextworks.nfvmano.libs.ifa.common.InterfaceInformationElement;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.NotExistingEntityException;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
		@JsonSubTypes.Type(value = RestNstConfigurationRule.class, 	name = "REST"),
		@JsonSubTypes.Type(value = SshNstConfigurationRule.class, 	name = "SSH")
})
@Entity
public abstract class NstConfigurationRule implements InterfaceInformationElement {

	@Id
	@GeneratedValue
    @JsonIgnore
    protected Long id;

	protected NstConfigurationRuleType type;

	protected String name;

	protected String nsdId;

	@ElementCollection(fetch = FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	protected List<String> params;

	//@JsonIgnore
	protected String nstId;

	protected String vnfdId;

	protected boolean day1;

	public NstConfigurationRule() { }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public NstConfigurationRuleType getType() {
		return type;
	}

	public void setType(NstConfigurationRuleType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNsdId() {
		return nsdId;
	}

	public void setNsdId(String nsdId) {
		this.nsdId = nsdId;
	}

	public List<String> getParams() {
		return params;
	}

	public void setParams(List<String> params) {
		this.params = params;
	}

	public String getNstId() {
		return nstId;
	}

	public void setNstId(String nstId) {
		this.nstId = nstId;
	}

	public String getVnfdId() { return vnfdId; }

	public void setVnfdId(String vnfdId) { this.vnfdId = vnfdId;}

	public boolean isDay1() { return day1; }

	public void setDay1(boolean day1) { this.day1 = day1;}

	@Override
	public void isValid() throws MalformattedElementException {
		if (type == null) throw new MalformattedElementException("NST configuration rule without type");
		if (nsdId == null) throw new MalformattedElementException("NST configuration rule without NSD ID");
		if (name == null) throw new MalformattedElementException("NST configuration rule without name");
	}

	@Override
	public String toString() {
		return "NstConfigurationRule{" +
				"id=" + id +
				", type=" + type +
				", name='" + name + '\'' +
				", nsdId='" + nsdId + '\'' +
				", params=" + params + '\'' +
				", vnfdId=" + vnfdId + '\'' +
				", day1=" + day1 + '\'' +
				'}';
	}
}
