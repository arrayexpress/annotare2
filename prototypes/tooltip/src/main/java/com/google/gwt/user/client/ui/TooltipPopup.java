package com.google.gwt.user.client.ui;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;

// http://martinivanov.net/2010/11/25/creating-a-speech-bubble-with-css3-and-without-additional-markup/
// and http://mrcoles.com/blog/callout-box-css-border-triangles-cross-browser/

public class TooltipPopup {

    private final PopupPanel tooltipPanel;
    private final HTMLPanel tooltipContents;

    public TooltipPopup() {
        tooltipPanel = createPanel();
        tooltipContents = new HTMLPanel("This is a sample tooltip text to display, and display and display.<b class=\"notch\"></b>");
        tooltipPanel.setWidget(tooltipContents);
    }

    protected PopupPanel createPanel() {
        PopupPanel p = new PopupPanel(false, false);
        p.setStyleName("gwt-TooltipPopup");
        PopupPanel.setStyleName(p.getContainerElement(), "callout");
        p.setPreviewingAllNativeEvents(true);
        p.setAnimationType(PopupPanel.AnimationType.ROLL_DOWN);
        return p;
    }

    public void showTip(final Element element) {

        // Show the popup under the input element
        tooltipPanel.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            public void setPosition(int offsetWidth, int offsetHeight) {
                tooltipPanel.setPopupPosition(element.getAbsoluteLeft(),
                        element.getAbsoluteBottom());
                tooltipPanel.getElement().getStyle().setProperty("minWidth",
                        (element.getAbsoluteRight() - element.getAbsoluteLeft())
                                + Style.Unit.PX.getType());
            }
        });
    }

    public void hideTip() {
        tooltipPanel.hide();
    }
}
