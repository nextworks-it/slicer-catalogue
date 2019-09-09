package it.nextworks.nfvmano.catalogue.blueprint;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.nextworks.nfvmano.catalogue.blueprint.elements.ExpBlueprint;
import it.nextworks.nfvmano.catalogue.blueprint.elements.ExpBlueprintInfo;
import it.nextworks.nfvmano.catalogue.blueprint.messages.OnboardExpBlueprintRequest;
import it.nextworks.nfvmano.catalogue.blueprint.repo.ExpBlueprintInfoRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.ExpBlueprintRepository;
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
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;


@RunWith(SpringRunner.class)
@DataJpaTest
@EnableJpaRepositories(basePackages = "it.nextworks.nfvmano")
@EntityScan(basePackages = "it.nextworks.nfvmano")

public class ExpBlueprintRepositoryTest {


    @Autowired
    ExpBlueprintRepository expBlueprintRepository;

    @Autowired
    ExpBlueprintInfoRepository expBlueprintInfoRepository;

    @Autowired
    VsComponentRepository vsComponentRepository;



    @Test
    public void expBlueprintOnboardTest(){
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream expbStream = null;
        try {
            expbStream = this.getClass().getClassLoader()
                    .getResource("expbonboardrequest.json")
                    .openStream();
            OnboardExpBlueprintRequest req = objectMapper.readValue(expbStream, new TypeReference<OnboardExpBlueprintRequest>() {});
            String id = storeExpBlueprint(req.getExpBlueprint());
            Optional<ExpBlueprint> retrieved = expBlueprintRepository.findByExpBlueprintId(id);
            assertTrue("Couldnot retrieve ExpBlueprint from catalogue",retrieved.isPresent());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (AlreadyExistingEntityException e) {
            e.printStackTrace();
        }







    }


    private String storeExpBlueprint(ExpBlueprint expBlueprint) throws AlreadyExistingEntityException {
        //log.debug("Onboarding EXP blueprint with name " + expBlueprint.getName() + " and version " + expBlueprint.getVersion());
        if ( (expBlueprintInfoRepository.findByNameAndExpBlueprintVersion(expBlueprint.getName(), expBlueprint.getVersion()).isPresent())) {
            //log.error("EXP Blueprint with name " + expBlueprint.getName() + " and version " + expBlueprint.getVersion() + " already present in DB.");
            throw new AlreadyExistingEntityException("EXP Blueprint with name " + expBlueprint.getName() + " and version " + expBlueprint.getVersion() + " already present in DB.");
        }
        if ( (expBlueprintInfoRepository.findByExpBlueprintId(expBlueprint.getExpBlueprintId()).isPresent())) {
            //log.error("EXP Blueprint with name " + expBlueprint.getName() + " and version " + expBlueprint.getVersion() + " already present in DB.");
            throw new AlreadyExistingEntityException("EXP Blueprint with id " + expBlueprint.getExpBlueprintId());
        }
        expBlueprintRepository.saveAndFlush(expBlueprint);
        ExpBlueprintInfo expbInfo = new ExpBlueprintInfo(expBlueprint.getExpBlueprintId(), expBlueprint.getVersion(), expBlueprint.getName());
        expBlueprintInfoRepository.saveAndFlush(expbInfo);


        return expbInfo.getExpBlueprintId();
    }

}
