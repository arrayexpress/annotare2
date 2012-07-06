package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

/**
 * @author Olga Melnichuk
 */
public class EditorApp implements EntryPoint {

    interface Binder extends UiBinder<DockLayoutPanel, EditorApp> {
    }

    private static final Binder binder = GWT.create(Binder.class);

    @UiField
    TabLayoutPanel tabPanel;

    public void onModuleLoad() {
        loadModule(RootLayoutPanel.get());
    }

    public void loadModule(final HasWidgets hasWidgets) {
        DockLayoutPanel hPanel = binder.createAndBindUi(this);

        tabPanel.add(new IdfView(), "IDF");
        tabPanel.add(new SimplePanel(), "SDRF");
        tabPanel.add(new SimplePanel(), "ADF");
        tabPanel.add(new SimplePanel(), "Data");

        tabPanel.selectTab(0);

        hasWidgets.add(hPanel);
    }
}