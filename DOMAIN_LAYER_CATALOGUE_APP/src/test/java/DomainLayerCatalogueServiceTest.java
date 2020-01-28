import it.nextworks.nfvmano.catalogue.SpringAppDomainLayerCatalogue;
import it.nextworks.nfvmano.catalogue.domainLayer.*;
import it.nextworks.nfvmano.catalogues.domainLayer.services.DomainCatalogueService;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.AlreadyExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.NotExistingEntityException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= SpringAppDomainLayerCatalogue.class)
public class DomainLayerCatalogueServiceTest {


    private static final Logger log = LoggerFactory.getLogger(DomainLayerCatalogueServiceTest.class);

    @Autowired
    private DomainCatalogueService domainCatalogueService;


    public DomainLayerCatalogueServiceTest() {

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
                log.info(pd.getDisplayName());
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
                        if(objReturned1 instanceof List<?> && ((List) objReturned1).size()>0 && ((List) objReturned1).size()>0 && ((List) objReturned1).get(0) instanceof String) {
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
                    e.printStackTrace();
                    return false;
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        return true;
    }


    private void onBoardDomainTest(Domain domain){
        try {
            Long domainId= domainCatalogueService.onBoardDomain(domain);
            assertTrue(domainId!=null);
            Domain domainActual = domainCatalogueService.getDomain(domain.getDomainId());
            ArrayList<String> toExclude= new ArrayList<String>();
            toExclude.add("DomainInterface");
            toExclude.add("OwnedLayers");
            toExclude.add("DomainsAgreement");
            assertTrue(haveTwoObjsSameFields(Domain.class,domain,domainActual,toExclude));
            assertTrue(haveTwoObjsSameFields(DomainInterface.class,domain.getDomainInterface(),domainActual.getDomainInterface(),new ArrayList<>()));
            assertTrue(domain.getOwnedLayers().size()==domainActual.getOwnedLayers().size());
            assertTrue(domain.getDomainsAgreement().size()==domainActual.getDomainsAgreement().size());

        } catch (MalformattedElementException e) {
            e.printStackTrace();
        } catch (AlreadyExistingEntityException e) {
            e.printStackTrace();
        } catch (NotExistingEntityException e) {
            e.printStackTrace();
        }
    }
    //A domain called "A" owns a vertical domain layer. A domain called "B" owns an NSP and NFVO domains layer. A relationship between domain A and the domain layers of B is tested.
    @Test
    public void testDomainCatalogueService() {
        //Domain A objects
        final String DOMAIN_A_ID="DomainAid";
        DomainInterface domainAinterface = new DomainInterface("url.url.rul", 8080, true, InterfaceType.HTTP);
        VerticalDomainLayer verticalDomainLayer = new VerticalDomainLayer("verticalDomainLayerID",DspType.VERTICAL_EXPERIMENT);
        ArrayList<DomainLayer> listOwnedLayerDomainA = new ArrayList<DomainLayer>();
        listOwnedLayerDomainA.add(verticalDomainLayer);
        Domain domainA = new Domain(DOMAIN_A_ID,"domainNameA","domainDescriptionA","domainOwnerA","Domain adminA",listOwnedLayerDomainA,new HashSet<>(),domainAinterface);

        //Domain B objects
        final String DOMAIN_B_ID="DomainBid";
        NspDomainLayer nspDomainLayer = new NspDomainLayer("nspDomainLayerID",NspNbiType.NEUTRAL_HOSTING);
        NfvoDomainLayer nfvoDomainLayer = new NfvoDomainLayer("nfvoDomainLayerId",ManoNbiType.NMRO_DRIVER);
        ArrayList<DomainLayer> listOwnedLayerDomainB = new ArrayList<DomainLayer>();
        listOwnedLayerDomainB.add(nspDomainLayer);
        listOwnedLayerDomainB.add(nfvoDomainLayer);
        DomainInterface domainBinterface = new DomainInterface("url2.url2.rul2", 8081, true, InterfaceType.HTTP);
        Domain domainB = new Domain(DOMAIN_B_ID,"domainNameB","domainDescriptionB","domainOwnerB","Domain adminB",listOwnedLayerDomainB,new HashSet<>(),domainBinterface);


        //Test on board Domain B and A
        log.info("Testing on boarding Domain B");
        onBoardDomainTest(domainB);

        //An agreement between domain A and two domains layer owned by domain B is performed. Eventually, the domain A is on boarded with such info.
        ArrayList<String> idDomainLayers = new ArrayList<String>();
        idDomainLayers.add(domainB.getOwnedLayers().get(0).getDomainLayerId());
        idDomainLayers.add(domainB.getOwnedLayers().get(1).getDomainLayerId());

        DomainAgreement domainAgreement = new DomainAgreement(domainB.getDomainId(),idDomainLayers);
        HashSet<DomainAgreement> domainAgreements = new HashSet<DomainAgreement>();
        domainAgreements.add(domainAgreement);
        domainA.setDomainsAgreement(domainAgreements);
        log.info("Testing on boarding domain A");
        onBoardDomainTest(domainA);
        try {
            domainCatalogueService.deleteDomain(domainA.getDomainId());
        } catch (NotExistingEntityException e) {
            e.printStackTrace();
        }
        boolean notFound=false;
        try {
            domainCatalogueService.getDomain(domainA.getDomainId());

        } catch (NotExistingEntityException e) {
            notFound=true;
            log.info("Element not found as expected");
        }
        assertTrue(notFound);
        assertTrue(domainCatalogueService.getAllDomains().size()==1);
    }
}