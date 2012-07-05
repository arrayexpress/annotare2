package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * @author Olga Melnichuk
 */
public class EditorApp implements EntryPoint {

    interface Binder extends UiBinder<DockLayoutPanel, EditorApp> {
    }

    private static final Binder binder = GWT.create(Binder.class);

    public void onModuleLoad() {
        loadModule(RootLayoutPanel.get());
    }

    public void loadModule(final HasWidgets hasWidgets) {
        DockLayoutPanel hPanel = binder.createAndBindUi(this);
/*
        tabPanel.add(, "IDF");
        tabPanel.add(, "SDRF");
        tabPanel.add(, "ADF");
        tabPanel.add(, "Data");

        tabPanel.selectTab(0);*/

        hasWidgets.add(hPanel);
    }
}