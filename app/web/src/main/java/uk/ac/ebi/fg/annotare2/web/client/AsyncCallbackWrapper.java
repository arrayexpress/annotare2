/*
 * Copyright 2009-2012 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;

/**
 * @author Olga Melnichuk
 */
public abstract class AsyncCallbackWrapper<T> implements AsyncCallback<T> {
    public AsyncCallback<T> wrap() {
        return new AsyncCallback<T>() {
            public void onFailure(Throwable caught) {
                if (isUnauthorizedRequestError(caught)) {
                    Window.Location.reload();
                } else {
                    AsyncCallbackWrapper.this.onFailure(caught);
                }
            }

            public void onSuccess(T result) {
                AsyncCallbackWrapper.this.onSuccess(result);
            }

            private boolean isUnauthorizedRequestError(Throwable caught) {
                return (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 401);
            }
        };
    }
}
