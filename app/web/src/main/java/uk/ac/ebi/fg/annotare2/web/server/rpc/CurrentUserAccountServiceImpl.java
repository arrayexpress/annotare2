/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.server.rpc;

import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.CurrentUserAccountService;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.dto.UserDto;
import uk.ac.ebi.fg.annotare2.web.server.services.AccountService;
import uk.ac.ebi.fg.annotare2.web.server.services.EmailSender;
import uk.ac.ebi.fg.annotare2.web.server.transaction.Transactional;

import static uk.ac.ebi.fg.annotare2.web.server.rpc.transform.UIObjectConverter.uiUser;

/**
 * @author Olga Melnichuk
 */
public class CurrentUserAccountServiceImpl extends AuthBasedRemoteService implements CurrentUserAccountService {

    @Inject
    public CurrentUserAccountServiceImpl(AccountService accountService, EmailSender emailSender) {
        super(accountService, emailSender);
    }

    @Transactional
    @Override
    public UserDto me() {
        return uiUser(getCurrentUser());
    }
}
