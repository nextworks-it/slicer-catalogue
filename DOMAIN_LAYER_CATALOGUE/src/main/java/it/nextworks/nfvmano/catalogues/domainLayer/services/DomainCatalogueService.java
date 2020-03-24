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

package it.nextworks.nfvmano.catalogues.domainLayer.services;


import it.nextworks.nfvmano.catalogue.domainLayer.Domain;
import it.nextworks.nfvmano.catalogue.domainLayer.interfaces.DomainCatalogueInterface;
import it.nextworks.nfvmano.catalogues.domainLayer.repo.DomainRepository;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.AlreadyExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.NotExistingEntityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class DomainCatalogueService implements DomainCatalogueInterface {

    private static final Logger log = LoggerFactory.getLogger(DomainCatalogueService.class);

    @Autowired
    private DomainRepository domainRepository;

    
    public DomainCatalogueService() { }


	public Domain getDomain(String domainId) throws NotExistingEntityException {
		Domain domain;

		if(domainRepository.findByDomainId(domainId).isPresent()){
			domain= domainRepository.findByDomainId(domainId).get();
		}
		else {
			throw new NotExistingEntityException("Domain with ID " + domainId + " not found in DB.");
		}
		return domain;
	}


	public void deleteDomain(String domainId) throws NotExistingEntityException {
		log.info("Received request to delete a domain from DB");
		if(domainRepository.findByDomainId(domainId).isPresent()){
			domainRepository.delete(domainRepository.findByDomainId(domainId).get());
		}
		else {
			throw new NotExistingEntityException("Domain layer with ID " + domainId + " not found in DB.");
		}
	}
    
	public List<Domain> getAllDomains() {
		List<Domain> domains = domainRepository.findAll();
		return domains;
	}

	public List<Domain> getDomainsByName(String name) {
		List<Domain> domains = domainRepository.findDomainByName(name);
		return domains;
	}


	public Long onBoardDomain(Domain domain) throws MalformattedElementException, AlreadyExistingEntityException {
    	domain.isValid();
		if(domainRepository.findByDomainId(domain.getDomainId()).isPresent()){
			throw new AlreadyExistingEntityException("Domain with name " + domain.getName() + " and description " +domain.getDescription()+ " already available into DB.");
		}
		Domain domainTarget = domainRepository.saveAndFlush(domain);
		domainTarget.setDomainId(String.valueOf(domainTarget.getId()));
		domainRepository.saveAndFlush(domainTarget);
		return domainTarget.getId();
	}
}
