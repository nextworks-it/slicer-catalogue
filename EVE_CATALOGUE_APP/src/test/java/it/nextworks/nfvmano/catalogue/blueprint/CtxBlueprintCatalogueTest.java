package it.nextworks.nfvmano.catalogue.blueprint;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.nextworks.nfvmano.catalogue.blueprint.elements.CtxBlueprint;
import it.nextworks.nfvmano.catalogue.blueprint.elements.CtxBlueprintInfo;
import it.nextworks.nfvmano.catalogue.blueprint.messages.OnboardCtxBlueprintRequest;
import it.nextworks.nfvmano.catalogue.blueprint.repo.CtxBlueprintInfoRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.CtxBlueprintRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.TranslationRuleRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.VsComponentRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.VsbForwardingPathHopRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.VsbLinkRepository;
import it.nextworks.nfvmano.catalogue.blueprint.services.CtxBlueprintCatalogueService;
import it.nextworks.nfvmano.libs.common.exceptions.AlreadyExistingEntityException;
import it.nextworks.nfvmano.libs.common.exceptions.FailedOperationException;
import it.nextworks.nfvmano.libs.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.common.exceptions.MethodNotImplementedException;
import it.nextworks.nfvmano.nfvodriver.NfvoCatalogueService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static junit.framework.TestCase.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@DataJpaTest
@EnableJpaRepositories(basePackages = "it.nextworks.nfvmano")
@EntityScan(basePackages = "it.nextworks.nfvmano")
//@ContextConfiguration(classes = CtxBlueprintCatalogueService.class)
public class CtxBlueprintCatalogueTest {

    //@Spy
    //@InjectMocks
	//@Autowired
    //CtxBlueprintCatalogueService ctxBlueprintCatalogueService;
/*

    //@Mock
    //NfvoCatalogueService nfvoCatalogueService;

    @Mock
	//@Autowired
    CtxBlueprintInfoRepository ctxBlueprintInfoRepository;

    @Mock
	//@Autowired
    CtxBlueprintRepository ctxBlueprintRepository;

	
    @Mock
    VsComponentRepository vsComponentRepository;

    @Mock
    TranslationRuleRepository translationRuleRepository;

    //@Mock
    VsbLinkRepository vsbLinkRepository;
    
    //@Mock
    VsbForwardingPathHopRepository vsbForwardingPathHopRepository;
    */

    @Before
    public void init() {

        //MockitoAnnotations.initMocks(this);
    }



    @Test
    public void ctxBlueprintOnboardTest(){
    	
    	/*
    	
        //when(ctxBlueprintInfoRepository.findByNameAndCtxBlueprintVersion(anyString(), anyString())).thenReturn(Optional.empty());
        //when(ctxBlueprintInfoRepository.findByCtxBlueprintId(anyString())).thenReturn(Optional.empty());
        //when(ctxBlueprintRepository.findByNameAndVersion(anyString(), anyString())).thenReturn(Optional.empty());
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            InputStream nsdStream = this.getClass().getClassLoader()
                    .getResource("ctxonboardrequest.json")
                    .openStream();


            OnboardCtxBlueprintRequest req = objectMapper.readValue(nsdStream, new TypeReference<OnboardCtxBlueprintRequest>() {});
            CtxBlueprint ctxBlueprint = req.getCtxBlueprint();
            CtxBlueprintInfo ctxBlueprintInfo  = new CtxBlueprintInfo(ctxBlueprint.getBlueprintId(),
                    ctxBlueprint.getVersion(),
                    ctxBlueprint.getName());
            //when(ctxBlueprintInfoRepository.findByCtxBlueprintId(anyString()))
            //        .thenReturn(Optional.of(ctxBlueprintInfo));

            ctxBlueprintCatalogueService.onboardCtxBlueprint(req);

            //CtxBlueprint target = ctxBlueprintRepository.findByNameAndVersion(source.getName(), source.getVersion()).get();

        } catch (IOException e) {
            fail(e.getMessage());
        } catch (MalformattedElementException e) {
            fail(e.getMessage());
        } catch (FailedOperationException e) {
            fail(e.getMessage());
        } catch (AlreadyExistingEntityException e) {
            fail(e.getMessage());
        } catch (MethodNotImplementedException e) {
            fail(e.getMessage());
        }

		*/
    }
}
