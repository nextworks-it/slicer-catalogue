/*
* Copyright 2018 Nextworks s.r.l.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package it.nextworks.nfvmano.catalogue.translator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.NotExistingEntityException;

@RestController
@CrossOrigin
@RequestMapping("/portal/catalogue/translator")
public class TranslatorRestController {
	
	private static final Logger log = LoggerFactory.getLogger(TranslatorRestController.class);

	@Autowired
	private TranslatorService translatorService;
	
	public TranslatorRestController() {	}
	
	@RequestMapping(value = "/expd/{expdId}", method = RequestMethod.GET)
	public ResponseEntity<?> getNfvNsInstantiationInfo(@PathVariable String expdId) {
		log.debug("Received request to retrieve NFV NS instantiation info from experiment descriptor with ID " + expdId);
		try {
			NfvNsInstantiationInfo result = translatorService.translateExpd(expdId);
			return new ResponseEntity<NfvNsInstantiationInfo>(result, HttpStatus.OK);
		} catch (MalformattedElementException e) {
			log.error("Malformatted request");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (NotExistingEntityException e) {
			log.error("Experiment Descriptor not found");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("Internal exception");
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
