package it.nextworks.nfvmano.catalogue.blueprint.repo;

import it.nextworks.nfvmano.catalogue.blueprint.elements.VsdNsdTranslationRule;
import it.nextworks.nfvmano.catalogue.blueprint.elements.VsdNstTranslationRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NstTranslationRuleRepository  extends JpaRepository<VsdNstTranslationRule, Long> {

    List<VsdNstTranslationRule> findByBlueprintId(String vsbId);

}

