package it.nextworks.nfvmano.catalogue.test;

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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.nextworks.nfvmano.catalogue.template.elements.NsTemplateInfo;
import it.nextworks.nfvmano.catalogue.template.messages.OnBoardNsTemplateRequest;
import it.nextworks.nfvmano.catalogue.template.messages.QueryNsTemplateResponse;
import it.nextworks.nfvmano.catalogues.template.SpringAppTest;
import it.nextworks.nfvmano.catalogues.template.services.NsTemplateCatalogueService;
import it.nextworks.nfvmano.libs.ifa.common.elements.Filter;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.AlreadyExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.FailedOperationException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MethodNotImplementedException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.NotExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.messages.GeneralizedQueryRequest;
import it.nextworks.nfvmano.libs.ifa.templates.NST;
import it.nextworks.nfvmano.libs.ifa.templates.NstServiceProfile;
import it.nextworks.nfvmano.libs.ifa.templates.SliceType;
import it.nextworks.nfvmano.libs.ifa.templates.UEMobilityLevel;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=SpringAppTest.class)
public class NsTemplateCatalogueServiceTest {

	
	private static final Logger log = LoggerFactory.getLogger(NsTemplateCatalogueServiceTest.class);
	
    @Autowired
    private NsTemplateCatalogueService nsTemplateCatalogueService;
    
    private NST nst;

	
    public NsTemplateCatalogueServiceTest() {
    	/*Creating sample NstServiceProfile and sample NST*/
    	nsTemplateCatalogueService = new NsTemplateCatalogueService();
    	
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
		
		ArrayList<String> nsstIDs=createArrayListWithRandomValuesIn(3, "nsstID");
		nst = new NST("nstId", "nstVersion", "nstProvider", nsstIDs, nstServiceProfile);
		nst.setNsdId("nsdID");
		nst.setNstName("nstName");
    }
    
    /*Given two objects of the same class expressed in classObjects, 
    it compares all the fields (except those from fieldsToExcludeFromCompare) through all the available getters method of the same class.
    It returns true if all fields are equal, otherwise false.*/
    private boolean haveTwoObjsSameFields(Class classObjects, Object obj1, Object obj2, ArrayList<String> fieldsToExcludeFromCompare) {
    	if (obj1.getClass() != obj2.getClass()) {
    		log.error("The two objects are instances of different classes");
    		return false;
    	}
    	
    	try {
			PropertyDescriptor[] pds= Introspector.getBeanInfo(classObjects).getPropertyDescriptors();
			for(PropertyDescriptor pd: pds) {
				String methodName=pd.getReadMethod().getName();
				String fieldName = methodName.substring(3, methodName.length());
				
				if(fieldsToExcludeFromCompare.contains(fieldName)==true) {
					//log.info(fieldName+" going to NOT be checked");
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
						//log.info("Both object are null");
						areEqual=true;
					}
					else {
						//log.info("One of two objects is null");
						//log.info("{}",objReturned1);
						//log.info("{}",objReturned2);
					}
				if(areEqual==false) {
					return false;
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
		
		//1. Onboard a NST sample
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
		log.info("TEST: NST onboarded with ID "+nstID+".");
		
		//2. If it has been onboarded, then try to onboard it again. This time should raise an exception because duplicate.
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
		log.info("TEST: NST cannot be onboarded because duplicate. ID "+nstID+ " already present.");
		
		//3. Getting NST by ID, should give a list of one element containing the just NST onboarded, i.e exactly one element
		try {
			Map<String,String> parameters=new HashMap<String,String> ();
			
			parameters.put("NST_ID", nstID);
			Filter filterById= new Filter(parameters);
			QueryNsTemplateResponse queryNsTemplateResp=nsTemplateCatalogueService.queryNsTemplate(new GeneralizedQueryRequest(filterById,null));
			List<NsTemplateInfo> nstInfos=queryNsTemplateResp.getNsTemplateInfos();
			
			assertEquals(nstInfos.size(),1);
			
		//4. Verify if the fields set are those expected. Excluded from check the id because is automatically generated and the NstServiceProfile because is checked later
			NST actualNST = nstInfos.get(0).getNST();
			ArrayList<String> fieldsNSTnotToCheck=new ArrayList<String>();
			fieldsNSTnotToCheck.add("Id");
			fieldsNSTnotToCheck.add("NstServiceProfile");
			assertTrue(haveTwoObjsSameFields(NST.class, actualNST, nst, fieldsNSTnotToCheck));
			
		//5. Check the NstServiceProfile get excluding the eMBBPerfReq and uRLLCPerfReq lists because not set at all in the NstServiceProfile sample.
			ArrayList<String> fieldsNSTserviceProfileNotToCheck=new ArrayList<String>();
			fieldsNSTserviceProfileNotToCheck.add("eMBBPerfReq");
			fieldsNSTserviceProfileNotToCheck.add("uRLLCPerfReq");
			assertTrue(haveTwoObjsSameFields(NstServiceProfile.class, actualNST.getNstServiceProfile(), nst.getNstServiceProfile(), fieldsNSTserviceProfileNotToCheck));
		} catch (MethodNotImplementedException e) {
			e.printStackTrace();
		} catch (MalformattedElementException e) {
			e.printStackTrace();
		} catch (NotExistingEntityException e) {
			e.printStackTrace();
		} catch (FailedOperationException e) {
			e.printStackTrace();
		}
		//6.Delete the added NST. Afterwards, the GET should return an empty list, i.e. its size must be euqal to zero.
		try {
			
			nsTemplateCatalogueService.deleteNsTemplate(nstID);
			QueryNsTemplateResponse queryNsTemplateResp=nsTemplateCatalogueService.queryNsTemplate(new GeneralizedQueryRequest(new Filter(),null));
			List<NsTemplateInfo> nstInfos=queryNsTemplateResp.getNsTemplateInfos();
			assertEquals(nstInfos.size(),0);
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
