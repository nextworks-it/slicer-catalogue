import it.nextworks.nfvmano.catalogue.SpringAppDomainLayerCatalogue;
import it.nextworks.nfvmano.catalogue.template.elements.*;
import it.nextworks.nfvmano.catalogues.template.services.DomainLayerCatalogueService;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= SpringAppDomainLayerCatalogue.class)
public class DomainLayerCatalogueServiceTest {


    private static final Logger log = LoggerFactory.getLogger(DomainLayerCatalogueServiceTest.class);

    @Autowired
    private DomainLayerCatalogueService domainLayerCatalogueService;


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
                        if(objReturned1 instanceof List<?> && ((List) objReturned1).size()>0 && ((List) objReturned1).get(0) instanceof String) {
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


    private Long onboardCustomNspDomainLayer(String customName){
        NspDomainLayer nspDomainLayer = new NspDomainLayer(
                "nspDomainLayer (no nfvo)"+customName,
                "nspDomainLayer (no nfvo) desc",
                "nspDomainLayer (no nfvo) owner",
                "nspDomainLayer (no nfvo) admin");
        nspDomainLayer.setCSP(false);
        try {
            Long verticalDomainLayerId = domainLayerCatalogueService.onBoardDomainLayer(nspDomainLayer);
            assertTrue(verticalDomainLayerId!=null);
            return verticalDomainLayerId;
        } catch (MalformattedElementException e) {
            e.printStackTrace();
        } catch (AlreadyExistingEntityException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Test
    public void testOnBoardingDomainLayer() throws InterruptedException {
        //0. Creating some custom objects
        ArrayList<InterfaceType> interfaceTypeArrayList = new  ArrayList<InterfaceType>();
        interfaceTypeArrayList.add(InterfaceType.HTTP);
        interfaceTypeArrayList.add(InterfaceType.RABBIT);

        ArrayList<Long> domainLayerIdsArrayList = new  ArrayList<Long>();
        domainLayerIdsArrayList.add(onboardCustomNspDomainLayer("name nsp_1"));
        domainLayerIdsArrayList.add(onboardCustomNspDomainLayer("name nsp_2"));

        VerticalDomainLayer verticalDomainLayer = new VerticalDomainLayer("VerticalDomainLayer name",
                "VerticalDomainLayer description",
                "VerticalDomainLayer owner",
                "VerticalDomainLayer admin",
                null, null);

        verticalDomainLayer.setInterfaceTypeList(interfaceTypeArrayList);
        verticalDomainLayer.setDomainLayersIdAssociated(domainLayerIdsArrayList);

        NspDomainLayer nspDomainLayer = new NspDomainLayer(
                "nspDomainLayer (no nfvo) name",
                "nspDomainLayer (no nfvo) desc",
                "nspDomainLayer (no nfvo) owner",
                "nspDomainLayer (no nfvo) admin");
        nspDomainLayer.setCSP(true);
        nspDomainLayer.setInterfaceTypeList(interfaceTypeArrayList);
        nspDomainLayer.setDomainLayersIdAssociated(domainLayerIdsArrayList);

        NfvoDomainLayer nfvoDomainLayer = new NfvoDomainLayer(
                "nfvoDomainLayer name",
                "nfvoDomainLayer description",
                "nfvoDomainLayer owner",
                "nfvoDomainLayer admin",
                true);
        nfvoDomainLayer.setInterfaceTypeList(interfaceTypeArrayList);
        nfvoDomainLayer.setDomainLayersIdAssociated(domainLayerIdsArrayList);

        ArrayList<String> toExclude= new ArrayList<String>();
        toExclude.add("InterfaceTypeList");
        toExclude.add("DomainLayersIdAssociated");
        toExclude.add("DomainLayersAssociated");

        //1. On boarding of (1.1) Vertical Domain layer, (1.2) NSP domain layer and (1.3) NFVO domain layer is tested

        try {
            //1.1 Testing the on boarding of vertical domain layer
            Long verticalDomainLayerId = domainLayerCatalogueService.onBoardDomainLayer(verticalDomainLayer);
            DomainLayer verticalDomainLayerGet = domainLayerCatalogueService.getDomainLayer(verticalDomainLayerId);

            assertTrue(haveTwoObjsSameFields(VerticalDomainLayer.class, verticalDomainLayer, verticalDomainLayerGet, toExclude));


            log.info("Checking the interfaces within the vertical domain layer");
            assertTrue(verticalDomainLayer.getInterfaceTypeList().size()==verticalDomainLayerGet.getInterfaceTypeList().size());
            for(int i=0; i<verticalDomainLayer.getInterfaceTypeList().size(); i++){
                assertTrue(haveTwoObjsSameFields(InterfaceType.class,verticalDomainLayer.getInterfaceTypeList().get(i),verticalDomainLayerGet.getInterfaceTypeList().get(i),toExclude));
            }

            log.info("Checking the domain layer IDs associated within the vertical domain layer");
            for(int i=0; i<verticalDomainLayer.getDomainLayersIdAssociated().size(); i++){
                assertTrue(verticalDomainLayer.getDomainLayersIdAssociated().get(i)==verticalDomainLayerGet.getDomainLayersIdAssociated().get(i));
            }


            //1.2 Testing the onboarding of nsp domain layer
            Long nspDomainLayerId = domainLayerCatalogueService.onBoardDomainLayer(nspDomainLayer);
            DomainLayer nspDomainLayerGet = domainLayerCatalogueService.getDomainLayer(nspDomainLayerId);
            assertTrue(haveTwoObjsSameFields(NspDomainLayer.class, nspDomainLayer, nspDomainLayerGet, toExclude));

            log.info("Checking the interfaces within the nsp domain layer");
            assertTrue(nspDomainLayer.getInterfaceTypeList().size()==nspDomainLayerGet.getInterfaceTypeList().size());
            for(int i=0; i<nspDomainLayer.getInterfaceTypeList().size(); i++){
                assertTrue(haveTwoObjsSameFields(InterfaceType.class,nspDomainLayer.getInterfaceTypeList().get(i),nspDomainLayerGet.getInterfaceTypeList().get(i),toExclude));
            }

            log.info("Checking the domain layer IDs associated within the nsp domain layer");
            for(int i=0; i<verticalDomainLayer.getDomainLayersIdAssociated().size(); i++){
                assertTrue(verticalDomainLayer.getDomainLayersIdAssociated().get(i)==verticalDomainLayerGet.getDomainLayersIdAssociated().get(i));
            }


            //1.3 Testing the on boarding of nfvo domain layer
            Long nfvoDomainLayerId =domainLayerCatalogueService.onBoardDomainLayer(nfvoDomainLayer);
            DomainLayer nfvoDomainLayerGet = domainLayerCatalogueService.getDomainLayer(nfvoDomainLayerId);

            assertTrue(haveTwoObjsSameFields(NfvoDomainLayer.class, nfvoDomainLayer, nfvoDomainLayerGet, toExclude));

            List <DomainLayer> domainLayerList = domainLayerCatalogueService.getAllDomainLayers();
            assertTrue(domainLayerList.size()>=3);

        } catch (MalformattedElementException e) {
            e.printStackTrace();
        } catch (AlreadyExistingEntityException e) {
            e.printStackTrace();
        } catch (NotExistingEntityException e) {
            e.printStackTrace();
        }

        //2. Going to test on boarding of duplicate
        boolean isDuplicate=false;
        try {
            Long verticalDomainLayerId = domainLayerCatalogueService.onBoardDomainLayer(verticalDomainLayer);

        } catch (MalformattedElementException e) {
            e.printStackTrace();
        } catch (AlreadyExistingEntityException e) {
            isDuplicate=true;
        }
        assertTrue(isDuplicate);
        log.info("Duplicate error raised as expected");

        //3 Test case: the vertical domain layer is federated
        log.info("Going to on board vertical domain layer with vertical domain layer federated");

        ArrayList<String> driversList = new ArrayList<String>();
        driversList.add("driver1");
        driversList.add("driver2");
        driversList.add("driver3");


        VerticalDomainLayer verticalDomainLayerWithFed = new VerticalDomainLayer("VerticalDomainLayer with fed name",
                "VerticalDomainLayer with fed description",
                "VerticalDomainLayer with fed owner",
                "VerticalDomainLayer with fed admin",
                new VerticalDomainLayer(
                        "VerticalDomainLayer Federated name",
                        "VerticalDomainLayer Federated description",
                        "VerticalDomainLayer Federated owner",
                        "VerticalDomainLayer Federated admin",
                        null,null),
                new NspDomainLayer("Nsp embedded into Vertical name",
                        "Nsp embedded into Vertical description",
                        "Nsp embedded into Vertical owner",
                        "Nsp embedded into Vertical admin",
                        true,
                        driversList, null));

        try {
            Long verticalDomainLayerId = domainLayerCatalogueService.onBoardDomainLayer(verticalDomainLayerWithFed);
            VerticalDomainLayer verticalDomainLayerWithFedGet = (VerticalDomainLayer) domainLayerCatalogueService.getDomainLayer(verticalDomainLayerId);

            assertTrue(haveTwoObjsSameFields(VerticalDomainLayer.class,
                            verticalDomainLayerWithFed.getFederatedBy(),
                            verticalDomainLayerWithFedGet.getFederatedBy(),
                            toExclude));

            assertTrue(haveTwoObjsSameFields(NspDomainLayer.class,
                    verticalDomainLayerWithFed.getNspDomainLayer(),
                    verticalDomainLayerWithFedGet.getNspDomainLayer(),
                    toExclude));


        } catch (MalformattedElementException e) {
            e.printStackTrace();
        } catch (AlreadyExistingEntityException e) {
            e.printStackTrace();
        } catch (NotExistingEntityException e) {
            e.printStackTrace();

        }

        //4 the nsp contains nfvo as well
        log.info("Going ot on board nsp domain layer with nfvo domain layer");
        DomainLayer nspDomainLater = new NspDomainLayer(
                "nspDomainLayer (with nfvo emb) name",
                "nspDomainLayer (with nfvo emb) desc",
                "nspDomainLayer (with nfvo emb) owner",
                "nspDomainLayer (with nfvo emb) admin",
                true,
                driversList,
                    new NfvoDomainLayer(
                    "nfvoDomainLayer (embedded) name",
                    "nfvoDomainLayer (embedded) description",
                    "nfvoDomainLayer (embedded) owner",
                    "nfvoDomainLayer (embedded) admin",
                    true));
        boolean notFound=false;
        try {
            Long nspDomainLayerId = domainLayerCatalogueService.onBoardDomainLayer(nspDomainLater);
            domainLayerCatalogueService.deleteDomainLayer(nspDomainLayerId);
            DomainLayer verticalDomainLayerGet = domainLayerCatalogueService.getDomainLayer(nspDomainLayerId);
        } catch (MalformattedElementException e) {
            e.printStackTrace();
        } catch (AlreadyExistingEntityException e) {
            e.printStackTrace();
        } catch (NotExistingEntityException e) {
            notFound=true;
        }
        assertTrue(notFound);
        log.info("The domain layer has been successfully deleted from the DB");


    }
}
