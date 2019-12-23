import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.nextworks.nfvmano.catalogue.SpringAppNSTCatalogue;
import it.nextworks.nfvmano.catalogues.template.services.NsTemplateCatalogueService;
import it.nextworks.nfvmano.libs.ifa.templates.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import it.nextworks.nfvmano.catalogue.template.elements.NsTemplateInfo;
import it.nextworks.nfvmano.catalogue.template.messages.OnBoardNsTemplateRequest;
import it.nextworks.nfvmano.catalogue.template.messages.QueryNsTemplateResponse;

import it.nextworks.nfvmano.libs.ifa.common.elements.Filter;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.AlreadyExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.FailedOperationException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MethodNotImplementedException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.NotExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.messages.GeneralizedQueryRequest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= SpringAppNSTCatalogue.class)
public class NsTemplateCatalogueServiceTest {


    private static final Logger log = LoggerFactory.getLogger(NsTemplateCatalogueServiceTest.class);

    @Autowired
    private NsTemplateCatalogueService nsTemplateCatalogueService;

    private NST nst;


    public NsTemplateCatalogueServiceTest() {
        /*Creating sample NstServiceProfile and sample NST*/
        //nsTemplateCatalogueService = new NsTemplateCatalogueService();


        EMBBPerfReq embbPerfReq1 = new EMBBPerfReq(100, 10, 200, 20, 200000, 3, 0,"Coverage");
        EMBBPerfReq embbPerfReq2 = new EMBBPerfReq(101, 11, 201, 21, 210000, 4, 1,"Coverage1");
        ArrayList<EMBBPerfReq> embbPerfReq = new ArrayList<EMBBPerfReq>();
        embbPerfReq.add(embbPerfReq1);
        embbPerfReq.add(embbPerfReq2);

        URLLCPerfReq urLLCPerfReq1 = new URLLCPerfReq(20, 200, 200, 2.0f, 20.0f, 200, "PayloadSize", 200, 200,"ServiceAreaDimension");
        URLLCPerfReq urLLCPerfReq2 = new URLLCPerfReq(21, 201, 201, 2.1f, 20.1f, 201, "PayloadSize2", 201, 201,"ServiceAreaDimension2");
        ArrayList<URLLCPerfReq> urLLCPerfReq = new ArrayList<URLLCPerfReq>();
        urLLCPerfReq.add(urLLCPerfReq1);
        urLLCPerfReq.add(urLLCPerfReq2);

        NstServiceProfile nstServiceProfile= new NstServiceProfile();
        ArrayList<String> pLMNId=createArrayListWithRandomValuesIn(3, "pLMNId");
        ArrayList<String> coverageAreaTAList=createArrayListWithRandomValuesIn(3, "ca");

        nstServiceProfile.setpLMNIdList(pLMNId);
        nstServiceProfile.setMaxNumberofUEs(20000);
        nstServiceProfile.setCoverageAreaTAList(coverageAreaTAList);
        nstServiceProfile.setLatency(20);
        nstServiceProfile.setuEMobilityLevel(UEMobilityLevel.STATIONARY);
        nstServiceProfile.setResourceSharingLevel(false);
        nstServiceProfile.setsST(SliceType.EMBB);
        nstServiceProfile.setAvailability(9.99f);
        nstServiceProfile.seteMBBPerfReq(embbPerfReq);
        nstServiceProfile.setuRLLCPerfReq(urLLCPerfReq);

        ArrayList<String> nsstIDs=createArrayListWithRandomValuesIn(3, "nsstID");
        nst = new NST("nstId", "nstVersion", "nstProvider", nsstIDs, nstServiceProfile);
        nst.setNsdId("nsdID");
        nst.setNstName("nstName");
    }

