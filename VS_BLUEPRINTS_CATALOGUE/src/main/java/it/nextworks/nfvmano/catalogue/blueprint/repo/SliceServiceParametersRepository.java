package it.nextworks.nfvmano.catalogue.blueprint.repo;

import it.nextworks.nfvmano.catalogue.blueprint.elements.ServiceConstraints;
import it.nextworks.nfvmano.catalogue.blueprint.elements.SliceServiceParameters;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SliceServiceParametersRepository extends JpaRepository<SliceServiceParameters, Long> {

}
