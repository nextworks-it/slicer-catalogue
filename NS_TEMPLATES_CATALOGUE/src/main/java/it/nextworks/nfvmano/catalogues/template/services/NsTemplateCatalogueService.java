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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.nextworks.nfvmano.catalogue.template.elements.NsTemplateInfo;
import it.nextworks.nfvmano.catalogue.template.interfaces.NsTemplateCatalogueInterface;
import it.nextworks.nfvmano.catalogue.template.messages.OnBoardNsTemplateRequest;
import it.nextworks.nfvmano.catalogue.template.messages.QueryNsTemplateResponse;
import it.nextworks.nfvmano.catalogues.template.repo.NsTemplateInfoRepository;
import it.nextworks.nfvmano.catalogues.template.repo.NsTemplateRepository;
import it.nextworks.nfvmano.libs.ifa.common.elements.Filter;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.AlreadyExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.FailedOperationException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MethodNotImplementedException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.NotExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.messages.GeneralizedQueryRequest;
import it.nextworks.nfvmano.libs.ifa.templates.NST;

@Service
public class NsTemplateCatalogueService implements NsTemplateCatalogueInterface {

    private static final Logger log = LoggerFactory.getLogger(NsTemplateCatalogueService.class);

    /*@Autowired
    private NfvoCatalogueService nfvoCatalogueService;
    */
    
    @Autowired
    private NsTemplateInfoRepository nstInfoRepository;
    
    @Autowired
    private NsTemplateRepository nstRepository;


    
    public NsTemplateCatalogueService() { }

       
  
    @Override
    public synchronized String onBoardNsTemplate(OnBoardNsTemplateRequest request)
    		throws MethodNotImplementedException, MalformattedElementException, AlreadyExistingEntityException, FailedOperationException {
    	log.debug("Processing request to onboard a new NsTemplate");
    	request.isValid();
    	String nstId = storeNsTemplate(request.getNst());
    	return nstId;
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
    	String nstName =nst.getNstName();
    	log.debug("Onboarding NsTemplate with name " + nstName + " and version " + nstVersion);
 
    	if (nstRepository.findByNstNameAndNstVersion(nstName, nstVersion).isPresent()) {
    		String logErrorStr= "NsTemplate with name " + nstName + " and version " + nstVersion + " already present in DB.";
			log.error(logErrorStr);
			throw new AlreadyExistingEntityException(logErrorStr);
		}
    	
    	NST target = new NST(null, nstName, nstVersion, nst.getNstProvider(), nst.getNsstIds(), nst.getNsdId(), nst.getNsdVersion(), nst.getNstServiceProfile());
    	
    	String nstTargetName=nst.getNstName();
    	target.setNstName(nstTargetName);
    	
    	nstRepository.saveAndFlush(target);
    	String nstTargetID =String.valueOf(target.getId());
    	target.setNstId(nstTargetID);
    	nstRepository.saveAndFlush(target);
    	log.debug("Added NsTemplate with ID " + nstTargetID);
    	
    	NsTemplateInfo nstInfo = new NsTemplateInfo(null, nstTargetID, nstTargetName, nstVersion, nst.getNstProvider(), target,null, null,false);
		nstInfoRepository.saveAndFlush(nstInfo);
		
		Long idGenerated = nstInfo.getId();
		NsTemplateInfo nstInfoTarget = new NsTemplateInfo(null, nstTargetID, nstTargetName, nstVersion, nst.getNstProvider(), target,null, null,false);
		nstInfoTarget.setId(idGenerated);
		nstInfoRepository.saveAndFlush(nstInfoTarget);
		
		log.debug("Added NsTemplate Info with NsTemplate ID " + nstTargetID);
    	return nstTargetID;
    }
}
