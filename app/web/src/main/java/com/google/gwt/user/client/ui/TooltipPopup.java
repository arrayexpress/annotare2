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

package com.google.gwt.user.client.ui;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.*;

// http://martinivanov.net/2010/11/25/creating-a-speech-bubble-with-css3-and-without-additional-markup/
// and http://mrcoles.com/blog/callout-box-css-border-triangles-cross-browser/

public class TooltipPopup {

    private final PopupPanel tooltipPanel;
    private final HTML tooltipContents;

    public TooltipPopup() {
        tooltipPanel = createPanel();
        tooltipContents = new HTML("<div/>");
        tooltipPanel.setWidget(tooltipContents);
    }

    protected PopupPanel createPanel() {
        PopupPanel p = new PopupPanel(true, false);
        p.setStyleName("gwt-TooltipPopup");
        PopupPanel.setStyleName(p.getContainerElement(), "callout");
        p.setPreviewingAllNativeEvents(true);
        p.setAnimationType(PopupPanel.AnimationType.ROLL_DOWN);
        return p;
    }

    public void showTip(final Element element, String html) {
        tooltipContents.setHTML(html + "<b class=\"border-notch notch\"></b><b class=\"notch\"></b>");

        Scheduler.get().scheduleFixedPeriod(new Scheduler.RepeatingCommand() {
            public boolean execute() {
                // Show the popup under the input element
                tooltipPanel.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
                    public void setPosition(int offsetWidth, int offsetHeight) {
                        tooltipPanel.setPopupPosition(element.getAbsoluteLeft(),
                                element.getAbsoluteBottom());
//                        tooltipPanel.getElement().getStyle().setProperty("minWidth",
//                                (element.getAbsoluteRight() - element.getAbsoluteLeft())
//                                        + Style.Unit.PX.getType());
                    }
                });
                return false;
            }
        }, 500);

    }

    public void hideTip() {
        tooltipPanel.hide();
    }

    private final static TooltipPopup popupInstance = new TooltipPopup();

    public static void attachTooltip(HasAllFocusHandlers focusTarget, final Element elementTarget, final String html) {

        focusTarget.addFocusHandler(new FocusHandler() {
            @Override
            public void onFocus(FocusEvent event) {
                popupInstance.showTip(elementTarget, html);
            }
        });
        focusTarget.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                popupInstance.hideTip();
            }
        });
    }
}
