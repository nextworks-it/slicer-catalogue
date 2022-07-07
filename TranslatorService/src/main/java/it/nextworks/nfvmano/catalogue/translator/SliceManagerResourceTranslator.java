package it.nextworks.nfvmano.catalogue.translator;

import io.swagger.client.slice_manager.ApiClient;
import io.swagger.client.slice_manager.ApiException;
import io.swagger.client.slice_manager.api.GenericNetworkSliceTemplateGstApi;
import io.swagger.client.slice_manager.api.NetworkSliceTypeNestApi;
import io.swagger.client.slice_manager.model.GetSlic3Types;
import io.swagger.client.slice_manager.model.PostSlic3Temp;
import io.swagger.client.slice_manager.model.PostSlic3Type;
import it.nextworks.nfvmano.catalogue.blueprint.elements.VsBlueprint;
import it.nextworks.nfvmano.catalogue.blueprint.elements.VsDescriptor;
import it.nextworks.nfvmano.catalogue.blueprint.elements.VsdNsdTranslationRule;
import it.nextworks.nfvmano.catalogue.blueprint.repo.TranslationRuleRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.VsBlueprintRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.VsDescriptorRepository;
import it.nextworks.nfvmano.catalogue.domainLayer.Domain;
import it.nextworks.nfvmano.catalogue.domainLayer.DomainLayer;
import it.nextworks.nfvmano.catalogue.domainLayer.NspDomainLayer;
import it.nextworks.nfvmano.catalogue.domainLayer.customDomainLayer.SliceManagerNspDomainLayer;
import it.nextworks.nfvmano.catalogues.domainLayer.repo.DomainRepository;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.FailedOperationException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MethodNotImplementedException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.NotExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.templates.NST;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SliceManagerResourceTranslator extends AbstractTranslator {
    private static final Logger log = LoggerFactory.getLogger(SliceManagerResourceTranslator.class);


    private DomainRepository domainRepository;
    private TranslationRuleRepository ruleRepo;
    private VsBlueprintRepository vsBlueprintRepository;

    public SliceManagerResourceTranslator(VsDescriptorRepository vsdRepo, DomainRepository domainRepository, VsBlueprintRepository vsBlueprintRepository, TranslationRuleRepository ruleRepo) {
        super(TranslatorType.SLICE_MANAGER_RESOURCE_TRANSLATOR, vsdRepo);
        this.domainRepository = domainRepository;
        this.vsBlueprintRepository = vsBlueprintRepository;
        this.ruleRepo=ruleRepo;
    }

    @Override
    public Map<String, NfvNsInstantiationInfo> translateVsd(List<String> vsdIds)
            throws FailedOperationException, NotExistingEntityException, MethodNotImplementedException {
        log.debug("VSD->NSD translation at multi-domain basic translator.");
        if (vsdIds.size() > 1)
            throw new FailedOperationException("Processing of multiple VSDs not supported by the basic translator");
        Map<String, NfvNsInstantiationInfo> nfvNsInfo = new HashMap<>();
        Map<String, VsDescriptor> vsds = retrieveVsDescriptors(vsdIds);
        for (Map.Entry<String, VsDescriptor> entry : vsds.entrySet()) {
            String vsdId = entry.getKey();
            VsDescriptor vsd = entry.getValue();
            VsdNsdTranslationRule translationRule = findMatchingTranslationRule(vsd);
            VsBlueprint blueprint = vsBlueprintRepository.findByBlueprintId(vsd.getVsBlueprintId()).get();
            //This translator is meant to be used in a 5GZORRO scenario
            // if the vSB name ends in resource means the allocation should happen through the slice manager
            //otherwise we use the default domain (NSMF) for the instantiation
            if (blueprint.getName().contains("_resource")) {
                List<String> nstDomains = new ArrayList<>();
                nstDomains.add("5GBARCELONA");
                Map<String, String> nsstDomain = new HashMap<>();
                nsstDomain.put(translationRule.getNstId(), "5GBARCELONA");
                NfvNsInstantiationInfo info = new NfvNsInstantiationInfo(translationRule.getNstId(),
                        null,
                        null,
                        null,
                        "",

                        nstDomains,
                        nsstDomain,
                        null);
                nfvNsInfo.put(vsdId, info);
            }else{
                NfvNsInstantiationInfo info = new NfvNsInstantiationInfo(translationRule.getNstId(),
                        translationRule.getNsdId(),
                        translationRule.getNsdVersion(),
                        translationRule.getNsFlavourId(),
                        translationRule.getNsInstantiationLevelId(),

                        null,
                        null,
                        null);
                nfvNsInfo.put(vsdId, info);
            }
        }
        return nfvNsInfo;
    }

    private VsdNsdTranslationRule findMatchingTranslationRule(VsDescriptor vsd) throws FailedOperationException, NotExistingEntityException {
        String vsbId = vsd.getVsBlueprintId();
        Map<String, String> vsdParameters = vsd.getQosParameters();
        return findMatchingTranslationRule(vsbId, vsdParameters);
    }

    private VsdNsdTranslationRule findMatchingTranslationRule(String blueprintId, Map<String, String> descriptorParameters) throws FailedOperationException, NotExistingEntityException {
        if ((blueprintId == null) || (descriptorParameters.isEmpty()))
            throw new NotExistingEntityException("Impossible to translate descriptor into NSD because of missing parameters");
        List<VsdNsdTranslationRule> rules = ruleRepo.findByBlueprintId(blueprintId);
        for (VsdNsdTranslationRule rule : rules) {
            if (rule.matchesVsdParameters(descriptorParameters)) {
                log.debug("Found translation rule");
                return rule;
            }
        }
        log.debug("Impossible to find a translation rule matching the given descriptor parameters");
        throw new FailedOperationException("Impossible to find a translation rule matching the given descriptor parameters");
    }
    private Map<String, VsDescriptor> retrieveVsDescriptors(List<String> vsdIds) throws
            NotExistingEntityException {
        log.debug("Retrieving VS descriptors from DB.");
        Map<String, VsDescriptor> vsds = new HashMap<>();
        for (String vsdId : vsdIds) {
            Optional<VsDescriptor> vsd = vsdRepo.findByVsDescriptorId(vsdId);
            if (vsd.isPresent()) vsds.put(vsdId, vsd.get());
            else throw new NotExistingEntityException("Unable to find VSD with ID " + vsdId + " in DB.");
        }
        return vsds;
    }
}
