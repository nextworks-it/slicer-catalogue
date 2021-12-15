/*
 * Copyright (c) 2019 Nextworks s.r.l
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.nextworks.nfvmano.catalogues.template.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import it.nextworks.nfvmano.libs.ifa.descriptors.nsd.Pnfd;
import it.nextworks.nfvmano.catalogue.template.elements.NstConfigurationRule;
import it.nextworks.nfvmano.catalogues.template.repo.ConfigurationRuleRepository;
import it.nextworks.nfvmano.libs.ifasol.catalogues.interfaces.enums.NsdFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import it.nextworks.nfvmano.catalogue.template.elements.NsTemplateInfo;
import it.nextworks.nfvmano.catalogue.template.interfaces.NsTemplateCatalogueInterface;
import it.nextworks.nfvmano.catalogue.template.messages.OnBoardNsTemplateRequest;
import it.nextworks.nfvmano.catalogue.template.messages.QueryNsTemplateResponse;
import it.nextworks.nfvmano.catalogues.template.TemplateCatalogueUtilities;
import it.nextworks.nfvmano.catalogues.template.repo.NsTemplateInfoRepository;
import it.nextworks.nfvmano.catalogues.template.repo.NsTemplateRepository;
import it.nextworks.nfvmano.libs.ifasol.catalogues.interfaces.messages.*;
import it.nextworks.nfvmano.libs.ifa.common.elements.Filter;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.*;
import it.nextworks.nfvmano.libs.ifa.common.messages.GeneralizedQueryRequest;
import it.nextworks.nfvmano.libs.ifa.descriptors.nsd.Nsd;
import it.nextworks.nfvmano.libs.ifa.templates.NST;
import it.nextworks.nfvmano.nfvodriver.NfvoCatalogueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;

@Service
public class NsTemplateCatalogueService implements NsTemplateCatalogueInterface {

	private static final Logger log = LoggerFactory.getLogger(NsTemplateCatalogueService.class);

	@Autowired
	private NfvoCatalogueService nfvoCatalogueService;

	@Autowired
	private NsTemplateInfoRepository nstInfoRepository;

	@Autowired
	private NsTemplateRepository nstRepository;

	@Autowired
	private ConfigurationRuleRepository configurationRuleRepository;

	public NsTemplateCatalogueService() { }

	public static Filter buildVnfPackageInfoFilter(String vnfProductName, String swVersion, String provider) {
		//VNF_PACKAGE_PRODUCT_NAME
		//VNF_PACKAGE_SW_VERSION
		//VNF_PACKAGE_PROVIDER
		Map<String, String> filterParams = new HashMap<>();
		filterParams.put("VNF_PACKAGE_PRODUCT_NAME", vnfProductName);
		filterParams.put("VNF_PACKAGE_SW_VERSION", swVersion);
		filterParams.put("VNF_PACKAGE_PROVIDER", provider);
		return new Filter(filterParams);
	}
	public static Filter buildNsdInfoFilter(String nsdId, String nsdVersion) {
		//NSD_ID
		//NSD_VERSION
		Map<String, String> filterParams = new HashMap<>();
		filterParams.put("NSD_ID", nsdId);
		filterParams.put("NSD_VERSION", nsdVersion);
		return new Filter(filterParams);
	}
	@Override
	public synchronized String onBoardNsTemplate(OnBoardNsTemplateRequest request)
			throws MethodNotImplementedException, MalformattedElementException, AlreadyExistingEntityException, FailedOperationException {
		log.debug("Processing request to onboard a new NsTemplate");
		request.isValid();
		//we have to add also nsd and vnf packages to store


		log.debug("Processing NFV descriptors");
		try {
			if(request.getVnfPackages()!=null) {

				log.debug("Storing VNF packages");
				List<OnBoardVnfPackageRequest> vnfPackages = request.getVnfPackages();
				for (OnBoardVnfPackageRequest vnfR : vnfPackages) {
					try {
						OnBoardVnfPackageResponse vnfReply = nfvoCatalogueService.onBoardVnfPackage(vnfR);
						String vnfPackageId = vnfReply.getOnboardedVnfPkgInfoId();
						log.debug("Added VNF package for VNF " + vnfR.getName() +
								", version " + vnfR.getVersion() + ", provider " + vnfR.getProvider() + " in NFVO catalogue. VNF package ID: " + vnfPackageId);
						//nsTemplateInfo.addVnfPackageInfoId(vnfPackageId);
					} catch (AlreadyExistingEntityException e) {
						log.debug("The VNF package is already present in the NFVO catalogue. ");

						//nsTemplateInfo.addVnfPackageInfoId(oldVnfPackageId);
					}
				}
			}
			if(request.getNsds()!=null) {
				log.debug("Storing NSDs");

				for (OnboardNsdRequest nsdReq : request.getNsds()) {
					try {
						Map<String, String> userDefinedData = new HashMap<>();
						String nsdInfoId = nfvoCatalogueService.onboardNsd(nsdReq);
						//nsTemplateInfo.addNsdInfoId(nsdInfoId);
					} catch (AlreadyExistingEntityException e) {
						log.debug("The NSD is already present in the NFVO catalogue. IGNORING");

					}
				}
			}
			if(request.getPnfds()!=null) {
				log.debug("Storing Pnfds");

				for (OnboardPnfdRequest pnfdReq : request.getPnfds()) {
					try {
						Map<String, String> userDefinedData = new HashMap<>();
						String pnfdInfoId = nfvoCatalogueService.onboardPnfd(pnfdReq);
						log.debug("Added PNFD " + pnfdInfoId);
						//nsTemplateInfo.addVnfPackageInfoId(vnfPackageId);
					} catch (AlreadyExistingEntityException e) {
						log.debug("The PNFD is already present in the NFVO catalogue. IGNORING");

					}
				}
			}
			String nstId = storeNsTemplate(request.getNst());
			NsTemplateInfo nsTemplateInfo;

			try {
				nsTemplateInfo = getNsTemplateInfo(nstId);
			} catch (NotExistingEntityException e) {
				log.error("Impossible to retrieve nsTemplateInfo. Error!");
				throw new FailedOperationException("Internal error: impossible to retrieve NsTemplateInfo.");
			}
			nstInfoRepository.saveAndFlush(nsTemplateInfo);

			request.setNstIdInConfigurationRules(nstId);
			log.debug("Storing configuration rules");
			List<NstConfigurationRule> crs = request.getConfigurationRules();
			for (NstConfigurationRule cr : crs) {
				cr.isValid();
				configurationRuleRepository.saveAndFlush(cr);
			}
			log.debug("Configuration rules saved in internal DB.");

			return nstId;
		}catch (Exception e) {
			log.error("Something went wrong when processing SO descriptors.",e );
			throw new FailedOperationException("Internal error: something went wrong when processing VNF packages / NS descriptors.", e);
		}
	}

	private NsTemplateInfo getNsTemplateInfo(String nstID) throws NotExistingEntityException {
		NsTemplateInfo nstInfo;
		nstInfoRepository.findByNsTemplateId(nstID).isPresent();
		if(nstInfoRepository.findByNsTemplateId(nstID).isPresent()){
			nstInfo=nstInfoRepository.findByNsTemplateId(nstID).get();
		}
		else {
			throw new NotExistingEntityException("NsTemplate info for NsTemplate with ID " + nstID + " not found in DB.");
		}
		NST nst=getNsTemplate(nstID);
		nstInfo.setNST(nst);
		return nstInfo;
	}


	private NsTemplateInfo getNsTemplateInfo(String name, String version) throws NotExistingEntityException {
		NsTemplateInfo nstInfo;
		if(nstInfoRepository.findByNameAndNsTemplateVersion(name, version).isPresent()){
			nstInfo=nstInfoRepository.findByNameAndNsTemplateVersion(name, version).get();
		}
		else {
			throw new NotExistingEntityException("NsTemplate info for NsTemplate with name " + name + " and version "+ version +"not found in DB.");
		}

		NST nst=getNsTemplate(name,version);
		nstInfo.setNST(nst);
		return nstInfo;
	}

	private NST getNsTemplate(String nstID) throws NotExistingEntityException{
		if (nstRepository.findByNstId(nstID).isPresent()) return nstRepository.findByNstId(nstID).get();
		else throw new NotExistingEntityException("NsTemplate with ID " + nstID + " not found in DB.");
	}

	private NST getNsTemplate(String name, String version) throws NotExistingEntityException{
		if (nstRepository.findByNstNameAndNstVersion(name, version).isPresent()) return nstRepository.findByNstNameAndNstVersion(name, version).get();
		else throw new NotExistingEntityException("NsTemplate with name " + name + " and version " + version + " not found in DB.");
	}

	@Override
	public QueryNsTemplateResponse queryNsTemplate(GeneralizedQueryRequest request) throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException {
		log.debug("Processing request to query a NsTemplate");
		request.isValid();

		//At the moment the only filters accepted are:
		//1. NS Template name and version
		//NST_NAME & NST_VERSION
		//2. NS Template ID
		//NST_ID
		//No attribute selector is supported at the moment

		List<NsTemplateInfo> nstInfos = new ArrayList<>();

		Filter filter = request.getFilter();
		
		
		 List<String> attributeSelector = request.getAttributeSelector();
		 if ((attributeSelector == null) || (attributeSelector.isEmpty())) {
	        	Map<String, String> filterPars = filter.getParameters();
	        	
	            if (filterPars.size() == 1 && filterPars.containsKey("NST_ID")) {
	            	String nstId = filterPars.get("NST_ID");
	            	NsTemplateInfo nstInfo = getNsTemplateInfo(nstId);
	            	nstInfos.add(nstInfo);
	            	log.debug("Added NsTemplate info for NsTemplate ID " + nstId);
	            } else if (filterPars.size() == 2 && filterPars.containsKey("NST_NAME") && filterPars.containsKey("NST_VERSION")) {
	            	String nstName = filterPars.get("NST_NAME");
	            	String nstVersion = filterPars.get("NST_VERSION");
	            	NsTemplateInfo nstInfo = getNsTemplateInfo(nstName, nstVersion);
	            	nstInfos.add(nstInfo);
	            	log.debug("Added NsTemplate info for NsTemplate with name " + nstName + " and version " + nstVersion);
	            } else if (filterPars.isEmpty()) {
	            	nstInfos = getAllNsTemplateInfos();
	            	log.debug("Addes all the NTSs info available in DB.");
	            }
	            return new QueryNsTemplateResponse(nstInfos);
	        } else {
	        	String logErrorStr = "Received query NsTemplate with attribute selector. Not supported at the moment.";
	            log.error(logErrorStr);
	            throw new MethodNotImplementedException(logErrorStr);
	        }
    }

    
	private List<NsTemplateInfo> getAllNsTemplateInfos() throws NotExistingEntityException {
		List<NsTemplateInfo> nstInfos = nstInfoRepository.findAll();
		for (NsTemplateInfo nstInfo : nstInfos) {
			String name = nstInfo.getName();
			String version = nstInfo.getNsTemplateVersion();
			NST nst = getNsTemplate(name, version);
			nstInfo.setNST(nst);
		}
		return nstInfos;
	}
	
    @Override
    public synchronized void deleteNsTemplate(String nsTemplateId) throws MethodNotImplementedException, MalformattedElementException, NotExistingEntityException, FailedOperationException {
    	log.debug("Processing request to delete a NsTemplate with ID " + nsTemplateId);
    	if (nsTemplateId == null) throw new MalformattedElementException("The NsTemplate ID is null");
    	
    	NsTemplateInfo nstInfo = getNsTemplateInfo(nsTemplateId);
    	

		nstInfoRepository.delete(nstInfo);
		log.debug("Removed NsTemplate info from DB.");
		NST nst = getNsTemplate(nsTemplateId);
		nstRepository.delete(nst);
		log.debug("Removed NsTemplate from DB.");
    }
    


	private String storeNsTemplate(NST nst) throws AlreadyExistingEntityException {
		String nstVersion =nst.getNstVersion();
		String nstTargetName =nst.getNstName();
		String nstTargetId = nst.getNstId();
		log.debug("Onboarding NsTemplate with name " + nstTargetName + " and version " + nstVersion);

		if (nstRepository.findByNstNameAndNstVersion(nstTargetName, nstVersion).isPresent()
				|| nstRepository.findByNstId(nstTargetId).isPresent()) {
			String logErrorStr= "NsTemplate with name " + nstTargetName + " and version " + nstVersion + " or ID already present in DB.";
			log.error(logErrorStr);
			throw new AlreadyExistingEntityException(logErrorStr);
		}

		NST target = new NST(nstTargetId,
				nstTargetName,
				nstVersion,
				nst.getNstProvider(),
				nst.getNsstIds(),
				nst.getNsdId(),
				nst.getNsdVersion(),
				nst.getNstServiceProfile());
		nstRepository.saveAndFlush(target);
    	log.debug("Added NsTemplate with ID " + nstTargetId);
    	
    	NsTemplateInfo nstInfo = new NsTemplateInfo(UUID.randomUUID().toString(), nstTargetId, nstTargetName, nstVersion, nst.getNstProvider(), target,null, null,false);
		nstInfoRepository.saveAndFlush(nstInfo);
		
		log.debug("Added NsTemplate Info with NsTemplate ID " + nstTargetId);
    	return nstTargetId;
    }
}
