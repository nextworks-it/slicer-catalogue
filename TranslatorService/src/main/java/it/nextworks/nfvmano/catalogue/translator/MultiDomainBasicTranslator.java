package it.nextworks.nfvmano.catalogue.translator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.nextworks.nfvmano.catalogue.blueprint.elements.*;
import it.nextworks.nfvmano.catalogue.blueprint.repo.*;
import it.nextworks.nfvmano.catalogue.domainLayer.Domain;
import it.nextworks.nfvmano.catalogue.domainLayer.DomainLayer;
import it.nextworks.nfvmano.catalogue.domainLayer.NspDomainLayer;
import it.nextworks.nfvmano.catalogues.domainLayer.repo.DomainRepository;
import it.nextworks.nfvmano.catalogues.template.repo.NsTemplateRepository;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.FailedOperationException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MethodNotImplementedException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.NotExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.templates.NST;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class MultiDomainBasicTranslator extends AbstractTranslator {

    private static final Logger log = LoggerFactory.getLogger(MultiDomainBasicTranslator.class);

    private TranslationRuleRepository ruleRepo;
    //private VsBlueprintInfoRepository vsBlueprintInfoRepository;
    private NsTemplateRepository nsTemplateRepository;
    private DomainRepository domainRepository;

    private VsBlueprintRepository vsBlueprintRepository;

    public MultiDomainBasicTranslator(VsDescriptorRepository vsdRepo,

                                      TranslationRuleRepository ruleRepo,
                                      NsTemplateRepository nsTemplateRepository,
                                      DomainRepository domainRepository,
                                      VsBlueprintRepository vsBlueprintRepository) {
        super(TranslatorType.MULTI_DOMAIN_TRANSLATOR, vsdRepo);
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

            // TODO: search for NST/NSD in destination domains if domain catalogues will be available
            log.debug("Retrieving NST with id {} from NS template repository", rule.getNstId());
            Optional<NST> optionalNST = nsTemplateRepository.findByNstId(rule.getNstId());

            NST e2e_nst;
            List<String> nsstIds;

            List<String> edgeServices = new ArrayList<>();
            List<String> coreServices = new ArrayList<>();

            List<String> nstDomains = new ArrayList<>();
            List<String> edgeDomains = new ArrayList<>();
            List<String> coreDomains = new ArrayList<>();

            List<Domain> domains = domainRepository.findAll();
            for (Domain domain: domains) {
                // Assuming that each domain has one NSP DomainLayer
                NspDomainLayer nspDomain;
                List<DomainLayer> ownedLayers = domain.getOwnedLayers();
                for (DomainLayer domainLayer : ownedLayers) {
                    if (domainLayer instanceof NspDomainLayer) {
                        nspDomain = (NspDomainLayer) domain.getOwnedLayers().get(0);
                        if (nspDomain.isRANEnabled()) {
                            edgeDomains.add(domain.getDomainId());
                            log.debug("Added domain {} to edgeDomains", domain.getDomainId());
                        } else {
                            coreDomains.add(domain.getDomainId());
                            log.debug("Added domain {} to coreDomains", domain.getDomainId());
                        }
                        nstDomains.add(domain.getDomainId());
                    }
                }
            }

            Map<String, String> nsstDomain = new HashMap<>();
            if (optionalNST.isPresent()) {
                e2e_nst = optionalNST.get();
                nsstIds = e2e_nst.getNsstIds();
                log.debug("Retrieved e2e NST with id: " + e2e_nst.getNstId());
                try {
                    log.debug("NSSTs in NST: \n" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(nsstIds));
                } catch (JsonProcessingException e) {
                    log.error("Unable to parse NSSts list: " + e.getMessage());
                }

                for (String nsstId : nsstIds) {
                    NST nsst;
                    Optional<NST> optionalNsst = nsTemplateRepository.findByNstId(nsstId);

                    if (optionalNsst.isPresent()) {
                        nsst = optionalNsst.get();
                        if (nsst.getNstServiceProfile() != null) {
                            edgeServices.add(nsstId);
                            log.debug("Added edge service {} to edgeService", nsstId);
                        } else {
                            coreServices.add(nsstId);
                            log.debug("Added core service {} to coreService", nsstId);
                        }
                    } else {
                        throw new NotExistingEntityException("Subnet NST with nstId " + nsstId + " not present in DB.");
                    }
                }

                // This approach is a kind of round robin, given the NSTs and domains characteristics
                if (edgeDomains.size() >= edgeServices.size()) {
                    for (String edgeNsstId : edgeServices) {
                        for (String edgeDomainId : edgeDomains) {
                            if (!nsstDomain.containsValue(edgeDomainId)) {
                                log.debug("Edge service {} mapped to domain {}", edgeNsstId, edgeDomainId);
                                nsstDomain.put(edgeNsstId, edgeDomainId);
                                break;
                            }
                        }
                    }
                } else {
                    throw new FailedOperationException("More than one NS instantiation in a single domain is not yet supported.");
                }
                if (coreDomains.size() >= coreServices.size()) {
                    for (String coreNsstId : coreServices) {
                        for (String coreDomainId: coreDomains) {
                            if (!nsstDomain.containsValue(coreDomainId)) {
                                log.debug("Core service {} mapped to domain {}", coreNsstId, coreDomainId);
                                nsstDomain.put(coreNsstId, coreDomainId);
                                break;
                            }
                        }
                    }
                } else {
                    throw new FailedOperationException("More than one NS instantiation in a single domain is not yet supported.");
                }

            } else {
                throw new NotExistingEntityException("NST with nstId " + rule.getNstId() + " not present in DB.");
            }

            Optional<VsBlueprint> vsbO = vsBlueprintRepository.findByBlueprintId(vsd.getVsBlueprintId());
            SliceServiceParameters vsdParameters = vsd.getSliceServiceParameters();
            SliceServiceParameters finalServiceParameters = vsdParameters;
            if(vsbO.isPresent()){
                if(vsbO.get().getSliceServiceType().equals(SliceServiceType.URLLC)){

                }

            }
            NfvNsInstantiationInfo info = new NfvNsInstantiationInfo(rule.getNstId(), rule.getNsdId(), rule.getNsdVersion(), rule.getNsFlavourId(), rule.getNsInstantiationLevelId(), nstDomains, nsstDomain, finalServiceParameters );
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
