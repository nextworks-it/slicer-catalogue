package it.nextworks.nfvmano.catalogue.blueprint;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.nextworks.nfvmano.catalogue.blueprint.elements.*;
import it.nextworks.nfvmano.catalogue.blueprint.messages.OnboardExpBlueprintRequest;
import it.nextworks.nfvmano.catalogue.blueprint.repo.*;
import it.nextworks.nfvmano.catalogue.blueprint.services.CtxBlueprintCatalogueService;
import it.nextworks.nfvmano.catalogue.blueprint.services.ExpBlueprintCatalogueService;
import it.nextworks.nfvmano.catalogue.blueprint.services.VsBlueprintCatalogueService;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.AlreadyExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.FailedOperationException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MethodNotImplementedException;
import it.nextworks.nfvmano.nfvodriver.NfvoCatalogueService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = {
        VsBlueprintCatalogueService.class,
        ExpBlueprintCatalogueService.class
})
@SpringBootTest

public class ExpBlueprintCatalogueTest {

    @InjectMocks
    ExpBlueprintCatalogueService expBlueprintCatalogueService;

    @Spy
    @InjectMocks
    VsBlueprintCatalogueService vsBlueprintCatalogueService;


    @Spy
    @InjectMocks
    CtxBlueprintCatalogueService ctxBlueprintCatalogueService;

    @Mock
    ExpBlueprintInfoRepository expBlueprintInfoRepository;

    @Mock
    ExpBlueprintRepository expBlueprintCatalogueRepository;

    @Mock
    VsComponentRepository vsComponentRepository;

    @Mock
    VsBlueprintRepository vsBlueprintRepository;

    @Mock
    CtxBlueprintRepository ctxBlueprintRepository;

    @Mock
    CtxBlueprintInfoRepository ctxBlueprintInfoRepository;

    @Mock
    VsBlueprintInfoRepository vsBlueprintInfoRepository;

    @Mock
    KeyPerformanceIndicatorRepository keyPerformanceIndicatorRepository;

    @Mock
    VsbLinkRepository vsbLinkRepository;

    @Mock
    TranslationRuleRepository translationRuleRepository;

    @Mock
    NfvoCatalogueService nfvoCatalogueService;

    @Before
    public void init() {

        MockitoAnnotations.initMocks(this);
    }

    
    @Test
    public void expBlueprintOnboardTest(){
        when(expBlueprintInfoRepository.findByNameAndExpBlueprintVersion(anyString(), anyString())).thenReturn(Optional.empty());

        when(vsBlueprintInfoRepository.findByNameAndVsBlueprintVersion(anyString(), anyString())).thenReturn(Optional.empty());

/*

        VsBlueprint testVsBlueprint = new VsBlueprint("1",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);

        when(vsBlueprintRepository.findByBlueprintId(anyString()))
                .thenReturn(Optional.of(testVsBlueprint));
        when(vsBlueprintRepository.findByNameAndVersion(anyString(), anyString()))
                .thenReturn(Optional.of(testVsBlueprint));
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            ((VsBlueprint)args[0]).setId(1L);
            return null; // void method in a block-style lambda, so return null
        }).when(vsBlueprintRepository).saveAndFlush(any(VsBlueprint.class));
        when(ctxBlueprintInfoRepository.findByNameAndCtxBlueprintVersion(anyString(), anyString())).thenReturn(Optional.empty());
        when(ctxBlueprintInfoRepository.findByCtxBlueprintId(anyString())).thenReturn(Optional.empty());
        when(ctxBlueprintRepository.findByNameAndVersion(anyString(), anyString())).thenReturn(Optional.empty());
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            ((CtxBlueprint)args[0]).setId(1L);
            return null; // void method in a block-style lambda, so return null
        }).when(ctxBlueprintRepository).saveAndFlush(any(CtxBlueprint.class));
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream expbStream = null;
        try {
            expbStream = this.getClass().getClassLoader()
                    .getResource("expbonboardrequest.json")
                    .openStream();
            OnboardExpBlueprintRequest req = objectMapper.readValue(expbStream, new TypeReference<OnboardExpBlueprintRequest>() {});
            ExpBlueprint expBlueprint = req.getExpBlueprint();
            ExpBlueprintInfo expBlueprintInfo  = new ExpBlueprintInfo(expBlueprint.getExpBlueprintId(),
                    expBlueprint.getVersion(),
                    expBlueprint.getName());
            when(expBlueprintInfoRepository.findByExpBlueprintId(anyString()))
                    .thenReturn(Optional.empty())
                    .thenReturn(Optional.of(expBlueprintInfo));

            when(expBlueprintCatalogueRepository.findByExpBlueprintId(anyString()))
                    .thenReturn(Optional.of(expBlueprint));

            expBlueprintCatalogueService.onboardExpBlueprint(req);



        } catch (IOException e) {
            fail(e.getMessage());
        } catch (AlreadyExistingEntityException e) {
            fail(e.getMessage());
        } catch (MethodNotImplementedException e) {
            fail(e.getMessage());
        } catch (MalformattedElementException e) {
            fail(e.getMessage());
        } catch (FailedOperationException e) {
            fail(e.getMessage());
        }

*/
    }
    




}
