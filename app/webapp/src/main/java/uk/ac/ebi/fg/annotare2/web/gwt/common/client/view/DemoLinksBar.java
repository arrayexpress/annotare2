/*
 * Copyright 2025
 */
package uk.ac.ebi.fg.annotare2.web.gwt.common.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Window;

import java.util.List;

/**
 * Reusable horizontal bar to show configurable demo/help links.
 */
public class DemoLinksBar extends Composite {

    interface Binder extends UiBinder<Widget, DemoLinksBar> {
        Binder BINDER = GWT.create(Binder.class);
    }

    public static class LinkItem {
        private final String label;      // non-clickable label shown before URL
        private final String url;        // href target
        private final String anchorText; // clickable text for the URL

        public LinkItem(String label, String url) {
            this(label, url, url);
        }

        public LinkItem(String label, String url, String anchorText) {
            this.label = label;
            this.url = url;
            this.anchorText = anchorText;
        }

        public String getLabel() {
            return label;
        }

        public String getUrl() {
            return url;
        }

        public String getAnchorText() {
            return anchorText;
        }
    }

    @UiField
    FlowPanel container;

    public DemoLinksBar() {
        initWidget(Binder.BINDER.createAndBindUi(this));
    }

    public void setLinks(List<LinkItem> items) {
        container.clear();
        if (items == null) {
            return;
        }
        // Count valid items
        new java.util.ArrayList<LinkItem>();
        int validCount = 0;
        for (LinkItem it : items) {
            if (it != null && it.getUrl() != null && !it.getUrl().trim().isEmpty()) {
                validCount++;
            }
        }
        if (validCount > 1) {
            // Render label and icon once, then a dropdown
            LinkItem first = null;
            for (LinkItem it : items) {
                if (it != null && it.getUrl() != null && !it.getUrl().trim().isEmpty()) { first = it; break; }
            }
            String labelText = first != null && first.getLabel() != null ? first.getLabel() : "Video Guide:";
            InlineLabel lbl = new InlineLabel(labelText);
            lbl.addStyleName("demo-links-label");
            container.add(lbl);
            InlineHTML icon = new InlineHTML("<i class=\"fa fa-youtube-play\" aria-hidden=\"true\"></i>");
            icon.addStyleName("demo-links-icon");
            container.add(icon);
            InlineLabel sp = new InlineLabel(" ");
            container.add(sp);

            final ListBox list = new ListBox();
            list.addStyleName("demo-links-dropdown");
            list.addItem("Select a video...", "");
            for (LinkItem it : items) {
                if (it == null || it.getUrl() == null || it.getUrl().trim().isEmpty()) continue;
                String text = it.getAnchorText() == null ? it.getUrl() : it.getAnchorText();
                list.addItem(text, it.getUrl());
            }
            list.addChangeHandler(new ChangeHandler() {
                @Override
                public void onChange(ChangeEvent event) {
                    int idx = list.getSelectedIndex();
                    String value = list.getValue(idx);
                    if (value != null && !value.isEmpty()) {
                        Window.open(value, "_blank", "");
                        list.setSelectedIndex(0);
                    }
                }
            });
            container.add(list);
            return;
        }
        // Single link case: render as label + anchor
        for (LinkItem item : items) {
            if (item == null || item.getUrl() == null || item.getUrl().trim().isEmpty()) {
                continue;
            }
            if (item.getLabel() != null && !item.getLabel().trim().isEmpty()) {
                InlineLabel lbl = new InlineLabel(item.getLabel());
                lbl.addStyleName("demo-links-label");
                container.add(lbl);
                InlineHTML icon = new InlineHTML("<i class=\"fa fa-youtube-play\" aria-hidden=\"true\"></i>");
                icon.addStyleName("demo-links-icon");
                container.add(icon);
                InlineLabel sp = new InlineLabel(" ");
                container.add(sp);
            }
            Anchor a = new Anchor(item.getAnchorText() == null ? item.getUrl() : item.getAnchorText(), item.getUrl(), "_blank");
            a.addStyleName("demo-links-anchor");
            container.add(a);
        }
    }

    public void clearLinks() {
        container.clear();
    }
}
