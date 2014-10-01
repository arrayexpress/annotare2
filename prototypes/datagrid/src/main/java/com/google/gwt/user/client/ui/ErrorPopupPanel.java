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
    private int hideTimeout = 4;

    private static class NoClipResizeAnimation extends ResizeAnimation {
        private final PopupPanel panel;

        public NoClipResizeAnimation(PopupPanel panel) {
            super(panel);
            this.panel = panel;
        }
        @Override
        protected void onComplete() {
            super.onComplete();
            panel.getElement().getStyle().clearProperty("clip");
        }
    }

    public ErrorPopupPanel(String message) {
        super(true, false);
        setStyleName("gwt-ErrorPopup");
        PopupPanel.setStyleName(getContainerElement(), "");
        getContainerElement().setInnerHTML(message);
        setAnimationEnabled(true);
        setAnimationType(AnimationType.ROLL_DOWN);
        setAnimation(new NoClipResizeAnimation(this));
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
}
