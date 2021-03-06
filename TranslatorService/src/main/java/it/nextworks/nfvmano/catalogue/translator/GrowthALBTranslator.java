package it.nextworks.nfvmano.catalogue.translator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.nextworks.nfvmano.catalogue.blueprint.elements.SliceServiceParameters;
import it.nextworks.nfvmano.catalogue.blueprint.elements.VsBlueprint;
import it.nextworks.nfvmano.catalogue.blueprint.elements.VsDescriptor;
import it.nextworks.nfvmano.catalogue.blueprint.elements.VsdNsdTranslationRule;
import it.nextworks.nfvmano.catalogue.blueprint.repo.TranslationRuleRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.VsBlueprintRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.VsDescriptorRepository;
import it.nextworks.nfvmano.catalogue.domainLayer.Domain;
import it.nextworks.nfvmano.catalogue.domainLayer.DomainLayer;
import it.nextworks.nfvmano.catalogue.domainLayer.NspDomainLayer;
import it.nextworks.nfvmano.catalogues.domainLayer.repo.DomainRepository;
import it.nextworks.nfvmano.catalogues.template.repo.NsTemplateRepository;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.FailedOperationException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MethodNotImplementedException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.NotExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.templates.NST;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class GrowthALBTranslator extends AbstractTranslator {

    private static final Logger log = LoggerFactory.getLogger(GrowthALBTranslator.class);

    private TranslationRuleRepository ruleRepo;
    //private VsBlueprintInfoRepository vsBlueprintInfoRepository;
    private NsTemplateRepository nsTemplateRepository;
    private DomainRepository domainRepository;

    private VsBlueprintRepository vsBlueprintRepository;

    public GrowthALBTranslator(VsDescriptorRepository vsdRepo,

                               TranslationRuleRepository ruleRepo,
                               NsTemplateRepository nsTemplateRepository,
                               DomainRepository domainRepository,
                               VsBlueprintRepository vsBlueprintRepository) {
        super(TranslatorType.GROWTH_ALB_TRANSLATOR, vsdRepo);
        this.ruleRepo = ruleRepo;
        this.nsTemplateRepository = nsTemplateRepository;
        this.domainRepository = domainRepository;
        this.vsBlueprintRepository = vsBlueprintRepository;
    }

    @Override
    public Map<String, NfvNsInstantiationInfo> translateVsd(List<String> vsdIds)
            throws FailedOperationException, NotExistingEntityException, MethodNotImplementedException {
        log.debug("VSD->NSD translation at multi-domain basic translator.");
        if (vsdIds.size() > 1) throw new FailedOperationException("Processing of multiple VSDs not supported by the basic translator");
        Map<String, NfvNsInstantiationInfo> nfvNsInfo = new HashMap<>();
        Map<String, VsDescriptor> vsds = retrieveVsDescriptors(vsdIds);
        for (Map.Entry<String, VsDescriptor> entry : vsds.entrySet()) {
            String vsdId = entry.getKey();
            VsDescriptor vsd = entry.getValue();
            VsdNsdTranslationRule rule = findMatchingTranslationRule(vsd);

            ObjectMapper mapper = new ObjectMapper();
            try {
                log.debug("Translation rule: \n" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rule));
            } catch (JsonProcessingException e) {
                log.error("Unable to parse Translation Rule: " + e.getMessage());
            }

            Map<String, String> nsstDomain = new HashMap<>();
            Optional<NST> optionalNST = nsTemplateRepository.findByNstId(rule.getNstId());
            if (optionalNST.isPresent()) {
                NST e2e_nst = optionalNST.get();
                List<String> nsstIds = e2e_nst.getNsstIds();
                for (String nsstId : nsstIds) {
                    nsstDomain.put(nsstId, "SONATA");
                }
            }
            List<String> nstDomains = new ArrayList<>();
            nstDomains.add("SONATA");
            NfvNsInstantiationInfo info = new NfvNsInstantiationInfo(rule.getNstId(),
                    rule.getNsdId(),
                    rule.getNsdVersion(),
                    rule.getNsFlavourId(),
                    rule.getNsInstantiationLevelId(),
                    nstDomains,
                    nsstDomain,
                    null );
            nfvNsInfo.put(vsdId, info);
            log.debug("Added NS instantiation info for VSD " + vsdId + " - NST ID: " + rule.getNstId());
        }
        return nfvNsInfo;
    }


    private VsdNsdTranslationRule findMatchingTranslationRule(VsDescriptor vsd) throws FailedOperationException, NotExistingEntityException {
        String vsbId = vsd.getVsBlueprintId();
        Map<String, String> vsdParameters = vsd.getQosParameters();
        return findMatchingTranslationRule(vsbId, vsdParameters);
    }

    private VsdNsdTranslationRule findMatchingTranslationRule(String blueprintId, Map<String, String> descriptorParameters) throws FailedOperationException, NotExistingEntityException {
        if ((blueprintId == null) || (descriptorParameters.isEmpty())) throw new NotExistingEntityException("Impossible to translate descriptor into NSD because of missing parameters");
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


    /**
     * This internal method returns a map with key VSD ID and value VSD, where the VSDs
     * are retrieved from the DB.
     *
     * @param vsdIds IDs of the VSDs to be retrieved
     * @return Map with key VSD ID and value VSD.
     * @throws NotExistingEntityException if one or more VSDs are not found.
     */
    private Map<String, VsDescriptor> retrieveVsDescriptors(List<String> vsdIds) throws NotExistingEntityException {
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
