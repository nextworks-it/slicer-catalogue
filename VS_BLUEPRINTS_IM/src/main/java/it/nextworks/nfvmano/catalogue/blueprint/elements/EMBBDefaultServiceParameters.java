package it.nextworks.nfvmano.catalogue.blueprint.elements;

import java.util.HashMap;
import java.util.Map;

public class EMBBDefaultServiceParameters {


    public static EMBBServiceParameters getDefaultServiceParameters(EMBBServiceCategory category){

        Map<EMBBServiceCategory, EMBBServiceParameters> defaultMap = new HashMap<>();

        defaultMap.put(EMBBServiceCategory.AIRPLANES_CONNECTIVITY,
                new EMBBServiceParameters(15L,
                7500000L,
                1200000000L,
                600000000L,
                400L,
                1L,
                null));


        defaultMap.put(EMBBServiceCategory.URBAN_MACRO,
                new EMBBServiceParameters(50000000L,
                        25000000L,
                        100000000000L,
                        600000000L,
                        400L,
                        1L,
                        null));

        defaultMap.put(EMBBServiceCategory.RURAL_MACRO,
                new EMBBServiceParameters(50000000L,
                        25000000L,
                        100000000000L,
                        600000000L,
                        400L,
                        1L,
                        null));

        defaultMap.put(EMBBServiceCategory.INDOOR_HOTSPOT,
                new EMBBServiceParameters(50000000L,
                        25000000L,
                        100000000000L,
                        600000000L,
                        400L,
                        1L,
                        null));


        defaultMap.put(EMBBServiceCategory.BROADBAND_ACCESS_IN_A_CROWD,
                new EMBBServiceParameters(50000000L,
                        25000000L,
                        100000000000L,
                        600000000L,
                        400L,
                        1L,
                        null));

        defaultMap.put(EMBBServiceCategory.DENSE_URBAN,
                new EMBBServiceParameters(50000000L,
                        25000000L,
                        100000000000L,
                        600000000L,
                        400L,
                        1L,
                        null));
        defaultMap.put(EMBBServiceCategory.BROADBAND_LIKE_SERVICES,
                new EMBBServiceParameters(50000000L,
                        25000000L,
                        100000000000L,
                        600000000L,
                        400L,
                        1L,
                        null));
        defaultMap.put(EMBBServiceCategory.HIGH_SPEED_TRAIN,
                new EMBBServiceParameters(50000000L,
                        25000000L,
                        100000000000L,
                        600000000L,
                        400L,
                        1L,
                        null));

        defaultMap.put(EMBBServiceCategory.HIGH_SPEED_VEHICLE,
                new EMBBServiceParameters(50000000L,
                        25000000L,
                        100000000000L,
                        600000000L,
                        400L,
                        1L,
                        null));

        return defaultMap.get(category);
    }
}
