package com.google.gwt.user.client.ui;

public class ResizableTextArea extends TextArea implements RequiresResize {

    @Override
    public void onResize() {
        int height = getOffsetHeight();
        int width = getOffsetWidth();
        setSize(width+"px", height+"px");
    }
}