/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
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

package uk.ac.ebi.fg.annotare2.web.gwt.common.client.view;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;

import java.util.logging.Level;
import java.util.logging.Logger;

public class NotificationPopupPanel extends PopupPanel {

    private final static Logger logger = Logger.getLogger("gwt.client.NotificationPopupPanel");

    public enum Type {
        INFO("info", "<i class=\"fa fa-info-circle\"></i>"),
        WARNING("warning", "<i class=\"fa fa-exclamation-circle\"></i>"),
        ERROR("error", "<i class=\"fa fa-minus-circle\"></i>"),
        FAILURE("failure", "<i class=\"fa fa-times-circle\"></i>");

        private final String styleName;
        private final String iconHtml;

        Type(String styleName, String iconHtml) {
            this.styleName = styleName;
            this.iconHtml = iconHtml;
        }

        public String getStyleName() {
            return styleName;
        }

        public String getIconHtml() {
            return iconHtml;
        }
    }

    private final Element iconElement, messageElement;
    private final Type type;
    private final boolean shouldAutoHide;

    private NotificationPopupPanel(Type type, boolean shouldAutoHide, boolean isModal) {
        super(!isModal, isModal);
        this.type = type;
        this.shouldAutoHide = shouldAutoHide;
        setStyleName("gwt-NotificationPopup");
        addStyleName(type.getStyleName());
        PopupPanel.setStyleName(getContainerElement(), "container");
        iconElement = Document.get().createDivElement();
        PopupPanel.setStyleName(iconElement, "icon");
        messageElement = Document.get().createDivElement();
        PopupPanel.setStyleName(messageElement, "message");
        getContainerElement().appendChild(iconElement);
        getContainerElement().appendChild(messageElement);
        setAnimationEnabled(false);
        if (isModal) {
            setGlassEnabled(true);
        }
    }

    private void showMessage(String message) {
        iconElement.setInnerHTML(type.getIconHtml());
        messageElement.setInnerHTML(message);
        setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            public void setPosition(int offsetWidth, int offsetHeight) {
                int left = (Window.getClientWidth() - offsetWidth) / 2;
                int top = 0;
                setPopupPosition(left, top);
            }
        });
        if (shouldAutoHide) {
            scheduleAutoHide();
        }
    }

    private void scheduleAutoHide() {
        Timer timer = new Timer() {
                @Override
                public void run() {
                    hide();
                }
            };
        timer.schedule(5000);
    }

    private static NotificationPopupPanel instance = null;

    public static void failure(String message, Throwable exception) {
        if (null != instance) {
            cancel();
        }
        logger.log(Level.SEVERE, message, exception);

        instance = new NotificationPopupPanel(Type.FAILURE, true, false);
        instance.showMessage(message);
    }

    public static void error(String message, boolean shouldAutoHide, boolean isModal) {
        if (null != instance) {
            cancel();
        }
        instance = new NotificationPopupPanel(Type.ERROR, shouldAutoHide, isModal);
        instance.showMessage(message);
    }

    public static void warning(String message, boolean shouldAutoHide, boolean isModal) {
        if (null != instance) {
            cancel();
        }
        instance = new NotificationPopupPanel(Type.WARNING, shouldAutoHide, isModal);
        instance.showMessage(message);
    }

    public static void message(String message, boolean shouldAutoHide) {
        if (null != instance) {
            cancel();
        }
        instance = new NotificationPopupPanel(Type.INFO, shouldAutoHide, false);
        instance.showMessage(message);
    }

    public static void cancel() {
        if (null != instance && instance.isAttached()) {
            instance.hide();
        }
    }
}
