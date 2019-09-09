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
import it.nextworks.nfvmano.libs.common.DescriptorInformationElement;
import it.nextworks.nfvmano.libs.common.exceptions.MalformattedElementException;
import org.hibernate.annotations.*;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.validation.Valid;

import javax.validation.constraints.NotNull;

@Entity
public class CtxBlueprint implements DescriptorInformationElement {

    @Id
    @GeneratedValue
    @JsonIgnore
    protected Long id;

    @ManyToOne
    private ExpBlueprint expBlueprint;


    private String ctxBlueprintId;


    //TODO: Verify with Giada the exact scope

    //@JsonInclude(JsonInclude.Include.NON_EMPTY)
    //@Embedded
    //private CtxConstraints constraints = new CtxConstraints();



    //TODO: Verify with Giada
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Metric> metrics = new ArrayList<>();


    protected String version;

    protected String name;
    protected String description;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    protected List<VsBlueprintParameter> parameters = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @OneToMany
    @JoinTable(
            name="CtxBlueprintComponents",
            joinColumns = @JoinColumn( name="ctxblueprint_id"),
            inverseJoinColumns = @JoinColumn( name="vscomponent_id")
    )
    @LazyCollection(LazyCollectionOption.FALSE)
    protected List<VsComponent> atomicComponents = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    protected List<VsbEndpoint> endPoints = new ArrayList<>();


    @OneToMany
    @JsonIgnore
    @ElementCollection(fetch = FetchType.EAGER)
    @JoinTable(
            name="CtxBlueprintTranslationRules",
            joinColumns = @JoinColumn( name="ctxblueprint_id"),
            inverseJoinColumns = @JoinColumn( name="translationrule_id")
    )
    protected List<VsdNsdTranslationRule> ctxTranslationRules = new ArrayList<>();


    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ElementCollection(fetch=FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<VsbForwardingGraphEntry> serviceSequence = new ArrayList<>();


    /**
     * This is used to pass parameters to the NS instance
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ElementCollection(fetch=FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<String> configurableParameters = new ArrayList<>();

    public CtxBlueprint() {
    }

    public List<VsbForwardingGraphEntry> getServiceSequence() {
        return serviceSequence;
    }

    public CtxBlueprint(String ctxBlueprintId,
                        String version,
                        String name,
                        String description,
                        List<VsBlueprintParameter> parameters,
                        List<VsComponent> atomicComponents,
                        List<VsbEndpoint> endPoints,
                        //CtxConstraints constraints,
                        List<Metric> metrics,
                        List<String> configurableParameters,
                        List<VsbForwardingGraphEntry> serviceSequence,
                        List<VsdNsdTranslationRule> ctxTranslationRules) {
        this.version = version;
        this.name = name;
        this.description = description;
        if (parameters != null) {
            this.parameters = parameters;
        }
        if (atomicComponents != null) {
            this.atomicComponents = atomicComponents;
        }
        if (endPoints != null) {
            this.endPoints = endPoints;
        }
        this.ctxBlueprintId = ctxBlueprintId;
        //this.constraints = constraints;
        if (metrics != null) {
            this.metrics = metrics;
        }

        if (configurableParameters!=null){
            this.configurableParameters=configurableParameters;
        }
        if (serviceSequence!=null)
            this.serviceSequence=serviceSequence;

        if (ctxTranslationRules!=null){
            this.ctxTranslationRules= ctxTranslationRules;
        }
    }

    public void setCtxTranslationRules(List<VsdNsdTranslationRule> ctxTranslationRules) {
        this.ctxTranslationRules = ctxTranslationRules;
    }

    public List<Metric> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<Metric> metrics) {
        this.metrics = metrics;
    }

    public String getCtxBlueprintId() {
        return ctxBlueprintId;
    }

    public void setCtxBlueprintId(String ctxBlueprintId) {
        this.ctxBlueprintId = ctxBlueprintId;
    }

    /*public CtxConstraints getConstraints() {
        return constraints;
    }

    public void setConstraints(CtxConstraints constraints) {
        this.constraints = constraints;
    }*/


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<VsBlueprintParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<VsBlueprintParameter> parameters) {
        this.parameters = parameters;
    }



    public List<VsComponent> getAtomicComponents() {
        return atomicComponents;
    }

    public void setAtomicComponents(List<VsComponent> atomicComponents) {
        this.atomicComponents = atomicComponents;
    }

    public List<VsbEndpoint> getEndPoints() {
        return endPoints;
    }

    public void setEndPoints(List<VsbEndpoint> endPoints) {
        this.endPoints = endPoints;
    }

    public List<VsdNsdTranslationRule> getCtxTranslationRules() {
        return ctxTranslationRules;
    }

    public List<String> getConfigurableParameters() {
        return configurableParameters;
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
        if (ctxTranslationRules!=null){
            for (VsdNsdTranslationRule rule: ctxTranslationRules){
                rule.isValid();
            }
        }
    }

    public Long getId(){
        return id;
    }
}