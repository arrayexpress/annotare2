package uk.ac.ebi.fg.annotare2.prototypes.editorapp.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.*;

/**
 * @author Olga Melnichuk
 */
public class EditorApp implements EntryPoint {

    interface Binder extends UiBinder<DockLayoutPanel, EditorApp> {
    }

    private static final Binder binder = GWT.create(Binder.class);

    @UiField
    SimpleLayoutPanel content;

    @UiField
    AppNavBar navBar;

    public void onModuleLoad() {
        loadModule(RootLayoutPanel.get());
    }

    public void loadModule(final HasWidgets hasWidgets) {
        DockLayoutPanel hPanel = binder.createAndBindUi(this);

        navBar.addItem("About", new Command() {
             public void execute() {
                 setContent(new IdfView());
             }
         });
        navBar.addItem("Experiment Design", new Command() {
            public void execute() {
                setContent(new SdrfView());
            }
        });
        navBar.addItem("Vocabularies", new Command() {
            public void execute() {
                setContent(new SimplePanel());
            }
        });
        navBar.addItem("Data", new Command() {
            public void execute() {
                setContent(new SimplePanel());
            }
        });
        navBar.addItem("Import", new Command() {
            public void execute() {
                setContent(new SimplePanel());
            }
        });
        navBar.addItem("Export", new Command() {
            public void execute() {
                setContent(new SimplePanel());
            }
        });

        hasWidgets.add(hPanel);

        showNewSubmissionPopup();
    }

    private void setContent(Widget w) {
        content.setWidget(w);
    }

    private void showNewSubmissionPopup() {
        SubmissionCreateDialog dialog = new SubmissionCreateDialog();
        dialog.addCloseHandler(new CloseHandler<PopupPanel>() {
            public void onClose(CloseEvent<PopupPanel> event) {
                navBar.select(1);
            }
        });
        dialog.show();
    }
}