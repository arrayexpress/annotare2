/*
 * Copyright 2009-2014 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.google.gwt.user.client.ui;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

public class ErrorPopupPanel extends PopupPanel {

    private int hideTimeout = 5;

    private ErrorPopupPanel() {
        super(true, false);
        setStyleName("gwt-ErrorPopup");
        PopupPanel.setStyleName(getContainerElement(), "");
        setAnimationEnabled(false);
    }

    private void showMessage(String message) {
        getContainerElement().setInnerHTML(message);
        setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            public void setPosition(int offsetWidth, int offsetHeight) {
                int left = (Window.getClientWidth() - offsetWidth) / 2;
                int top = 0;
                setPopupPosition(left, top);
            }
        });
        setAutoHide();

    }

    private void setAutoHide() {
        if (hideTimeout > 0) {
            Timer timer = new Timer() {
                @Override
                public void run() {
                    hide();
                }
            };
            timer.schedule(hideTimeout * 1000);
        }
    }

    private static ErrorPopupPanel instance = null;

    public static void message(String message) {
        if (null == instance) {
            instance = new ErrorPopupPanel();
        }
        cancel();
        instance.showMessage(message);
    }

    public static void cancel() {
        if (null != instance && instance.isAttached()) {
            instance.hide();
        }
    }
}
