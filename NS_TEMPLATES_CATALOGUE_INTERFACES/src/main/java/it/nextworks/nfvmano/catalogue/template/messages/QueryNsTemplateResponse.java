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

package it.nextworks.nfvmano.catalogue.template.messages;

import it.nextworks.nfvmano.catalogue.template.elements.NsTemplateInfo;
import it.nextworks.nfvmano.libs.ifa.common.InterfaceMessage;
import it.nextworks.nfvmano.libs.ifa.common.exceptions.MalformattedElementException;

import java.util.ArrayList;
import java.util.List;

public class QueryNsTemplateResponse implements InterfaceMessage {

    private List<NsTemplateInfo> nsTemplateInfo = new ArrayList<>();

    /**
     * @return the nsTemplateInfo
     */
    public List<NsTemplateInfo> getNsTemplateInfo() {
        return nsTemplateInfo;
    }

    @Override
    public void isValid() throws MalformattedElementException {

    }
}
