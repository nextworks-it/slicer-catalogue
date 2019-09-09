package it.nextworks.nfvmano.catalogue.blueprint.elements;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.nextworks.nfvmano.libs.common.DescriptorInformationElement;
import it.nextworks.nfvmano.libs.common.exceptions.MalformattedElementException;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;


@Entity
public class KeyPerformanceIndicator implements DescriptorInformationElement {

    @Id
    @GeneratedValue
    @JsonIgnore
    protected Long id;

    @ManyToOne
    @JsonIgnore
    private ExpBlueprint blueprint;


    private String kpiId;


    private String name;


    private String formula;


    private String unit;

    //@NotBlank
    //private String objective;

    public void setBlueprint(ExpBlueprint blueprint) {
        this.blueprint = blueprint;
    }


    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ElementCollection(fetch= FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<String> metrics;


    private String interval;

    public KeyPerformanceIndicator() {
    }

    public KeyPerformanceIndicator(ExpBlueprint blueprint,
                                    String kpiId,
                                    String name,
                                   String formula,
                                    String unit,
                                   //@NotBlank String objective,
                                   List<String> metrics,
                                   String interval) {
        this.blueprint = blueprint;
        this.kpiId = kpiId;
        this.name = name;
        this.formula = formula;
        this.unit = unit;
        //this.objective = objective;
        if(metrics!=null)
            this.metrics = metrics;
        this.interval = interval;
    }

    public String getKpiId() {
        return kpiId;
    }

    public void setKpiId(String kpiId) {
        this.kpiId = kpiId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    /*
    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }
     */

    public List<String> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<String> metrics) {
        this.metrics = metrics;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    @Override
    public void isValid() throws MalformattedElementException {

        if(name==null || name.equals(""))
            throw  new MalformattedElementException("Missing KPI name");
        if(kpiId==null || kpiId.equals(""))
            throw  new MalformattedElementException("Missing KPI id");
        if(formula==null || formula.equals(""))
            throw  new MalformattedElementException("Missing KPI name");
        if(unit==null || unit.equals(""))
            throw  new MalformattedElementException("Missing KPI name");
        if(metrics.size()<1)
            throw  new MalformattedElementException("Missing KPI metrics");
        if(interval==null || interval.equals(""))
            throw  new MalformattedElementException("Missing KPI interval");
    }


}