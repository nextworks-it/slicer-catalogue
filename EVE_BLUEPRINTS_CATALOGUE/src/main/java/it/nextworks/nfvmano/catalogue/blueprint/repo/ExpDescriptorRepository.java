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
package it.nextworks.nfvmano.catalogue.blueprint.repo;

import it.nextworks.nfvmano.catalogue.blueprint.elements.ExpDescriptor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExpDescriptorRepository extends JpaRepository<ExpDescriptor, Long> {

	List<ExpDescriptor> findByTenantId(String tenantId);
	Optional<ExpDescriptor> findByExpDescriptorId(String vsdId);
	Optional<ExpDescriptor> findByExpDescriptorIdAndTenantId(String vsdId, String tenantId);
	List<ExpDescriptor> findByIsPublic(boolean isPublic);
	Optional<ExpDescriptor> findByNameAndVersionAndTenantId(String name, String version, String tenantId);
	
}
