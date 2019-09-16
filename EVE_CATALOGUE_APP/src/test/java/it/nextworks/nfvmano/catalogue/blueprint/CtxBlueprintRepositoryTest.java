package it.nextworks.nfvmano.catalogue.blueprint;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.nextworks.nfvmano.catalogue.blueprint.elements.CtxBlueprint;
import it.nextworks.nfvmano.catalogue.blueprint.elements.CtxBlueprintInfo;
import it.nextworks.nfvmano.catalogue.blueprint.elements.VsComponent;
import it.nextworks.nfvmano.catalogue.blueprint.messages.OnboardCtxBlueprintRequest;
import it.nextworks.nfvmano.catalogue.blueprint.repo.CtxBlueprintInfoRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.CtxBlueprintRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.TranslationRuleRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.VsComponentRepository;
import it.nextworks.nfvmano.libs.common.exceptions.AlreadyExistingEntityException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;


@RunWith(SpringRunner.class)
@DataJpaTest
@EnableJpaRepositories(basePackages = "it.nextworks.nfvmano")
@EntityScan(basePackages = "it.nextworks.nfvmano")

public class CtxBlueprintRepositoryTest {


    @Autowired
    CtxBlueprintRepository ctxBlueprintRepository;

    @Autowired
    CtxBlueprintInfoRepository ctxBlueprintInfoRepository;

    @Autowired
    VsComponentRepository vsComponentRepository;

    @Autowired
    TranslationRuleRepository translationRuleRepository;

    @Test
    public void ctxBlueprintOnboardTest(){

        ObjectMapper objectMapper = new ObjectMapper();
        InputStream nsdStream = null;
        try {
            nsdStream = this.getClass().getClassLoader()
                    .getResource("ctxonboardrequest.json")
                    .openStream();
            OnboardCtxBlueprintRequest req = objectMapper.readValue(nsdStream, new TypeReference<OnboardCtxBlueprintRequest>() {});
            String id = storeCtxBlueprint(req.getCtxBlueprint());
            Optional<CtxBlueprint> retrieved = ctxBlueprintRepository.findByBlueprintId(id);
            assertTrue("Couldnot retrieve CtxBlueprint from catalogue",retrieved.isPresent());
            //the following is wrong since atomic components needs to be uploaded within the blueprint to keep trace of their relationship with the parent
            //assertFalse("The retrieved list of Ctx Atomic Components is empty",retrieved.get().getAtomicComponents().isEmpty());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AlreadyExistingEntityException e) {
            e.printStackTrace();
        }







    }


    private String storeCtxBlueprint(CtxBlueprint ctxBlueprint) throws AlreadyExistingEntityException {
        //log.debug("Onboarding CTX blueprint with name " + ctxBlueprint.getName() + " and version " + ctxBlueprint.getVersion());
        if ( (ctxBlueprintInfoRepository.findByNameAndCtxBlueprintVersion(ctxBlueprint.getName(), ctxBlueprint.getVersion()).isPresent()) ||
                (ctxBlueprintRepository.findByNameAndVersion(ctxBlueprint.getName(), ctxBlueprint.getVersion()).isPresent()) ) {
            //log.error("CTX Blueprint with name " + ctxBlueprint.getName() + " and version " + ctxBlueprint.getVersion() + " already present in DB.");
            throw new AlreadyExistingEntityException("VS Blueprint with name " + ctxBlueprint.getName() + " and version " + ctxBlueprint.getVersion() + " already present in DB.");
        }


        //ctxBlueprintRepository.saveAndFlush(target);


        //ctxBlueprintRepository.saveAndFlush(target);
        //log.debug("Added VS Blueprint with ID " + ctxbIdString);

        List<VsComponent> atomicComponents = ctxBlueprint.getAtomicComponents();
        List<VsComponent> targetComponents = new ArrayList<>();
        if (atomicComponents != null) {
            for (VsComponent c : atomicComponents) {
                //first element is null because it should be a VSB
                VsComponent targetComponent = new VsComponent(null, c.getComponentId(), c.getServersNumber(), c.getImagesUrls(), c.getEndPointsIds(), c.getLifecycleOperations());
                vsComponentRepository.saveAndFlush(targetComponent);
                targetComponents.add(targetComponent);
            }
            //log.debug("Added atomic components in CTX blueprint " + ctxbIdString);
        }

        CtxBlueprint target = new CtxBlueprint(
                null,
                ctxBlueprint.getVersion(),
                ctxBlueprint.getName(),
                ctxBlueprint.getDescription(),
                ctxBlueprint.getParameters(),
                ctxBlueprint.getEndPoints(),
                ctxBlueprint.getConfigurableParameters(),
                ctxBlueprint.getCompatibleSites(),
                ctxBlueprint.getApplicationMetrics());

        ctxBlueprintRepository.saveAndFlush(target);
        Long ctxbId = target.getId();
        
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<< ID : " + ctxbId + " <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        
        String ctxbIdString = String.valueOf(ctxbId);
        target.setBlueprintId(ctxbIdString);
        //log.debug("Added CTX Blueprint Info with ID " + ctxbIdString);

        CtxBlueprintInfo ctxBlueprintInfo = new CtxBlueprintInfo(ctxbIdString, ctxBlueprint.getVersion(), ctxBlueprint.getName());
        ctxBlueprintInfoRepository.saveAndFlush(ctxBlueprintInfo);

        return ctxbIdString;
    }

}
