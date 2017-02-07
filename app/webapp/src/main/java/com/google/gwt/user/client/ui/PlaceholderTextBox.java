package com.google.gwt.user.client.ui;

/**
 * Created by haideri on 06/02/2017.
 */
public class PlaceholderTextBox extends TextBox {
    String placeholder = "";

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        getElement().setAttribute("placeholder", new HTML( this.placeholder).getText());
    }
}
