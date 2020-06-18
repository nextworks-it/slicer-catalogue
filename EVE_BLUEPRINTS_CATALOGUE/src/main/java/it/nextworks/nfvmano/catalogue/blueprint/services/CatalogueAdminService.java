package it.nextworks.nfvmano.catalogue.blueprint.services;

import it.nextworks.nfvmano.catalogue.blueprint.elements.*;
import it.nextworks.nfvmano.catalogue.blueprint.exceptions.ConflictiveOperationException;
import it.nextworks.nfvmano.catalogue.blueprint.repo.*;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CatalogueAdminService {

    private static final Logger log = LoggerFactory.getLogger(CatalogueAdminService.class);




    @Autowired
    private ExpBlueprintInfoRepository expBlueprintInfoRepository;

    @Autowired
    private ExpBlueprintRepository expBlueprintRepository;

    @Autowired
    private ExpBlueprintCatalogueService expBlueprintCatalogueService;

    @Autowired
    private ExpDescriptorCatalogueService expDescriptorCatalogueService;

    @Autowired
    private TestCaseBlueprintInfoRepository testCaseBlueprintInfoRepository;

    @Autowired
    private TcBlueprintCatalogueService tcBlueprintCatalogueService;

    @Autowired
    private VsBlueprintInfoRepository vsBlueprintInfoRepository;

    @Autowired
    private VsBlueprintCatalogueService vsBlueprintCatalogueService;

    @Autowired
    private VsDescriptorCatalogueService vsDescriptorCatalogueService;

    @Autowired
    private CtxBlueprintCatalogueService ctxBlueprintCatalogueService;

    @Autowired
    private CtxBlueprintInfoRepository ctxBlueprintInfoRepository;

    @Autowired
    private TcDescriptorCatalogueService tcDescriptorCatalogueService;


    public synchronized void forceDeleteVsBlueprint(String vsBlueprintId, String tenantId) throws NotExistingEntityException, FailedOperationException, MalformattedElementException, NotPermittedOperationException, MethodNotImplementedException {
        log.debug("Force Delete VSB:"+vsBlueprintId);
        Optional<VsBlueprintInfo> vsBlueprintInfoOpt = vsBlueprintInfoRepository.findByVsBlueprintId(vsBlueprintId);
        if (vsBlueprintInfoOpt.isPresent()) {


            log.debug("Retrieving associated expbs");
            List<ExpBlueprint> expBlueprints = expBlueprintRepository.findByVsBlueprintId(vsBlueprintId);
            if (expBlueprints != null && !expBlueprints.isEmpty()) {
                for (ExpBlueprint expBlueprint : expBlueprints) {

                    forceDeleteExpBlueprint(expBlueprint.getExpBlueprintId(), tenantId);


                }
            }

            //VSD should have been removed when removing the ExpD
            vsBlueprintCatalogueService.deleteVsBlueprint(vsBlueprintId);


        } else {
            log.debug("VsBlueprint with id:" + vsBlueprintId + " NOT FOUND");
            throw new NotExistingEntityException("VsBlueprint with id:" + vsBlueprintId + " NOT FOUND");
        }

    }

    public synchronized  void forceDeleteTcBlueprint(String tcBlueprintId, String tenantId)throws NotExistingEntityException, FailedOperationException, MalformattedElementException, NotPermittedOperationException, MethodNotImplementedException {
        log.debug("Force Delete TCB:"+tcBlueprintId);
        Optional<TestCaseBlueprintInfo> tcBlueprintInfoOpt = testCaseBlueprintInfoRepository.findByTestCaseBlueprintId(tcBlueprintId);
        if (tcBlueprintInfoOpt.isPresent()) {


            log.debug("Retrieving associated expbs");
            List<ExpBlueprint> expBlueprints = getTcbExpbs(tcBlueprintId);
            if (expBlueprints != null && !expBlueprints.isEmpty()) {
                for (ExpBlueprint expBlueprint : expBlueprints) {

                    forceDeleteExpBlueprint(expBlueprint.getExpBlueprintId(), tenantId);

                }
            }

            //TCD should have been removed when removing the ExpD
             tcBlueprintCatalogueService.deleteTestCaseBlueprint(tcBlueprintId);


        } else {
            log.debug("TcBlueprint with id:" + tcBlueprintId + " NOT FOUND");
            throw new NotExistingEntityException("TcBlueprint with id:" + tcBlueprintId + " NOT FOUND");
        }

    }

    private List<ExpBlueprint> getTcbExpbs(String tcBlueprintId) {
        List<ExpBlueprint> expBlueprints = expBlueprintRepository.findAll().stream()
                .filter(expB -> expB.getTcBlueprintIds().contains(tcBlueprintId))
                .collect(Collectors.toList());
        return expBlueprints;

    }

    public synchronized void forceDeleteCtxBlueprint(String ctxBlueprintId, String tenantId) throws NotExistingEntityException, FailedOperationException, MalformattedElementException, NotPermittedOperationException, MethodNotImplementedException {
        log.debug("Force Delete CtxB:"+ctxBlueprintId);
        Optional<CtxBlueprintInfo> ctxBlueprintInfoOpt = ctxBlueprintInfoRepository.findByCtxBlueprintId(ctxBlueprintId);
        if (ctxBlueprintInfoOpt.isPresent()) {


            log.debug("Retrieving associated expbs");
            List<ExpBlueprint> expBlueprints = getCtxBlueprintExpbs(ctxBlueprintId);
            if (expBlueprints != null && !expBlueprints.isEmpty()) {
                for (ExpBlueprint expBlueprint : expBlueprints) {

                    forceDeleteExpBlueprint(expBlueprint.getExpBlueprintId(), tenantId);


                }
            }

            //CTXD should have been removed when removing the ExpD
            ctxBlueprintCatalogueService.deleteCtxBlueprint(ctxBlueprintId);


        } else {
            log.debug("CtxBlueprint with id:" + ctxBlueprintId + " NOT FOUND");
            throw new NotExistingEntityException("VsBlueprint with id:" + ctxBlueprintId + " NOT FOUND");
        }

    }

    private List<ExpBlueprint> getCtxBlueprintExpbs(String ctxBlueprintId) {
        List<ExpBlueprint> expBlueprints = expBlueprintRepository.findAll().stream()
                .filter(expB -> expB.getCtxBlueprintIds().contains(ctxBlueprintId))
                .collect(Collectors.toList());
        return expBlueprints;
    }


    public synchronized void forceDeleteExpBlueprint(String expBlueprintId, String tenantId) throws NotExistingEntityException, FailedOperationException, MalformattedElementException, NotPermittedOperationException, MethodNotImplementedException {
        log.debug("Force deleting ExpB:" + expBlueprintId);
        Optional<ExpBlueprint> expBlueprintOpt = expBlueprintRepository.findByExpBlueprintId(expBlueprintId);
        if (expBlueprintOpt.isPresent()) {
            ExpBlueprint expBlueprint = expBlueprintOpt.get();
            log.debug("Retrieving ExpDs for:" + expBlueprint.getExpBlueprintId());

            Optional<ExpBlueprintInfo> expBlueprintInfoOpt = expBlueprintInfoRepository.findByExpBlueprintId(expBlueprint.getExpBlueprintId());
            if (expBlueprintInfoOpt.isPresent()) {
                List<String> activeExpds = new ArrayList<>(expBlueprintInfoOpt.get().getActiveExpdId());
                if (activeExpds != null && !activeExpds.isEmpty()) {
                    log.debug("Deleting Expds");
                    for (String expdId : activeExpds) {

                        forceDeleteExpDescriptor(expdId, tenantId);
                    }
                }

                expBlueprintCatalogueService.deleteExpBlueprint(expBlueprintId);
            } else {
                log.error("Catalogue inconsistency. ExpB information element not found:" + expBlueprint.getExpBlueprintId());
                throw new NotExistingEntityException("Database insconsistency. ExpB information element not found:" + expBlueprint.getExpBlueprintId());
            }
        } else {

            throw new NotExistingEntityException("ExpB element not found:" + expBlueprintId);
        }

    }


    public synchronized void forceDeleteExpDescriptor(String expdId, String tenantId) throws FailedOperationException, MalformattedElementException, NotPermittedOperationException, NotExistingEntityException, MethodNotImplementedException {
        log.debug("Force Delete ExpD:"+expdId);
        try {
            expDescriptorCatalogueService.deleteExpDescriptor(expdId, tenantId, true, true);
        } catch (ConflictiveOperationException e) {
            log.error("ConflictiveOperationException, this should not happen when forced:", e);
        }
    }


}
