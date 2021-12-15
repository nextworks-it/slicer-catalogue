package it.nextworks.nfvmano.catalogue.translator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import it.nextworks.nfvmano.catalogue.domainLayer.customDomainLayer.SonataNspDomainLayer;
import it.nextworks.nfvmano.catalogues.domainLayer.repo.DomainRepository;
import it.nextworks.nfvmano.catalogues.template.repo.NsTemplateRepository;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.FailedOperationException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MethodNotImplementedException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.NotExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.templates.NST;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SliceManagerTranslator extends AbstractTranslator {
    private static final Logger log = LoggerFactory.getLogger(MultiDomainBasicTranslator.class);


    private DomainRepository domainRepository;

    private VsBlueprintRepository vsBlueprintRepository;

    public SliceManagerTranslator(VsDescriptorRepository vsdRepo, DomainRepository domainRepository, VsBlueprintRepository vsBlueprintRepository) {
        super(TranslatorType.SLICE_MANAGER_TRANSLATOR, vsdRepo);
        this.domainRepository = domainRepository;
        this.vsBlueprintRepository = vsBlueprintRepository;
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
            VsBlueprint vsBlueprint = vsBlueprintRepository.findByBlueprintId(vsd.getVsBlueprintId()).get();


            NST e2e_nst;
            List<String> nsstIds;


            List<Domain> domains = domainRepository.findAll();
            for (Domain domain : domains) {
                // Assuming that each domain has one NSP DomainLayer
                NspDomainLayer nspDomain;
                List<DomainLayer> ownedLayers = domain.getOwnedLayers();
                for (DomainLayer domainLayer : ownedLayers) {
                    if (domainLayer instanceof NspDomainLayer && domainLayer instanceof SliceManagerNspDomainLayer) {
                        String baseUrl = domain.getDomainInterface().getUrl();
                        ApiClient client = new ApiClient();

                        client.setBasePath(baseUrl);
                        client.setConnectTimeout(0);
                        NetworkSliceTypeNestApi nstApi = new NetworkSliceTypeNestApi();
                        GenericNetworkSliceTemplateGstApi gstApi = new GenericNetworkSliceTemplateGstApi();
                        gstApi.setApiClient(client);
                        nstApi.setApiClient(client);
                        try {
                            GetSlic3Types sliceTypes =  nstApi.getSlic3Types(null);
                            log.debug("Slice types:", sliceTypes);
                            for(PostSlic3Type slic3Type : sliceTypes){
                                String gstId = slic3Type.getGenericSliceTemplateId();
                                PostSlic3Temp gstTemp = gstApi.getSlic3Temp(gstId);
                                gstTemp.getAttributes().getCoverageArea();
                                //TODO: check coverage area, ul/dl rate, etc
                                List<String> domainIds = new ArrayList<>();
                                domainIds.add(domain.getDomainId());

                                nfvNsInfo.put(vsdId, new NfvNsInstantiationInfo(slic3Type.getId(),
                                        null,
                                        null,
                                        null,
                                        null,
                                        domainIds,
                                        null,
                                        null)
                                        );

                                return nfvNsInfo;
                            }
                        } catch (ApiException e) {
                           log.error("Error while interacting with Slice Manager", e);
                           throw new FailedOperationException(e);
                        }

                    }
                }
            }
        }

        throw new FailedOperationException("Could not find matching translation");
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
