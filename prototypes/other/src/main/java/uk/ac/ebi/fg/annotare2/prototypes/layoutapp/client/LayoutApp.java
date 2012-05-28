package uk.ac.ebi.fg.annotare2.prototypes.layoutapp.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;

/**
 * @author Olga Melnichuk
 */
public class LayoutApp implements EntryPoint {

    public void onModuleLoad() {
        setUncaughtExceptionHandler();
        loadModule(RootPanel.get());
    }

    public void loadModule(final HasWidgets hasWidgets) {
        ListBox projectList = new ListBox(false);
        projectList.addItem("All projects");
        projectList.addItem("Project 2");
        projectList.addItem("Project 3");
        projectList.addItem("Project 4");

        HorizontalPanel hPanel = new HorizontalPanel();
        hPanel.add(projectList);

        hasWidgets.add(hPanel);
    }

    private void setUncaughtExceptionHandler() {
        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
            public void onUncaughtException(Throwable e) {
                e.printStackTrace();
                Throwable rootCause = getRootCause(e);
                new AlertMessageBox("Exception", rootCause.toString()).show();
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
