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
package it.nextworks.nfvmano.catalogue.blueprint.elements;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.nextworks.nfvmano.libs.common.InterfaceInformationElement;
import it.nextworks.nfvmano.libs.common.exceptions.MalformattedElementException;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ExpBlueprintInfo implements InterfaceInformationElement {
	
	@Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

	private String expBlueprintId;
	private String expBlueprintVersion;
	private String name;
	
	@Transient
	private ExpBlueprint expBlueprint;
	

	@ElementCollection(fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	private List<String> activeExpdId = new ArrayList<>();

	@ElementCollection(fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	private List<String> onBoardedNsdInfoId = new ArrayList<>();

	public ExpBlueprintInfo() {	}
	
	

	/**
	 * @param expBlueprintId
	 * @param expBlueprintVersion
	 * @param name
	 */
	public ExpBlueprintInfo(String expBlueprintId, String expBlueprintVersion, String name) {
		this.expBlueprintId = expBlueprintId;
		this.expBlueprintVersion = expBlueprintVersion;
		this.name = name;
	}



	/**
	 * @return the id
	 */
	@JsonIgnore
	public Long getId() {
		return id;
	}



	public List<String> getOnBoardedNsdInfoId() {
		return onBoardedNsdInfoId;
	}

	/**
	 * @return the ExpBlueprint
	 */
	public ExpBlueprint getExpBlueprint() {
		return expBlueprint;
	}

	/**
	 * @param expBlueprint the ExpBlueprint to set
	 */
	public void setExpBlueprint(ExpBlueprint expBlueprint) {
		this.expBlueprint = expBlueprint;
	}

	/**
	 * @return the expBlueprintId
	 */
	public String getExpBlueprintId() {
		return expBlueprintId;
	}

	/**
	 * @return the expBlueprintVersion
	 */
	public String getExpBlueprintVersion() {
		return expBlueprintVersion;
	}



	/**
	 * @return the activeExpdId
	 */
	public List<String> getActiveExpdId() {
		return activeExpdId;
	}
	
	public void addExpd(String expdId) {
		if (!(activeExpdId.contains(expdId)))
			activeExpdId.add(expdId);
	}
	
	public void removeExpd(String expdId) {
		if (activeExpdId.contains(expdId))
			activeExpdId.remove(expdId);
	}


	public void addNsdInfoId(String nsdInfoId) {
		onBoardedNsdInfoId.add(nsdInfoId);
	}

	
	@Override
	public void isValid() throws MalformattedElementException {
		if (expBlueprintId == null) throw new MalformattedElementException("EXP Blueprint info without EXP ID");
		if (expBlueprintVersion == null) throw new MalformattedElementException("EXP Blueprint info without EXP version");
		if (name == null) throw new MalformattedElementException("EXP Blueprint info without Exp name");
	}
	
	public String getName() {
		return name;
	}
}
