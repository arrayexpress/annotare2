package com.google.gwt.user.cellview.client;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;

public abstract class ConditionalEditTextCell extends EditTextCell {

    interface NonEditableTemplate extends SafeHtmlTemplates {
        @Template("<div style=\"non-editable\">{0}</div>")

        SafeHtml div(String value);
    }

    private static NonEditableTemplate nonEditableTemplate;

    private final SafeHtmlRenderer<String> renderer;

    public ConditionalEditTextCell() {
        this(SimpleSafeHtmlRenderer.getInstance());
    }

    public ConditionalEditTextCell(SafeHtmlRenderer<String> renderer) {
        super(renderer);
        this.renderer = renderer;

        if (null == nonEditableTemplate) {
            nonEditableTemplate = GWT.create(NonEditableTemplate.class);
        }
    }

    public abstract boolean isEditable();

    @Override
    public void onBrowserEvent(Context context, Element parent, String value, NativeEvent event,
                               ValueUpdater<String> valueUpdater) {
        if (isEditable()) {
            super.onBrowserEvent(context, parent, value, event, valueUpdater);
        }
    }

    @Override
    public void render(Context context, String value, SafeHtmlBuilder sb) {
        if (isEditable()) {
            super.render(context, value, sb);
        } else {
            if (value != null && value.trim().length() > 0) {

                sb.append(nonEditableTemplate.div(renderer.render(value).asString()));
            } else {
                sb.append(nonEditableTemplate.div("\u00A0"));
            }
        }
    }
}
