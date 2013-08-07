package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.EventBus;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ValidationResult;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DockBarEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.DockBarEventHandler;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.ValidationFinishedEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.ValidationFinishedEventHandler;

/**
 * @author Olga Melnichuk
 */
public class ExperimentLayout extends Composite implements EditorLayout {

    private static final int DEFAULT_OPEN_LOGBAR_SIZE = 250;

    private static final int MIN_DOCK_SIZE = 21;

    @UiField
    ScrollPanel logBarDisplay;

    @UiField
    DockLayoutPanel dockPanel;

    @UiField
    SimpleLayoutPanel contentDisplay;

    @UiField
    SplitLayoutPanel verticalSplitPanel;

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

    private double dockSize;

    interface Binder extends UiBinder<Widget, ExperimentLayout> {
        Binder BINDER = GWT.create(Binder.class);
    }

    public ExperimentLayout(EventBus eventBus) {
        initWidget(Binder.BINDER.createAndBindUi(this));
        verticalSplitPanel.setWidgetMinSize(dockPanel, MIN_DOCK_SIZE);

        eventBus.addHandler(DockBarEvent.getType(), new DockBarEventHandler() {
            @Override
            public void onFileUploadToggle() {
                toggleDockPanel();
            }
        });
        eventBus.addHandler(ValidationFinishedEvent.TYPE, new ValidationFinishedEventHandler() {
            @Override
            public void validationFinished(ValidationResult result) {
                expandLogBar(DEFAULT_OPEN_LOGBAR_SIZE);
            }
        });
    }

    private void expandLogBar(double size) {
        Widget w = splitPanel.getWidget(0);
        double widgetSize = splitPanel.getWidgetSize(w);
        if (widgetSize < size) {
            splitPanel.setWidgetSize(w, size);
        }
    }

    private void toggleDockPanel() {
        double widgetSize = verticalSplitPanel.getWidgetSize(dockPanel);
        double newSize;
        if (widgetSize <= MIN_DOCK_SIZE + 1.0) {
            double defaultWidth = (verticalSplitPanel.getOffsetWidth() - MIN_DOCK_SIZE)/2;
            newSize = dockSize < MIN_DOCK_SIZE + 10.0 ?  defaultWidth : dockSize;
        } else {
            dockSize = widgetSize;
            newSize = MIN_DOCK_SIZE;
        }
        verticalSplitPanel.setWidgetSize(dockPanel, newSize);
    }

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
