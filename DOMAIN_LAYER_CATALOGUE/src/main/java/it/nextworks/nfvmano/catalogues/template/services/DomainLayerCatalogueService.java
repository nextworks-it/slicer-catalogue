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

import it.nextworks.nfvmano.catalogue.domainLayer.interfaces.DomainLayerCatalogueInterface;
import it.nextworks.nfvmano.catalogue.template.elements.DomainLayer;
import it.nextworks.nfvmano.catalogues.template.repo.DomainLayerRepository;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.AlreadyExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.NotExistingEntityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DomainLayerCatalogueService implements DomainLayerCatalogueInterface {

    private static final Logger log = LoggerFactory.getLogger(DomainLayerCatalogueService.class);

    @Autowired
    private DomainLayerRepository domainLayerRepository;

    
    public DomainLayerCatalogueService() { }


	public DomainLayer getDomainLayer(Long domainLayerId) throws NotExistingEntityException {
		DomainLayer domainLayer;

		if(domainLayerRepository.findById(domainLayerId).isPresent()){
			domainLayer= domainLayerRepository.findById(domainLayerId).get();
		}
		else {
			throw new NotExistingEntityException("Domain layer with ID " + domainLayerId + " not found in DB.");
		}
		return domainLayer;
	}


	public void deleteDomainLayer(Long domainLayerId) throws NotExistingEntityException {
		log.info("Received request to delete a domain layer from DB");
		if(domainLayerRepository.findById(domainLayerId).isPresent()){
			domainLayerRepository.delete(domainLayerRepository.findById(domainLayerId).get());
		}
		else {
			throw new NotExistingEntityException("Domain layer with ID " + domainLayerId + " not found in DB.");
		}
	}
    
	public List<DomainLayer> getAllDomainLayers() {
		List<DomainLayer> domainLayers = domainLayerRepository.findAll();
		return domainLayers;
	}


	public Long onBoardDomainLayer(DomainLayer domainLayer) throws MalformattedElementException, AlreadyExistingEntityException {
    	domainLayer.isValid();
		if(domainLayerRepository.findByNameAndDescription(domainLayer.getName(), domainLayer.getDescription()).isPresent()){
			throw new AlreadyExistingEntityException("Domain layer with name " + domainLayer.getName() + "and description " +domainLayer.getDescription()+ "already available into DB.");
		}
		for(Long idDomainLayerAssociated: domainLayer.getDomainLayersIdAssociated()){
			if(!domainLayerRepository.findById(idDomainLayerAssociated).isPresent()){
				log.info("The domain layer going to be associated with ID "+domainLayerRepository+" is not present into DB");
				throw new AlreadyExistingEntityException("The domain layer associated with ID "+domainLayerRepository+" is not present into DB");
			}
		}
		DomainLayer verticalDomainLayerTarget = domainLayerRepository.saveAndFlush(domainLayer);
		return verticalDomainLayerTarget.getId();
	}
}
