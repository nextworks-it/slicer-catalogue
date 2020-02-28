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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;

import it.nextworks.nfvmano.libs.ifa.common.InterfaceInformationElement;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;

@Entity
public class TestCaseBlueprintInfo implements InterfaceInformationElement{

	@Id
    @GeneratedValue
    @JsonIgnore
    private Long id;
	
	private String testCaseBlueprintId;
	private String version;
	private String name;
	private String owner;
	
	@Transient
	private TestCaseBlueprint testCaseBlueprint;
	
	@ElementCollection(fetch=FetchType.EAGER)
	@Fetch(FetchMode.SELECT)
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	private List<String> activeTcdId = new ArrayList<>();
	
	public TestCaseBlueprintInfo() {
		// JPA only
	}
	
	/**
	 * Constructor
	 * 
	 * @param testCaseBlueprintId ID of the test case blueprint
	 * @param version version of the test case blueprint
	 * @param name name of the test case blueprint
	 * @param owner owner user of the test case blueprint
	 */
	public TestCaseBlueprintInfo(String testCaseBlueprintId,
			String version, 
			String name,
								 String owner) {
		this.testCaseBlueprintId = testCaseBlueprintId;
		this.version = version;
		this.name = name;
	}
	

	
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the testCaseBlueprint
	 */
	public TestCaseBlueprint getTestCaseBlueprint() {
		return testCaseBlueprint;
	}



	/**
	 * @param testCaseBlueprint the testCaseBlueprint to set
	 */
	public void setTestCaseBlueprint(TestCaseBlueprint testCaseBlueprint) {
		this.testCaseBlueprint = testCaseBlueprint;
	}



	/**
	 * @return the testCaseBlueprintId
	 */
	public String getTestCaseBlueprintId() {
		return testCaseBlueprintId;
	}



	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}


	@JsonIgnore
	public String getOwner() {
		return owner;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}



	/**
	 * @return the activeTcdId
	 */
	public List<String> getActiveTcdId() {
		return activeTcdId;
	}

	/**
	 * Associate a new active test case descriptor to the test case blueprint
	 * 
	 * @param tcdId ID of the test case descriptor to be associated to the given blueprint
	 */
	public void addTcd(String tcdId) {
		if (!(activeTcdId.contains(tcdId)))
			activeTcdId.add(tcdId);
	}
	
	/**
	 * De-associate an active test case descriptor from the test case blueprint
	 * 
	 * @param tcdId ID of the test case descriptor to be de-associated from the given blueprint
	 */
	public void removeTcd(String tcdId) {
		if (activeTcdId.contains(tcdId))
			activeTcdId.remove(tcdId);
	}

	public void removeAllTcds() {
		this.activeTcdId = new ArrayList<String>();
	}
	

	@Override
	public void isValid() throws MalformattedElementException {
		if (name == null) throw new MalformattedElementException("Test case blueprint info without name");
		if (version == null) throw new MalformattedElementException("Test case blueprint info without version");
	}
	
}
