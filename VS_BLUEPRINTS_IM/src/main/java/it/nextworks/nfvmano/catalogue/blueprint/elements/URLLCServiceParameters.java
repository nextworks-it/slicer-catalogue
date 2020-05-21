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


import java.util.HashMap;
import java.util.Map;
import javax.persistence.*;
/**
 * Based on: ETSI TS 122 261 V15.5.0 (2018-07)
 */


@Entity
public class URLLCServiceParameters extends  SliceServiceParameters {


    private Integer e2eKatency;
    private Integer jitter;
    private Integer survivalTime;
    private Float cSAvailability;
    private Float reliability;
    private Integer expDataRate;
    private Integer payloadSize;
    private String trafficDensity;
    private Float connDensity;
    private String serviceDimension;

    public URLLCServiceParameters(Integer e2eKatency, Integer jitter, Integer survivalTime, Float cSAvailability, Float reliability, Integer expDataRate, Integer payloadSize, String trafficDensity, Float connDensity, String serviceDimension) {
        this.e2eKatency = e2eKatency;
        this.jitter = jitter;
        this.survivalTime = survivalTime;
        this.cSAvailability = cSAvailability;
        this.reliability = reliability;
        this.expDataRate = expDataRate;
        this.payloadSize = payloadSize;
        this.trafficDensity = trafficDensity;
        this.connDensity = connDensity;
        this.serviceDimension = serviceDimension;
    }



    public URLLCServiceParameters(){};

    public Integer getE2eKatency() {
        return e2eKatency;
    }

    public Integer getJitter() {
        return jitter;
    }

    public Integer getSurvivalTime() {
        return survivalTime;
    }

    public Float getcSAvailability() {
        return cSAvailability;
    }

    public Float getReliability() {
        return reliability;
    }

    public Integer getExpDataRate() {
        return expDataRate;
    }

    public Integer getPayloadSize() {
        return payloadSize;
    }

    public String getTrafficDensity() {
        return trafficDensity;
    }

    public Float getConnDensity() {
        return connDensity;
    }

    public String getServiceDimension() {
        return serviceDimension;
    }

    @Override
    public Map<String, Object> getSliceServiceParameters(){
        Map<String, Object>  values = new HashMap<>();
        values.put("e2eKatency", e2eKatency);
        values.put("jitter", jitter);
        values.put("survivalTime", survivalTime);
        values.put("cSAvailability", cSAvailability);
        values.put("reliability", reliability);
        values.put("expDataRate", expDataRate);
        values.put("payloadSize", payloadSize);
        values.put("trafficDensity", trafficDensity);
        values.put("connDensity", connDensity);
        values.put("serviceDimension", serviceDimension);
        return values;
    }

}
