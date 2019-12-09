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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import it.nextworks.nfvmano.catalogue.blueprint.repo.CtxDescriptorRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.ExpDescriptorRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.TranslationRuleRepository;
import it.nextworks.nfvmano.catalogue.blueprint.repo.VsDescriptorRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.nextworks.nfvmano.libs.ifa.common.exceptions.FailedOperationException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MethodNotImplementedException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.NotExistingEntityException;
import it.nextworks.nfvmano.catalogue.blueprint.elements.CtxDescriptor;
import it.nextworks.nfvmano.catalogue.blueprint.elements.ExpDescriptor;
import it.nextworks.nfvmano.catalogue.blueprint.elements.VsDescriptor;
import it.nextworks.nfvmano.catalogue.blueprint.elements.VsdNsdTranslationRule;

public class BasicTranslator extends AbstractTranslator {
	
	private static final Logger log = LoggerFactory.getLogger(BasicTranslator.class);
	
	private TranslationRuleRepository ruleRepo;

	public BasicTranslator(VsDescriptorRepository vsdRepo,
							ExpDescriptorRepository expDescriptorRepository,
							CtxDescriptorRepository ctxDescriptorRepository,
						    TranslationRuleRepository ruleRepo) {
		super(TranslatorType.BASIC_TRANSLATOR, vsdRepo, expDescriptorRepository, ctxDescriptorRepository);
		this.ruleRepo = ruleRepo;
	}
	
	@Override
	public Map<String, NfvNsInstantiationInfo> translateVsd(List<String> vsdIds)
			throws FailedOperationException, NotExistingEntityException, MethodNotImplementedException {
		log.debug("VSD->NSD translation at basic translator.");
		if (vsdIds.size() > 1) throw new FailedOperationException("Processing of multiple VSDs not supported by the basic translator");
		Map<String, NfvNsInstantiationInfo> nfvNsInfo = new HashMap<>();
		Map<String, VsDescriptor> vsds = retrieveVsDescriptors(vsdIds);
		for (Map.Entry<String, VsDescriptor> entry : vsds.entrySet()) {
			String vsdId = entry.getKey();
			VsDescriptor vsd = entry.getValue();
			VsdNsdTranslationRule rule = findMatchingTranslationRule(vsd);
			NfvNsInstantiationInfo info = new NfvNsInstantiationInfo(rule.getNsdId(), rule.getNsdVersion(), rule.getNsFlavourId(), rule.getNsInstantiationLevelId(), null);
			nfvNsInfo.put(vsdId, info);
			log.debug("Added NS instantiation info for VSD " + vsdId + " - NSD ID: " + rule.getNsdId() + " - NSD version: " + rule.getNsdVersion() + " - DF ID: " 
					+ rule.getNsFlavourId() + " - IL ID: " + rule.getNsInstantiationLevelId());
		}
		return nfvNsInfo;
	}
	
	@Override
	public NfvNsInstantiationInfo translateExpd(String expdId)
			throws MalformattedElementException, FailedOperationException, NotExistingEntityException, MethodNotImplementedException {
		log.debug("ExpD->NSD translation at basic translator.");
		if (expdId == null) throw new MalformattedElementException("Received null expdId as input to the basic translator");
		Optional<ExpDescriptor> expdOpt = expDescriptorRepository.findByExpDescriptorId(expdId);
		if (expdOpt.isPresent()) {
			ExpDescriptor expd = expdOpt.get();
			VsdNsdTranslationRule rule = findMatchingTranslationRule(expd);
			return new NfvNsInstantiationInfo(rule.getNsdId(), rule.getNsdVersion(), rule.getNsFlavourId(), rule.getNsInstantiationLevelId(), null);
		} else {
			log.error("Experiment descriptor " + expdId + " not found in DB.");
			throw new NotExistingEntityException("Experiment descriptor " + expdId + " not found in DB.");
		}
	}
	
	private VsdNsdTranslationRule findMatchingTranslationRule(ExpDescriptor expd) throws FailedOperationException, NotExistingEntityException {
		String blueprintId = expd.getExpBlueprintId();
		Map<String, String> descriptorParameters = new HashMap<>();
		// for the experiment descriptor the parameters are defined in the related VSD and CTXD
		String vsdId = expd.getVsDescriptorId();
		Optional<VsDescriptor> vsdOpt = vsdRepo.findByVsDescriptorId(vsdId);
		if (vsdOpt.isPresent()) {
			Map<String, String> vsdParameters = vsdOpt.get().getQosParameters();
			descriptorParameters.putAll(vsdParameters);
		}
		
		List<String> ctxDescriptorIds = expd.getCtxDescriptorIds();
		for (String ctxId: ctxDescriptorIds) {
			Optional<CtxDescriptor> ctxOpt = ctxDescriptorRepository.findByCtxDescriptorId(ctxId);
			if (ctxOpt.isPresent()) {
				Map<String, String> ctxParameters = ctxOpt.get().getCtxParameters();
				descriptorParameters.putAll(ctxParameters);
			}
		}
		return findMatchingTranslationRule(blueprintId, descriptorParameters);
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
