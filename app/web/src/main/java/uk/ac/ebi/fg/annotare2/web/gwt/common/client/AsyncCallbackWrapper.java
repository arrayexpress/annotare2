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

package uk.ac.ebi.fg.annotare2.web.gwt.common.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;

/**
 * @author Olga Melnichuk
 */
public abstract class AsyncCallbackWrapper<T> implements AsyncCallback<T> {

    public void onPermissionDenied() {
        Window.alert("Sorry, you do not have permissions to proceed with this operation");
    }

    @Override
    public void onFailure(Throwable caught) {
        Window.alert("Server error; please try again later");
    }

    public AsyncCallback<T> wrap() {
        final AsyncCallbackWrapper<T> wrapper = this;

        return new AsyncCallback<T>() {
            public void onFailure(Throwable caught) {
                if (unableToConnect(caught) ||
                        nonAuthorizedRequest(caught)) {
                    Window.Location.reload();
                } else if (noPermission(caught)) {
                    wrapper.onPermissionDenied();
                } else {
                    wrapper.onFailure(caught);
                }
            }

            public void onSuccess(T result) {
                wrapper.onSuccess(result);
            }

            private boolean unableToConnect(Throwable caught) {
                return (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 0);
            }

            private boolean nonAuthorizedRequest(Throwable caught) {
                return (caught instanceof StatusCodeException && ((StatusCodeException) caught).getStatusCode() == 401);
            }

            private boolean noPermission(Throwable caught) {
                return caught instanceof NoPermissionException;
            }
        };
    }

    public static <T> AsyncCallback<T> wrap(final AsyncCallback<T> callback) {
        return new AsyncCallbackWrapper<T>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(T result) {
                callback.onSuccess(result);
            }
        }.wrap();
    }
}
