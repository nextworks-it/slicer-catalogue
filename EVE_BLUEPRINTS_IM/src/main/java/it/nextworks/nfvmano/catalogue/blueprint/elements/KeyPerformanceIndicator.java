package it.nextworks.nfvmano.catalogue.blueprint.elements;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import it.nextworks.nfvmano.libs.ifa.common.DescriptorInformationElement;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;

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
    private String interval;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ElementCollection(fetch= FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<String> metricIds = new ArrayList<>();

    public KeyPerformanceIndicator() { }

    public KeyPerformanceIndicator(ExpBlueprint blueprint,
                                    String kpiId,
                                    String name,
                                    String formula,
                                    String unit,                                   
                                    List<String> metricIds,
                                    String interval) {
        this.blueprint = blueprint;
        this.kpiId = kpiId;
        this.name = name;
        this.formula = formula;
        this.unit = unit;
        if(metricIds!=null)
            this.metricIds = metricIds;
        this.interval = interval;
    }
    
    

    /**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the blueprint
	 */
	public ExpBlueprint getBlueprint() {
		return blueprint;
	}

	/**
	 * @return the kpiId
	 */
	public String getKpiId() {
		return kpiId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the formula
	 */
	public String getFormula() {
		return formula;
	}

	/**
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * @return the interval
	 */
	public String getInterval() {
		return interval;
	}

	/**
	 * @return the metricIds
	 */
	public List<String> getMetricIds() {
		return metricIds;
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
        if(metricIds.size()<1)
            throw  new MalformattedElementException("Missing KPI metrics");
        if(interval==null || interval.equals(""))
            throw  new MalformattedElementException("Missing KPI interval");
    }


}