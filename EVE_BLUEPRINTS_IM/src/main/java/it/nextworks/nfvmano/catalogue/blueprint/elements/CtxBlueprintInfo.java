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

import it.nextworks.nfvmano.libs.ifa.common.InterfaceInformationElement;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class CtxBlueprintInfo implements InterfaceInformationElement {

	@Id
    @GeneratedValue
    @JsonIgnore
    private Long id;

	private String ctxBlueprintId;
	private String ctxBlueprintVersion;
	private String name;
	private String owner;

	@Transient
	private CtxBlueprint ctxBlueprint;

	@ElementCollection(fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	private List<String> onBoardedNsdInfoId = new ArrayList<>();

	@ElementCollection(fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	private List<String> activeCtxdId = new ArrayList<>();

	public CtxBlueprintInfo() {	}



	/**
	 * @param ctxBlueprintId
	 * @param ctxBlueprintVersion
	 * @param name
	 * @param owner
	 */
	public CtxBlueprintInfo(String ctxBlueprintId, String ctxBlueprintVersion, String name, String owner) {
		this.ctxBlueprintId = ctxBlueprintId;
		this.ctxBlueprintVersion = ctxBlueprintVersion;
		this.name = name;
		this.owner=owner;
	}



	/**
	 * @return the id
	 */
	@JsonIgnore
	public Long getId() {
		return id;
	}



	/**
	 * @return the ctxBlueprint
	 */
	public CtxBlueprint getCtxBlueprint() {
		return ctxBlueprint;
	}

	/**
	 * @param ctxBlueprint the ctxBlueprint to set
	 */
	public void setCtxBlueprint(CtxBlueprint ctxBlueprint) {
		this.ctxBlueprint = ctxBlueprint;
	}

	/**
	 * @return the ctxBlueprintId
	 */
	public String getCtxBlueprintId() {
		return ctxBlueprintId;
	}

	/**
	 * @return the ctxBlueprintVersion
	 */
	public String getCtxBlueprintVersion() {
		return ctxBlueprintVersion;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the onBoardedNsdInfoId
	 */
	public List<String> getOnBoardedNsdInfoId() {
		return onBoardedNsdInfoId;
	}

	/**
	 * @return the activeCtxdId
	 */
	public List<String> getActiveCtxdId() {
		return activeCtxdId;
	}


	public String getOwner() {
		return owner;
	}

	public void addCtxd(String ctxdId) {
		if (!(activeCtxdId.contains(ctxdId)))
			activeCtxdId.add(ctxdId);
	}
	
	public void removeCtxd(String ctxdId) {
		if (activeCtxdId.contains(ctxdId))
			activeCtxdId.remove(ctxdId);
	}
	
	public void addNsdInfoId(String nsdInfoId) {
		onBoardedNsdInfoId.add(nsdInfoId);
	}
	
	public void removeAllCtxd() {
		this.activeCtxdId = new ArrayList<String>();
	}
	
	@Override
	public void isValid() throws MalformattedElementException {
		if (ctxBlueprintId == null) throw new MalformattedElementException("ctx Blueprint info without ctx ID");
		if (ctxBlueprintVersion == null) throw new MalformattedElementException("ctx Blueprint info without ctx version");
		if (name == null) throw new MalformattedElementException("ctx Blueprint info without ctx name");
	}
}
