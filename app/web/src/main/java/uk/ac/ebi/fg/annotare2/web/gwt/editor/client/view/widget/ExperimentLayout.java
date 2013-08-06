package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

/**
 * @author Olga Melnichuk
 */
public class ExperimentLayout extends Composite implements EditorLayout {

    @UiField
    ScrollPanel logBarDisplay;

    @UiField
    DockLayoutPanel dockPanel;

    @UiField
    SimpleLayoutPanel contentDisplay;

    @UiField
    SplitLayoutPanel verticalSplit;

    @UiField
    MinimizableScrollPanel leftMenuDisplay;

    @UiField
    SimpleLayoutPanel tabBarDisplay;

    @UiField
    SplitLayoutPanel splitPanel;

    @UiField
    SimpleLayoutPanel titleBarDisplay;

    @UiField
    SimplePanel dockBarDisplay;

    @UiField
    SimplePanel dockBarPanelDisplay;

    interface Binder extends UiBinder<Widget, ExperimentLayout> {
        Binder BINDER = GWT.create(Binder.class);
    }

    public ExperimentLayout() {
        initWidget(Binder.BINDER.createAndBindUi(this));
        verticalSplit.setWidgetMinSize(dockPanel, 21);
    }

   /* public void expandLogBar(double size) {
        Widget w = splitPanel.getWidget(0);
        double widgetSize = splitPanel.getWidgetSize(w);
        if (widgetSize < size) {
            splitPanel.setWidgetSize(w, size);
        }
    }*/

    @Override
    public HasOneWidget getTitleBarDisplay() {
        return titleBarDisplay;
    }

    @Override
    public HasOneWidget getTabBarDisplay() {
        return tabBarDisplay;
    }

    @Override
    public HasOneWidget getLeftMenuDisplay() {
        return leftMenuDisplay;
    }

    @Override
    public HasOneWidget getContentDisplay() {
        return contentDisplay;
    }

    @Override
    public HasOneWidget getLogBarDisplay() {
        return logBarDisplay;
    }

    @Override
    public HasOneWidget getDockBarDisplay() {
        return dockBarDisplay;
    }

    @Override
    public HasOneWidget getDockBarPanelDisplay() {
        return dockBarPanelDisplay;
    }
}
