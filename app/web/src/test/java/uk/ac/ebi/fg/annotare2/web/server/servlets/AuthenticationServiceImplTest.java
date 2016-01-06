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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.server.servlets;

import org.junit.Test;
import uk.ac.ebi.fg.annotare2.web.server.services.*;
import uk.ac.ebi.fg.annotare2.web.server.servlets.utils.FormParams;
import uk.ac.ebi.fg.annotare2.web.server.servlets.utils.ValidationErrors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * @author Olga Melnichuk
 */
public class AuthenticationServiceImplTest {

    @Test
    public void testExistedLogin() {
        final String name = "existed_user";
        final String password = "existed_password";

        AccountManager accMan = mockAccManager(name, password, true);
        EmailSender emailer = mockEmailer();
        HttpServletRequest request = mockRequest(name, password);

        try {
            AccountService accountService = new AccountServiceImpl(accMan, emailer);
            ValidationErrors errors = accountService.login(request);
            assertTrue(errors.isEmpty());
        } catch (AccountServiceException e) {
            fail("Existed user should be logged in");
        }
    }

    @Test
    public void testNonExistedLogin() {
        final String name = "non_existed_user";
        final String password = "non_existed_password";

        AccountManager accMan = mockAccManager(name, password, false);
        EmailSender emailer = mockEmailer();
        HttpServletRequest request = mockRequest(name, password);

        try {
            AccountService accountService = new AccountServiceImpl(accMan, emailer);
            accountService.login(request);
            fail("Non-existed user should not be logged in");
        } catch (AccountServiceException e) {
            //ok
        }
    }

    @Test
    public void testInvalidLogin() throws AccountServiceException {
        AccountService accountService = new AccountServiceImpl(null, null);

        HttpServletRequest request = mockRequest("user", null);
        ValidationErrors errors = accountService.login(request);
        assertFalse(errors.isEmpty());
        assertFalse(errors.getErrors(FormParams.PASSWORD_PARAM).isEmpty());

        request = mockRequest("user", "");
        errors = accountService.login(request);
        assertFalse(errors.isEmpty());
        assertFalse(errors.getErrors(FormParams.PASSWORD_PARAM).isEmpty());

        request = mockRequest(null, "password");
        errors = accountService.login(request);
        assertFalse(errors.isEmpty());
        assertFalse(errors.getErrors(FormParams.EMAIL_PARAM).isEmpty());

        request = mockRequest("", "password");
        errors = accountService.login(request);
        assertFalse(errors.isEmpty());
        assertFalse(errors.getErrors(FormParams.EMAIL_PARAM).isEmpty());
    }

    private AccountManager mockAccManager(String user, String password, boolean exists) {
        AccountManager accMan = createMock(AccountManager.class);
        expect(accMan.isValid(user, password)).andReturn(exists).once();
        expect(accMan.isEmailVerified(user)).andReturn(true).once();
        replay(accMan);
        return accMan;
    }

    private EmailSender mockEmailer() {
        EmailSender emailer = createMock(EmailSender.class);
        return emailer;
    }

    private HttpServletRequest mockRequest(String name, String password) {
        HttpSession session = createMock(HttpSession.class);
        session.setAttribute(isA(String.class), isA(Object.class));
        expectLastCall().anyTimes();

        HttpServletRequest request = createMock(HttpServletRequest.class);
        expect(request
                .getParameterValues(FormParams.EMAIL_PARAM))
                .andReturn(name == null ? null : new String[]{name}).anyTimes();

        expect(request
                .getParameterValues(FormParams.PASSWORD_PARAM))
                .andReturn(password == null ? null : new String[]{password}).anyTimes();

        expect(request
                .getParameterValues(FormParams.TOKEN_PARAM))
                .andReturn(null).anyTimes();

        expect(request.getSession()).andReturn(session).anyTimes();

        replay(request, session);
        return request;
    }

}
