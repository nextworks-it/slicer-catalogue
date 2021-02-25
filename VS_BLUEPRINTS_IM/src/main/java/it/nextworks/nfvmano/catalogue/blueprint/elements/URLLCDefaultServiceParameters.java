package it.nextworks.nfvmano.catalogue.blueprint.elements;

import java.util.HashMap;
import java.util.Map;

public class URLLCDefaultServiceParameters {

    public static URLLCServiceParameters getDefaultServiceParameters(URLLCServiceCategory category){


        //TODO verify values
        Map<URLLCServiceCategory, URLLCServiceParameters> values = new HashMap<>();

        values.put(URLLCServiceCategory.DISCRETE_AUTOMATION, new URLLCServiceParameters(10,
                10,
                10,
                10F,
                10F,
                10,
                10,
                "HIGH",
                10F,
                "LARGE"));
        values.put(URLLCServiceCategory.PROCESS_AUTOMATION_REMOTE_CONTROL, new URLLCServiceParameters(10,
                10,
                10,
                10F,
                10F,
                10,
                10,
                "HIGH",
                10F,
                "LARGE"));
        values.put(URLLCServiceCategory.PROCESS_AUTOMATION_MONITORING, new URLLCServiceParameters(10,
                10,
                10,
                10F,
                10F,
                10,
                10,
                "HIGH",
                10F,
                "LARGE"));
        values.put(URLLCServiceCategory.ELECTRICITY_DISTRIBUTION_HIGH_VOLTAGE, new URLLCServiceParameters(10,
                10,
                10,
                10F,
                10F,
                10,
                10,
                "HIGH",
                10F,
                "LARGE"));
        values.put(URLLCServiceCategory.ELECTRICITY_DISTRIBUTION_MEDIUM_VOLTAGE, new URLLCServiceParameters(10,
                10,
                10,
                10F,
                10F,
                10,
                10,
                "HIGH",
                10F,
                "LARGE"));
        values.put(URLLCServiceCategory.INTELLIGENT_TRANSPORT_SYSTEMS_INFRASTRUCTURE_BACKHAUL, new URLLCServiceParameters(10,
                10,
                10,
                10F,
                10F,
                10,
                10,
                "HIGH",
                10F,
                "LARGE"));

        values.put(URLLCServiceCategory.DISCRETE_AUTOMATION, new URLLCServiceParameters(10,
                10,
                10,
                10F,
                10F,
                10,
                10,
                "HIGH",
                10F,
                "LARGE"));
        return  values.get(category);


    }
}
