package com.google.gwt.user.client.ui;

import java.util.Iterator;

public class RadioGroup extends HTMLPanel implements HasWidgets {

    public RadioGroup(String html) {
        super(html);
    }

    public String getValue() {
        Iterator<Widget> iterator = iterator();
        while (iterator.hasNext())  {
            Widget w = iterator.next();
            if (w instanceof RadioButton) {
                if( ((RadioButton)w).getValue()) {
                    return ((RadioButton) w).getText();
                }
            }
        }
        return null;
    }
}