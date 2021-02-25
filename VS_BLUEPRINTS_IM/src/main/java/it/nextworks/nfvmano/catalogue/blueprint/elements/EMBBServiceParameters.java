package it.nextworks.nfvmano.catalogue.blueprint.elements;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.*;

/**
 * Based on: ETSI TS 122 261 V15.5.0 (2018-07)
 */
@Entity
public class EMBBServiceParameters extends SliceServiceParameters {

    @JsonInclude(Include.NON_NULL)
    private Long expDataRateDL;
    @JsonInclude(Include.NON_NULL)
    private Long expDataRateUL;
    @JsonInclude(Include.NON_NULL)
    private Long areaTrafficCapDL;
    @JsonInclude(Include.NON_NULL)
    private Long areaTrafficCapUL;
    @JsonInclude(Include.NON_NULL)
    private Long userDensity;
    @JsonInclude(Include.NON_NULL)
    private Long uESpeed;
    @JsonInclude(Include.NON_NULL)
    private String coverage;


    public EMBBServiceParameters(){};

    public EMBBServiceParameters(Long expDataRateDL,
                                 Long expDataRateUL,
                                 Long areaTrafficCapDL,
                                 Long areaTrafficCapUL,
                                 Long userDensity,
                                 Long uESpeed,
                                 String coverage) {
        this.expDataRateDL = expDataRateDL;
        this.expDataRateUL = expDataRateUL;
        this.areaTrafficCapDL = areaTrafficCapDL;
        this.areaTrafficCapUL = areaTrafficCapUL;
        this.userDensity = userDensity;
        this.uESpeed = uESpeed;
        this.coverage = coverage;
    }


    public Long getExpDataRateDL() {
        return expDataRateDL;
    }

    public Long getExpDataRateUL() {
        return expDataRateUL;
    }

    public Long getAreaTrafficCapDL() {
        return areaTrafficCapDL;
    }

    public Long getAreaTrafficCapUL() {
        return areaTrafficCapUL;
    }

    public Long getUserDensity() {
        return userDensity;
    }

    public Long getuESpeed() {
        return uESpeed;
    }

    public String getCoverage() {
        return coverage;
    }

    @Override
    public Map<String, Object> getSliceServiceParameters(){

        Map<String, Object>  values = new HashMap<>();
        values.put("expDataRateDL", expDataRateDL);
        values.put("expDataRateUL", expDataRateUL);
        values.put("areaTrafficCapDL", areaTrafficCapDL);
        values.put("areaTrafficCapUL", areaTrafficCapUL);
        values.put("userDensity", userDensity);
        values.put("uESpeed", uESpeed);
        values.put("coverage", coverage);
        values.put("TYPE", "EMBB");
        return values;


    }
}