    /*Given two objects of the same class expressed in classObjects,
    it compares all the fields (except those from fieldsToExcludeFromCompare) through all the available getters method of the same class.
    It returns true if all fields are equal, otherwise false.*/
    private boolean haveTwoObjsSameFields(Class classObjects, Object obj1, Object obj2, ArrayList<String> fieldsToExcludeFromCompare) {
        log.info("TEST: comparing two objects of class "+classObjects.getName());
        if (obj1.getClass() != obj2.getClass()) {
            log.error("TEST:  The two objects are instances of different classes, thus different");
            return false;
        }

        try {
            PropertyDescriptor[] pds= Introspector.getBeanInfo(classObjects).getPropertyDescriptors();
            for(PropertyDescriptor pd: pds) {
                String methodName=pd.getReadMethod().getName();
                String fieldName = methodName.substring(3, methodName.length());

                if(fieldsToExcludeFromCompare.contains(fieldName)==true) {
                    continue;
                }

                boolean areEqual=false;
                try {
                    Object objReturned1 = pd.getReadMethod().invoke(obj1);
                    Object objReturned2 =pd.getReadMethod().invoke(obj2);
                    if(objReturned1!=null && objReturned2!=null) {
                        areEqual=objReturned1.equals(objReturned2);
                        if(objReturned1 instanceof List<?> && ((List) objReturned1).get(0) instanceof String) {
                            String str1=String.valueOf(objReturned1);
                            String str2=String.valueOf(objReturned2);
                            areEqual=str1.equals(str2);

                        }
                    }
                    else if(objReturned1==null && objReturned2==null) {
                        log.info("TEST: Field with name "+fieldName+" null in both objects.");
                        areEqual=true;
                    }
                    else {
                        //log.info("One of two objects is null");
                        //log.info("{}",objReturned1);
                        //log.info("{}",objReturned2);
                    }
                    if(areEqual==false) {
                        log.info("TEST: field with name "+fieldName+" has different values in objects. ");
                        return false;
                    }
                    else{
                        log.info("TEST: field with name "+fieldName+" has same value in both objects. ");
                    }
                } catch (IllegalAccessException e) {

                    e.printStackTrace();
                    return false;
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return false;
                } catch (InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return false;
                }
            }
        } catch (IntrospectionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }


    private ArrayList<String> createArrayListWithRandomValuesIn(int sizeArrayList, String prefixElement) {
        ArrayList<String> arrayList = new ArrayList<String>();
        for(int i=0; i<sizeArrayList; i++) {
            arrayList.add(prefixElement+String.valueOf(i+1));
        }
        return arrayList;
    }


    private OnBoardNsTemplateRequest createOnBoardNsTemplateRequest() {
        OnBoardNsTemplateRequest request=new OnBoardNsTemplateRequest();
        request.setNst(nst);
        return request;
    }


    @Test
    public void testOnBoardingNSTemplate() {

        //1. On board a NST sample
        OnBoardNsTemplateRequest request=createOnBoardNsTemplateRequest();
        String nstID=null;
        try {
            nstID=nsTemplateCatalogueService.onBoardNsTemplate(request);
        } catch (MethodNotImplementedException e) {
            e.printStackTrace();
        } catch (MalformattedElementException e) {
            e.printStackTrace();
        } catch (AlreadyExistingEntityException e) {
            e.printStackTrace();
        } catch (FailedOperationException e) {
            e.printStackTrace();
        }
        assertNotNull(nstID);
        nst.setNstId(nstID);
        log.info("TEST: NST on boarded with ID "+nstID+".");

        //2. If it has been on boarded, then try to on board it again. This time should raise an exception because duplicate.
        boolean isDuplicate=false;
        request=createOnBoardNsTemplateRequest();
        try {
            nsTemplateCatalogueService.onBoardNsTemplate(request);
        } catch (MethodNotImplementedException e) {
            e.printStackTrace();
        } catch (MalformattedElementException e) {
            e.printStackTrace();
        } catch (AlreadyExistingEntityException e) {
            isDuplicate=true;
            log.info("TEST: AlreadyExistingEntityException raised as expected");
            //e.printStackTrace();
        } catch (FailedOperationException e) {
            e.printStackTrace();
        }

        assertTrue(isDuplicate);
        log.info("TEST: NST cannot be on boarded because duplicate. ID "+nstID+ " already present.");

        //3. Getting NST by ID, should give a list of exactly one element containing the just NST on boarded.
        try {
            Map<String,String> parameters=new HashMap<String,String> ();

            parameters.put("NST_ID", nstID);
            Filter filterById= new Filter(parameters);
            QueryNsTemplateResponse queryNsTemplateResp=nsTemplateCatalogueService.queryNsTemplate(new GeneralizedQueryRequest(filterById,null));
            List<NsTemplateInfo> nstInfos=queryNsTemplateResp.getNsTemplateInfos();

            assertEquals(nstInfos.size(),1);
            log.info("TEST: The list of NSTInfo contained in GET response, has exactly ONE element as expected");
            //4. Verify if the fields set are those expected. Excluded from check the ID because is automatically generated and the NstServiceProfile because is checked later
            NST actualNST = nstInfos.get(0).getNST();
            ArrayList<String> fieldsNSTnotToCheck=new ArrayList<String>();
            fieldsNSTnotToCheck.add("Id");
            fieldsNSTnotToCheck.add("NstServiceProfile");
            assertTrue(haveTwoObjsSameFields(NST.class, actualNST, nst, fieldsNSTnotToCheck));

            //5. Check the NstServiceProfile. The two fields below are excluded because are going to check later
            ArrayList<String> fieldsNSTserviceProfileNotToCheck=new ArrayList<String>();
            fieldsNSTserviceProfileNotToCheck.add("eMBBPerfReq");
            fieldsNSTserviceProfileNotToCheck.add("uRLLCPerfReq");

            assertTrue(haveTwoObjsSameFields(NstServiceProfile.class, actualNST.getNstServiceProfile(), nst.getNstServiceProfile(), fieldsNSTserviceProfileNotToCheck));
            assertTrue(haveTwoObjsSameFields(NstServiceProfile.class, actualNST.getNstServiceProfile(), nst.getNstServiceProfile(), fieldsNSTserviceProfileNotToCheck));

            for(int i=0; i<actualNST.getNstServiceProfile().geteMBBPerfReq().size(); i++){
                assertTrue(haveTwoObjsSameFields(EMBBPerfReq.class, actualNST.getNstServiceProfile().geteMBBPerfReq().get(i),nst.getNstServiceProfile().geteMBBPerfReq().get(i),new ArrayList<String>()));
            }
            for(int i=0; i<actualNST.getNstServiceProfile().geteMBBPerfReq().size(); i++){
                assertTrue(haveTwoObjsSameFields(URLLCPerfReq.class, actualNST.getNstServiceProfile().getuRLLCPerfReq().get(i),nst.getNstServiceProfile().getuRLLCPerfReq().get(i),new ArrayList<String>()));
            }


        } catch (MethodNotImplementedException e) {
            e.printStackTrace();
        } catch (MalformattedElementException e) {
            e.printStackTrace();
        } catch (NotExistingEntityException e) {
            e.printStackTrace();
        } catch (FailedOperationException e) {
            e.printStackTrace();
        }
        //6.Delete the added NST. Afterwards, the GET should return a response with empty list, i.e. its size must be equal to zero.
        try {
            nsTemplateCatalogueService.deleteNsTemplate(nstID);
            QueryNsTemplateResponse queryNsTemplateResp=nsTemplateCatalogueService.queryNsTemplate(new GeneralizedQueryRequest(new Filter(),null));
            List<NsTemplateInfo> nstInfos=queryNsTemplateResp.getNsTemplateInfos();
            assertEquals(nstInfos.size(),0);
            log.info("TEST: The list of NSTInfo contained in GET response, has zero element as expected");
        } catch (MethodNotImplementedException e) {

            e.printStackTrace();
        } catch (MalformattedElementException e) {
            e.printStackTrace();
        } catch (NotExistingEntityException e) {
            e.printStackTrace();
        } catch (FailedOperationException e) {
            e.printStackTrace();
        }
    }


}
