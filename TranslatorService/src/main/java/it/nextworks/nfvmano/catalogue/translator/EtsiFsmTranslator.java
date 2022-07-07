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
import it.nextworks.nfvmano.catalogue.domainLayer.customDomainLayer.SliceManagerFsmNspDomainLayer;
import it.nextworks.nfvmano.catalogue.domainLayer.customDomainLayer.SliceManagerNspDomainLayer;
import it.nextworks.nfvmano.catalogues.domainLayer.repo.DomainRepository;
import it.nextworks.nfvmano.catalogues.template.repo.NsTemplateRepository;
import it.nextworks.nfvmano.catalogues.template.services.NsTemplateCatalogueService;
import it.nextworks.nfvmano.libs.ifa.common.elements.Filter;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.FailedOperationException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MethodNotImplementedException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.NotExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.messages.GeneralizedQueryRequest;
import it.nextworks.nfvmano.libs.ifa.templates.NST;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class EtsiFsmTranslator extends AbstractTranslator {
    private static final Logger log = LoggerFactory.getLogger(MultiDomainBasicTranslator.class);


    private DomainRepository domainRepository;

    private TranslationRuleRepository ruleRepo;
    private VsBlueprintRepository vsBlueprintRepository;

    private NsTemplateRepository nsTemplateRepository;

    public EtsiFsmTranslator(VsDescriptorRepository vsdRepo, DomainRepository domainRepository, VsBlueprintRepository vsBlueprintRepository, NsTemplateRepository nsTemplateRepository, TranslationRuleRepository ruleRepo) {
        super(TranslatorType.ETSI_FSM_TRANSLATOR, vsdRepo);
        this.domainRepository = domainRepository;
        this.vsBlueprintRepository = vsBlueprintRepository;
        this.nsTemplateRepository = nsTemplateRepository;
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


            Map<String, String> filterNstParams = new HashMap<>();
            VsdNsdTranslationRule translationRule = findMatchingTranslationRule(vsd);

            NST e2e_nst = nsTemplateRepository.findByNstId(translationRule.getNstId()).get();
            List<String> nstDomains = new ArrayList<>();
            nstDomains.add("5GBARCELONA");
            nstDomains.add("5TONIC");

            Map<String, String> nsstDomain = new HashMap<>();
            nsstDomain.put("6273e9fa145614000e0fd594", "5GBARCELONA");

            nsstDomain.put("nsst_vpn_ai_image_recognition", "5TONIC");
            NfvNsInstantiationInfo info = new NfvNsInstantiationInfo(translationRule.getNstId(),
                    null,
                    null,
                    null,
                    "",

                    nstDomains,
                    nsstDomain,
                    null);
            nfvNsInfo.put(vsdId, info);


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
