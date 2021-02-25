package it.nextworks.nfvmano.catalogue.blueprint;

import java.util.HashMap;
import java.util.Map;

import it.nextworks.nfvmano.libs.ifa.common.elements.Filter;

public class EveportalCatalogueUtilities {



    public static Filter buildCtxBlueprintFilter(String ctxbId) {
        //CTXB_ID
        Map<String, String> filterParams = new HashMap<>();
        filterParams.put("CTXB_ID", ctxbId);
        return new Filter(filterParams);
    }
    
    public static Filter buildCtxBlueprintFilter(String name, String version) {
        //CTXB_ID
        Map<String, String> filterParams = new HashMap<>();
        filterParams.put("CTXB_NAME", name);
        filterParams.put("CTXB_VERSION", version);
        return new Filter(filterParams);
    }

    public static Filter buildExpBlueprintFilter(String expbId) {
        //EXPB_ID
        Map<String, String> filterParams = new HashMap<>();
        filterParams.put("EXPB_ID", expbId);
        return new Filter(filterParams);
    }


    public static Filter buildExpDescriptorFilter(String expdId, String tenantId) {
        //EXPD_ID & TENANT_ID
        Map<String, String> filterParams = new HashMap<>();
        filterParams.put("EXPD_ID", expdId);
        filterParams.put("TENANT_ID", tenantId);
        return new Filter(filterParams);
    }

    public static Filter buildCtxDescriptorFilter(String ctxdId, String tenantId) {
        //CTXD_ID & TENANT_ID
        Map<String, String> filterParams = new HashMap<>();
        filterParams.put("CTXD_ID", ctxdId);
        filterParams.put("TENANT_ID", tenantId);
        return new Filter(filterParams);
    }
    
    public static Filter buildTestCaseBlueprintFilterFromId(String tcbId) {
        //TCB_ID
        Map<String, String> filterParams = new HashMap<>();
        filterParams.put("TCB_ID", tcbId);
        return new Filter(filterParams);
    }
    
    public static Filter buildTestCaseDescriptorFilterFromId(String tcdId, String tenantId) {
        //TCD_ID
        Map<String, String> filterParams = new HashMap<>();
        filterParams.put("TCD_ID", tcdId);
        filterParams.put("TENANT_ID", tenantId);
        return new Filter(filterParams);
    }


}
