package it.nextworks.nfvmano.catalogue.blueprint.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.nextworks.nfvmano.catalogue.blueprint.elements.VsdNsdTranslationRule;
import it.nextworks.nfvmano.libs.ifa.common.InterfaceMessage;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;

import java.util.ArrayList;
import java.util.List;

public class OnBoardTranslationRuleRequest implements InterfaceMessage {

    List<VsdNsdTranslationRule> vsdNsdTranslationRuleList = new ArrayList<>();

    public List<VsdNsdTranslationRule> getVsdNsdTranslationRuleList() {
        return vsdNsdTranslationRuleList;
    }

    public void setVsdNsdTranslationRuleList(List<VsdNsdTranslationRule> vsdNsdTranslationRuleList) {
        this.vsdNsdTranslationRuleList = vsdNsdTranslationRuleList;
    }

    public OnBoardTranslationRuleRequest() {}

    /**
     * Constructor
     *
     * @param translationRules
     */
    public OnBoardTranslationRuleRequest(List<VsdNsdTranslationRule> translationRules) {
        if (translationRules != null) vsdNsdTranslationRuleList = translationRules;
    }

    /**
     * @return the translationRules
     */
    public List<VsdNsdTranslationRule> getTranslationRules() {
        return vsdNsdTranslationRuleList;
    }

    @JsonIgnore
    public void setBlueprintIdInTranslationRules(String blueprintId) {
        for (VsdNsdTranslationRule tr : vsdNsdTranslationRuleList)
            tr.setBlueprintId(blueprintId);
    }

    @JsonIgnore
    public void setNsdInfoIdInTranslationRules(String nsdInfoId, String nsdId, String nsdVersion) {
        for (VsdNsdTranslationRule tr : vsdNsdTranslationRuleList) {
            if (tr.matchesNsdIdAndNsdVersion(nsdId, nsdVersion)) tr.setNsdInfoId(nsdInfoId);
        }
    }


    @Override
    public void isValid() throws MalformattedElementException {
        if(!vsdNsdTranslationRuleList.isEmpty()) for (VsdNsdTranslationRule tr : vsdNsdTranslationRuleList) tr.isValid();
    }
}