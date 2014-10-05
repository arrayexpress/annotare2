package com.google.gwt.user.client.ui;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;

public class RootLayoutPanel extends LayoutPanel {

    private static RootLayoutPanel singleton;

    /**
     * Gets the singleton instance of RootLayoutPanel. This instance will always
     * be attached to the document body via {@link RootPanel#get()}.
     *
     * <p>
     * Note that, unlike {@link RootPanel#get(String)}, this class provides no way
     * to get an instance for any element on the page other than the document
     * body. This is because we know of no way to get resize events for anything
     * but the window.
     * </p>
     */
    public static RootLayoutPanel get() {
        if (singleton == null) {
            singleton = new RootLayoutPanel();
            RootPanel.get("content").add(singleton);
        }
        return singleton;
    }

    private RootLayoutPanel() {
        Window.addResizeHandler(new ResizeHandler() {
            public void onResize(ResizeEvent event) {
                RootLayoutPanel.this.onResize();
            }
        });

        // TODO(jgw): We need notification of font-size changes as well.
        // I believe there's a hidden iframe trick that we can use to get
        // a font-size-change event (really an em-definition-change event).
    }

    @Override
    protected void onLoad() {
        getLayout().onAttach();
        getLayout().fillParent();
    }
}
