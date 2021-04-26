package it.nextworks.nfvmano.catalogue.blueprint.messages;

import it.nextworks.nfvmano.catalogue.blueprint.elements.VsdNsdTranslationRule;
import it.nextworks.nfvmano.libs.ifa.common.InterfaceMessage;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;

import java.util.ArrayList;
import java.util.List;

public class QueryTranslationRuleResponse implements InterfaceMessage {

    private List<VsdNsdTranslationRule> vsdNsdTranslationRules = new ArrayList<>();

    public QueryTranslationRuleResponse() {	}

    public QueryTranslationRuleResponse(List<VsdNsdTranslationRule> vsdNsdTranslationRules) {
        if (vsdNsdTranslationRules != null) this.vsdNsdTranslationRules = vsdNsdTranslationRules;
    }

    /**
     * @return the Translation Rules
     */
    public List<VsdNsdTranslationRule> getVsdNsdTranslationRules() {
        return vsdNsdTranslationRules;
    }

    @Override
    public void isValid() throws MalformattedElementException { }
}
