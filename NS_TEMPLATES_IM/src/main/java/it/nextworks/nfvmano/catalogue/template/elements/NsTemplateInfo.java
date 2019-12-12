/*
 * Copyright (c) 2019 Nextworks s.r.l
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.nextworks.nfvmano.catalogue.template.elements;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import it.nextworks.nfvmano.libs.ifa.common.InterfaceInformationElement;
import it.nextworks.nfvmano.libs.ifa.common.enums.OperationalState;
import it.nextworks.nfvmano.libs.ifa.common.enums.UsageState;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.templates.NST;

@Entity
public class NsTemplateInfo implements InterfaceInformationElement {
	
	@Id
    @GeneratedValue
    @JsonIgnore
    private Long id;
	
	private String nstInfoId;
	private String nsTemplateId;
	private String name;
	private String nsTemplateVersion;
	private String designer;
	
	@Transient
	private NST nst;
	
	
	private OperationalState operationalState;
	private UsageState usageState;
	private boolean deletionPending;
	private SliceServiceType sst;
	
	@ElementCollection
	private Map<String, String> userDefinedData = new HashMap<>();
	
	public NsTemplateInfo() {
		
	}
	
	
	public NsTemplateInfo(String nstInfoId, String nsTemplateId, 
			String name, String nsTemplateVersion, String designer, NST nst,
			OperationalState operationalState, UsageState usageState,
			boolean deletionPending) {
		this.nstInfoId=nstInfoId;
		this.nsTemplateId=nsTemplateId;
		this.name=name;
		this.setNsTemplateVersion(nsTemplateVersion);
		this.designer=designer;
		this.nst=nst;
		this.operationalState=operationalState;
		this.usageState=usageState;
		this.deletionPending=deletionPending;
		
	}
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id=id;
	}
	public String getNstInfoId(){
		return nstInfoId;
	}
	
	public void setNstInfoId(String nstInfoId) {
		this.nstInfoId=nstInfoId;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getDesigner() {
		return designer;
	}


	public void setDesigner(String designer) {
		this.designer = designer;
	}

	public void setNST(NST nst) {
		this.nst=nst;
	}
	
	public NST getNST() {
		return nst;
	}
	public OperationalState getOperationalState() {
		return operationalState;
	}


	public void setOperationalState(OperationalState operationalState) {
		this.operationalState = operationalState;
	}


	public UsageState getUsageState() {
		return usageState;
	}


	public void setUsageState(UsageState usageState) {
		this.usageState = usageState;
	}


	public boolean isDeletionPending() {
		return deletionPending;
	}


	public void setDeletionPending(boolean deletionPending) {
		this.deletionPending = deletionPending;
	}


	public Map<String, String> getUserDefinedData() {
		return userDefinedData;
	}


	public void setUserDefinedData(Map<String, String> userDefinedData) {
		this.userDefinedData = userDefinedData;
	}
	
	
    @Override
    public void isValid() throws MalformattedElementException {
    	if (nstInfoId == null) throw new MalformattedElementException("NsTemplate info without ID");
		if (nsTemplateVersion == null) throw new MalformattedElementException("NsTemplate info without version");
		if (name == null) throw new MalformattedElementException("NsTemplate info without name");
    }
    
	public SliceServiceType getSst() {
		return sst;
	}

	public void setSst(SliceServiceType sst) {
		this.sst = sst;
	}

	public String getNsTemplateId() {
		return nsTemplateId;
	}

	public void setNsTemplateId(String nsTemplateId) {
		this.nsTemplateId = nsTemplateId;
	}

	public String getNsTemplateVersion() {
		return nsTemplateVersion;
	}

	public void setNsTemplateVersion(String nsTemplateVersion) {
		this.nsTemplateVersion = nsTemplateVersion;
	}
}
