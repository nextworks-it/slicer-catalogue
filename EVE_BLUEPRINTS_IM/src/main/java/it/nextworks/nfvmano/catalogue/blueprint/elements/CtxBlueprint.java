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
import com.fasterxml.jackson.annotation.JsonInclude;
import it.nextworks.nfvmano.libs.common.exceptions.MalformattedElementException;
import org.hibernate.annotations.*;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("CTXB")
public class CtxBlueprint extends Blueprint {

    @Id
    @GeneratedValue
    @JsonIgnore
    protected Long id;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ElementCollection(fetch=FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
	private List<EveSite> compatibleSites = new ArrayList<>();

    public CtxBlueprint(String blueprintId,
			String version,
			String name,
			String description,
			List<VsBlueprintParameter> parameters,
			List<VsbEndpoint> endPoints,
			List<String> configurableParameters, 
			List<EveSite> compatibleSites) {
    	super(blueprintId, version, name, description, parameters, endPoints, configurableParameters);
		if (compatibleSites != null) this.compatibleSites = compatibleSites;
    }

    

    //This should be used only for testing purposes
    public void setId(Long id){
        this.id=id;
    }

    @Override
    public void isValid() throws MalformattedElementException {
        for (VsBlueprintParameter p : parameters) {
            p.isValid();
        }
        if (version == null) {
            throw new MalformattedElementException("CTX blueprint without version");
        }
        if (name == null) {
            throw new MalformattedElementException("CTX blueprint without name");
        }
        if (atomicComponents != null) {
            for (VsComponent c : atomicComponents) {
                c.isValid();
            }
        }
        if (endPoints != null) {
            for (VsbEndpoint e : endPoints) {
                e.isValid();
            }
        }
    }

    public Long getId(){
        return id;
    }
}