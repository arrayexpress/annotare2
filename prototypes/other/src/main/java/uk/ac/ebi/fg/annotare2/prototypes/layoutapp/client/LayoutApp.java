package uk.ac.ebi.fg.annotare2.prototypes.layoutapp.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

/**
 * @author Olga Melnichuk
 */
public class LayoutApp implements EntryPoint {

    interface Binder extends UiBinder<DockPanel, LayoutApp> { }
    private static final Binder binder = GWT.create(Binder.class);

    @UiField ListBox experimentList;

    @UiField TabLayoutPanel tabPanel;

    public void onModuleLoad() {
        setUncaughtExceptionHandler();
        loadModule(RootPanel.get());
    }

    public void loadModule(final HasWidgets hasWidgets) {
   /*     ListBox experimentList = new ListBox(false);
        experimentList.addItem("All projects");
        experimentList.addItem("Project 2");
        experimentList.addItem("Project 3");
        experimentList.addItem("Project 4");

        HorizontalPanel hPanel = new HorizontalPanel();
        hPanel.add(experimentList);*/

        DockPanel hPanel = binder.createAndBindUi(this);
        experimentList.addItem("All my experiments");
        experimentList.addItem("Experiment 2");
        experimentList.addItem("Experiment 3");
        experimentList.addItem("Experiment 4");

        tabPanel.add(new MySubmissionsHome(), "Experiment submissions");
        tabPanel.add(new MySubmissionsHome(),  "ADF submissions");

        tabPanel.selectTab(0);

        hasWidgets.add(hPanel);
    }

    @UiHandler("experimentList")
    protected void projectSelect(ChangeEvent event) {
        Window.alert("you've selected " + experimentList.getValue(experimentList.getSelectedIndex()));
    }

    private void setUncaughtExceptionHandler() {
        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
            public void onUncaughtException(Throwable e) {
                e.printStackTrace();
                Throwable rootCause = getRootCause(e);
                Window.alert(rootCause.toString());
            }
        });
    }

    private Throwable getRootCause(Throwable e) {
        Throwable lastCause;
        do {
            lastCause = e;
        } while ((e = e.getCause()) != null);
        return lastCause;
    }
}
