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


import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.*;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.persistence.Entity;

@Entity(name = "ContextBlueprint")
@DiscriminatorValue("CTXB")
public class CtxBlueprint extends Blueprint {

//    @Id
//    @GeneratedValue
//    @JsonIgnore
//    protected Long id;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ElementCollection(fetch=FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
	private List<EveSite> compatibleSites = new ArrayList<>();

    public CtxBlueprint() {
    	//JPA only
    }
    
    public CtxBlueprint(String blueprintId,
			String version,
			String name,
			String description,
			List<VsBlueprintParameter> parameters,
			List<VsbEndpoint> endPoints,
			List<String> configurableParameters, 
			List<EveSite> compatibleSites,
			List<ApplicationMetric> applicationMetrics) {
    	super(blueprintId, version, name, description, parameters, endPoints, 
    			configurableParameters, applicationMetrics);
		if (compatibleSites != null) this.compatibleSites = compatibleSites;
    }    

    /**
	 * @return the compatibleSites
	 */
	public List<EveSite> getCompatibleSites() {
		return compatibleSites;
	}

}