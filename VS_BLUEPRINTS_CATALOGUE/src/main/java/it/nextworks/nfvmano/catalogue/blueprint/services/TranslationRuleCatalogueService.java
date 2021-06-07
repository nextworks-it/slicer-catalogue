package it.nextworks.nfvmano.catalogue.blueprint.services;

import it.nextworks.nfvmano.catalogue.blueprint.elements.VsBlueprint;
import it.nextworks.nfvmano.catalogue.blueprint.elements.VsdNsdTranslationRule;
import it.nextworks.nfvmano.catalogue.blueprint.elements.VsdParameterValueRange;
import it.nextworks.nfvmano.catalogue.blueprint.elements.VsBlueprintParameter;
import it.nextworks.nfvmano.catalogue.blueprint.interfaces.TranslationRuleCatalogueInterface;
import it.nextworks.nfvmano.catalogue.blueprint.messages.OnBoardTranslationRuleRequest;
import it.nextworks.nfvmano.catalogue.blueprint.messages.QueryTranslationRuleResponse;
import it.nextworks.nfvmano.catalogue.blueprint.repo.VsBlueprintRepository;
import it.nextworks.nfvmano.libs.ifa.common.elements.Filter;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.AlreadyExistingEntityException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.FailedOperationException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MethodNotImplementedException;
import it.nextworks.nfvmano.libs.ifa.common.messages.GeneralizedQueryRequest;
import it.nextworks.nfvmano.catalogue.blueprint.repo.TranslationRuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TranslationRuleCatalogueService implements TranslationRuleCatalogueInterface {

    private static final Logger log = LoggerFactory.getLogger(TranslationRuleCatalogueService.class);

    //Added to be able to retrieve the Vsb service slice type and category
    @Autowired
    private VsBlueprintRepository vsBlueprintRepository;

    @Autowired
    private TranslationRuleRepository translationRuleRepository;

    @Override
    public synchronized List<String> onBoardTranslationRule(OnBoardTranslationRuleRequest request) throws MalformattedElementException, AlreadyExistingEntityException {
        log.debug("Processing request to onboard new translation rules.");
        request.isValid();
        List<String> ruleIdList = new ArrayList<>();
        for (VsdNsdTranslationRule vsdNsdTranslationRule : request.getTranslationRules()) {
            //check if the blueprint id exixts in db
            String vsbId = vsdNsdTranslationRule.getBlueprintId();
            Optional<VsBlueprint> vsBlueprint = vsBlueprintRepository.findByBlueprintId(vsbId);
            if (vsBlueprint.isPresent()) {
                //check if the translation rule to onboard has valid configurable parameters
                if (areParametersValid(vsBlueprint, vsdNsdTranslationRule.getInput())) {
                    //check if the translation rule already exists in db
                    List<VsdNsdTranslationRule> trs = translationRuleRepository.findByBlueprintId(vsbId);
                    if (!isRuleAlreadyPresent(trs, vsdNsdTranslationRule)) {
                        translationRuleRepository.saveAndFlush(vsdNsdTranslationRule);
                        ruleIdList.add(vsdNsdTranslationRule.getId().toString());
                    }
                    else log.debug("Skipping this translation rule. Already present.");
                } else log.debug("Cannot onboard a translation rule that contains wrong input.");
            } else
                log.debug("Internal error: VsBlueprint " + vsbId + " doesn't exists in db. This translation rule will be skipped.");
        }
        log.debug("Translation rules saved in internal DB.");
        return ruleIdList;
    }

    private boolean isRuleAlreadyPresent(List<VsdNsdTranslationRule> trs, VsdNsdTranslationRule vsdNsdTranslationRule) {
        List<VsdParameterValueRange> vsdParameterValueRangeList = vsdNsdTranslationRule.getInput();
        for (VsdNsdTranslationRule vsd : trs) {
            if (vsd.getNsInstantiationLevelId().equals(vsdNsdTranslationRule.getNsInstantiationLevelId())
                    && vsd.getBlueprintId().equals(vsdNsdTranslationRule.getBlueprintId())
                    && vsd.matchesNstId(vsdNsdTranslationRule.getNstId())) {
                //checking parameter values
                if (areParametersAlreadyPresent(vsd.getInput(), vsdParameterValueRangeList))
                    //same translation rule
                    return true;
                else return false;
            }
        }
        return false;
    }

    private boolean areParametersAlreadyPresent(List<VsdParameterValueRange> input, List<VsdParameterValueRange> vsdParameterValueRangeList) {
        if (vsdParameterValueRangeList.size() == input.size()) {
            for (VsdParameterValueRange valueRange : vsdParameterValueRangeList) {
                if (!input.contains(valueRange))
                    //they are different
                    return false;
            }
            //same value range parameters
            return true;
        }
        return false;
    }

    @Override
    public QueryTranslationRuleResponse queryTranslationRule(GeneralizedQueryRequest request)
            throws MethodNotImplementedException, MalformattedElementException, FailedOperationException {
        log.debug("Processing request to query Translation Rule");
        request.isValid();

        //At the moment the only filters accepted are:
        //1. VS Blueprint ID
        //VSB_ID
        
        List<VsdNsdTranslationRule> vsdNsdTranslationRules;

        Filter filter = request.getFilter();
        List<String> attributeSelector = request.getAttributeSelector();
        if ((attributeSelector == null) || (attributeSelector.isEmpty())) {
            Map<String, String> fp = filter.getParameters();
            if (fp.size()==2 && fp.containsKey("TENANT_ID") && fp.containsKey("VSB_ID")) {
                String vsbId = fp.get("VSB_ID");
                vsdNsdTranslationRules = translationRuleRepository.findByBlueprintId(vsbId);
                log.debug("Retrieved translation rules of " + vsbId);
            } /*else if (fp.size() == 2 && fp.containsKey("VSB_NAME") && fp.containsKey("VSB_VERSION")) {
                String vsbName = fp.get("VSB_NAME");
                String vsbVersion = fp.get("VSB_VERSION");
                Optional<VsBlueprint> vsb = vsBlueprintRepository.findByNameAndVersion(vsbName, vsbVersion);
                if (!vsb.isPresent()) {
                    log.error("Cannot retrieve VsBlueprint from the specified name and version: " + vsbName + " " + vsbVersion);
                    throw new FailedOperationException();
                }
                vsdNsdTranslationRules = translationRuleRepository.findByBlueprintId(vsb.get().getBlueprintId());
                log.debug("Updated translation rule for VSB " + vsbName + " with version " + vsbVersion);*/
            else {
                vsdNsdTranslationRules = translationRuleRepository.findAll();
                log.debug("Retrieved all translation rules available in DB.");
            }
            return new QueryTranslationRuleResponse(vsdNsdTranslationRules);
        } else {
            log.error("Received query Translation Rule with attribute selector. Not supported at the moment.");
            throw new MethodNotImplementedException("Received query Translation Rule with attribute selector. Not supported at the moment.");
        }
    }

    @Override
    public void updateTranslationRule(OnBoardTranslationRuleRequest request) throws MalformattedElementException {
        log.debug("Processing request to update translation rules.");
        request.isValid();
        List<VsdNsdTranslationRule> newVsdRules = request.getTranslationRules();
        List<VsdNsdTranslationRule> vsdNsdTranslationRules = translationRuleRepository.findAll();
        for (VsdNsdTranslationRule newVsdNsdTranslationRule : newVsdRules) {
            for (VsdNsdTranslationRule vsdNsdTranslationRule : vsdNsdTranslationRules) {
                //check if blueprint id and nsIlId are the same
                if (vsdNsdTranslationRule.getBlueprintId().equals(newVsdNsdTranslationRule.getBlueprintId())
                        && vsdNsdTranslationRule.getNsInstantiationLevelId().equals(newVsdNsdTranslationRule.getNsInstantiationLevelId())) {
                    //need to perform update
                    String vsbId = newVsdNsdTranslationRule.getBlueprintId();
                    Optional<VsBlueprint> vsBlueprint = vsBlueprintRepository.findByBlueprintId(vsbId);
                    //check if the new translation rule contains valid parameters the are present also
                    //in the vs blueprint (like "users" etc...)
                    if (areParametersValid(vsBlueprint, newVsdNsdTranslationRule.getInput())) {
                        //the new translation rule contains valid parameters
                        translationRuleRepository.delete(vsdNsdTranslationRule);
                        translationRuleRepository.saveAndFlush(newVsdNsdTranslationRule);
                    } else log.debug("Cannot update a translation rule that contains wrong parameters.");
                    break;
                }
            }
        }
        log.debug("Finished to update translation rules.");
    }

    public String updateTranslationRuleWithId(VsdNsdTranslationRule rule, String ruleId) throws MalformattedElementException {
        log.debug("Processing request to update translation rule.");
        List<VsdNsdTranslationRule> vsdNsdTranslationRules = translationRuleRepository.findAll();
        for (VsdNsdTranslationRule vsdNsdTranslationRule : vsdNsdTranslationRules) {
            //check if this ruleID is the same of ruleId
            if (vsdNsdTranslationRule.getId().toString().equals(ruleId)) {
                //need to perform update
                String vsbId = rule.getBlueprintId();
                Optional<VsBlueprint> vsBlueprint = vsBlueprintRepository.findByBlueprintId(vsbId);
                //check if the new translation rule contains valid parameters the are present also
                //in the vs blueprint (like "users" etc...)
                if (areParametersValid(vsBlueprint, rule.getInput())) {
                    //the new translation rule contains valid parameters
                    translationRuleRepository.delete(vsdNsdTranslationRule);
                    translationRuleRepository.saveAndFlush(rule);
                    return rule.getId().toString();

                } else log.debug("Cannot update a translation rule that contains wrong parameters.");
                break;
            }
        }
        log.debug("Finished to update translation rules.");
        return null;
    }

    private boolean areParametersValid(Optional<VsBlueprint> vsBlueprint, List<VsdParameterValueRange> input) {
        List<VsBlueprintParameter> blueprintParameters = vsBlueprint.get().getParameters();
        List<String> parameters = new ArrayList<>();
        for (VsBlueprintParameter vsBlueprintParameter : blueprintParameters) {
            parameters.add(vsBlueprintParameter.getParameterId());
        }
        for (VsdParameterValueRange vsdParameterValueRange : input) {
            if (!parameters.contains(vsdParameterValueRange.getParameterId()))
                return false;
        }
        return true;
    }

    @Override
    public void deleteTranslationRule(String ruleId) throws FailedOperationException {
        if (ruleId == null) {
            log.error("Rule ID is null");
            throw new FailedOperationException();
        }
        log.debug("Processing request to delete translation rule " + ruleId);
        List<VsdNsdTranslationRule> vsdNsdTranslationRuleList = translationRuleRepository.findAll();
        for (VsdNsdTranslationRule vsdNsdTranslationRule : vsdNsdTranslationRuleList) {
            if (vsdNsdTranslationRule.getId().toString().equals(ruleId)) {
                translationRuleRepository.delete(vsdNsdTranslationRule);
                log.debug("Translation rule removed from DB.");
                return;
            }
        }
        log.debug("Traslation rule "+ ruleId + " not in internal DB.");
        /*if (ruleId != null) {
            log.debug("Processing request to delete a translation rule of blueprint with ID " + vsBlueprintId);

            for (VsdNsdTranslationRule vsdNsdTranslationRule : vsdNsdTranslationRuleList) {
                if (vsdNsdTranslationRule.getNsdInfoId().equals(ruleId)) {
                    translationRuleRepository.delete(vsdNsdTranslationRule);
                    break;
                }
            }
            log.debug("Removed the translation rule " +  ruleId +" for blueprint " + vsBlueprintId + " from DB.");
        } else {
            log.debug("Processing request to delete all translation rules of blueprint with ID " + vsBlueprintId);

            for (VsdNsdTranslationRule vsdNsdTranslationRule : vsdNsdTranslationRuleList) {
                translationRuleRepository.delete(vsdNsdTranslationRule);
            }

            log.debug("Removed all translation rules for blueprint " + vsBlueprintId + " from DB.");
        }*/
    }
}

