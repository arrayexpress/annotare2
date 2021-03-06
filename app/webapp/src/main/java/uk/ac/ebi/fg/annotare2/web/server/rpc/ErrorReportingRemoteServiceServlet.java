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

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.apache.catalina.connector.ClientAbortException;
import uk.ac.ebi.fg.annotare2.core.components.Messenger;

abstract class ErrorReportingRemoteServiceServlet extends RemoteServiceServlet {

    private final Messenger messenger;

    public ErrorReportingRemoteServiceServlet(Messenger messenger) {
        this.messenger = messenger;
    }

    @Override
    protected void doUnexpectedFailure(Throwable e) {
        if (!(e instanceof ClientAbortException)) {
            messenger.send("Unexpected exception in RPC call", e);
            super.doUnexpectedFailure(e);
        }
    }
}