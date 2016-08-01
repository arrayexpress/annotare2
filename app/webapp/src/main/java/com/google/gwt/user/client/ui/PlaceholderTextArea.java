package com.google.gwt.user.client.ui;

public class PlaceholderTextArea extends TextArea  {
    String placeholder = "";

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        getElement().setAttribute("placeholder", new HTML( this.placeholder).getText());
    }
}