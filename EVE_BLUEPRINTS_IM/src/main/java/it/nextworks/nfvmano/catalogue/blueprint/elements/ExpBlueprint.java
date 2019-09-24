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

import it.nextworks.nfvmano.libs.ifa.common.DescriptorInformationElement;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;

import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class ExpBlueprint  implements DescriptorInformationElement {



    @Id
    @GeneratedValue
    @JsonIgnore
    private Long id;


    private String expBlueprintId;

    private String version;
    private String name;
    private String description;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ElementCollection(fetch=FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<EveSite> sites = new ArrayList<>();


    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @OneToMany(mappedBy = "blueprint", cascade=CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<KeyPerformanceIndicator> kpis = new ArrayList<>();


    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ElementCollection(fetch=FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<InfrastructureMetric> metrics = new ArrayList<>();


    private String vsBlueprintId;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ElementCollection(fetch=FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<String> ctxBlueprintIds = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ElementCollection(fetch=FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<String> tcBlueprintIds = new ArrayList<>();
    
    public ExpBlueprint() {
    	//JPA only
    }

    public ExpBlueprint(String version, String name, String description, 
    		List<EveSite> sites, String vsBlueprintId, List<String> ctxBlueprintIds, 
    		List<String> tcBlueprintIds, List<InfrastructureMetric> metrics) {
        this.version = version;
        this.name = name;
        this.description = description;
        if(sites!=null)
            this.sites = sites;
        this.vsBlueprintId = vsBlueprintId;
        if (ctxBlueprintIds != null) 
        	this.ctxBlueprintIds = ctxBlueprintIds;
        if (tcBlueprintIds !=  null)
        	this.tcBlueprintIds = tcBlueprintIds;
        if(metrics!=null)
            this.metrics = metrics;
    } 

    public String getVersion() {
        return version;
    }

    public String getExpBlueprintId() {
        return expBlueprintId;
    }

    public List<EveSite> getSites() {
        return sites;
    }

    public String getVsBlueprintId() {
        return vsBlueprintId;
    }

    
    public List<String> getCtxBlueprintIds() {
        return ctxBlueprintIds;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    

    public String getDescription() {
        return description;
    }

    

    /**
	 * @return the tcBlueprintIds
	 */
	public List<String> getTcBlueprintIds() {
		return tcBlueprintIds;
	}

	/**
	 * @param expBlueprintId the expBlueprintId to set
	 */
	public void setExpBlueprintId(String expBlueprintId) {
		this.expBlueprintId = expBlueprintId;
	}

	@Override
    public void isValid() throws MalformattedElementException {
        if(name==null || name.isEmpty())
            throw new MalformattedElementException("ExpBlueprint without name");

        if(version==null || version.isEmpty())
            throw new MalformattedElementException("ExpBlueprint without version");

        if(sites==null || sites.isEmpty())
            throw new MalformattedElementException("ExpBlueprint without sites");

        Set<String>  kpiIdSet = kpis.stream()
                        .map(kpi -> kpi.getKpiId())
                        .collect(Collectors.toSet());
        //Check for duplicate kpi ids
        if(kpiIdSet.size()!=kpis.size())
            throw  new MalformattedElementException("Duplicate KPI id inside the ExpBlueprint");

        Set<String>  metricIdSet = metrics.stream()
                .map(metric -> metric.getMetricId())
                .collect(Collectors.toSet());
        //Check for duplicate kpi ids
        if(metricIdSet.size()!=metrics.size())
            throw  new MalformattedElementException("Duplicate Metric id inside the ExpBlueprint");
        if (kpis != null) {
        	for (KeyPerformanceIndicator kpi : kpis) kpi.isValid();
        }
        if (metrics != null) {
        	for (InfrastructureMetric m : metrics) m.isValid();
        }
        if (vsBlueprintId == null) throw new MalformattedElementException("Experiment blueprint without vertical service blueprint");
        if ( (tcBlueprintIds == null) || (tcBlueprintIds.isEmpty())) throw new MalformattedElementException("Experiment blueprint without test cases");
    }

    public List<KeyPerformanceIndicator> getKpis() {
        return kpis;
    }

    public List<InfrastructureMetric> getMetrics() {
        return metrics;
    }

}