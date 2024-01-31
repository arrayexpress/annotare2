package com.google.gwt.user.cellview.client;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class TextAreaEditCell extends EditTextCell {
    @Override
    public void onBrowserEvent(Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
        super.onBrowserEvent(context, parent, value, event, valueUpdater);
    }

    @Override
    public void render(Context context, String value, SafeHtmlBuilder sb) {
        sb.appendHtmlConstant("<textarea type=\"text\" tabindex=\"-1\" style=\"width:98%;max-width:98%;\">");
        if (value != null) {
            sb.appendEscaped(value);
        }
        sb.appendHtmlConstant("</textarea>");
    }
}
