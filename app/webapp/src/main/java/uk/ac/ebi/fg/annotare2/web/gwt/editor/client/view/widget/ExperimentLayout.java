package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.EventBus;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ValidationResult;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.ValidationFinishedEvent;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.event.ValidationFinishedEventHandler;

/**
 * @author Olga Melnichuk
 */
public class ExperimentLayout extends Composite implements EditorLayout, RequiresResize {

    private static final int DEFAULT_LOG_PANEL_SIZE = 250;

    @UiField
    ScrollPanel logBarDisplay;

    @UiField
    SimpleLayoutPanel contentDisplay;

    @UiField
    MinimizableScrollPanel leftMenuDisplay;

    @UiField
    SplitLayoutPanel splitPanel;

    @UiField
    SimpleLayoutPanel topBarDisplay;

    @UiField
    SimpleLayoutPanel titleBarDisplay;

    @UiField
    SimpleLayoutPanel tabBarDisplay;

    @UiField
    NotificationMole notificationMole;

    private int submissionCount;

    interface Binder extends UiBinder<Widget, ExperimentLayout> {
        Binder BINDER = GWT.create(Binder.class);
    }

    public ExperimentLayout(EventBus eventBus, int submissionCount) {
        this.submissionCount = submissionCount;
        initWidget(Binder.BINDER.createAndBindUi(this));
        eventBus.addHandler(ValidationFinishedEvent.TYPE, new ValidationFinishedEventHandler() {
            @Override
            public void validationFinished(ValidationResult result) {
                openLogPanel(DEFAULT_LOG_PANEL_SIZE);
            }
        });
        notificationMole.addAttachHandler(new AttachEvent.Handler() {
            @Override
            public void onAttachOrDetach(AttachEvent attachEvent) {
                showNotificationMole();
            }
        });

    }

    private void showNotificationMole() {
        if (submissionCount==1) {
            notificationMole.setAnimationDuration(500);
            notificationMole.showDelayed(1000);
            com.google.gwt.user.client.Timer t = new com.google.gwt.user.client.Timer() {
                @Override
                public void run() {
                    notificationMole.hide();
                }
            };
            t.schedule(6000);
        }
    }

    private void openLogPanel(double size) {
        Widget w = splitPanel.getWidget(0);
        double widgetSize = splitPanel.getWidgetSize(w);
        if (widgetSize < size) {
            splitPanel.setWidgetSize(w, size);
        }
    }

    @Override
    public HasOneWidget getTopBarDisplay() {
        return topBarDisplay;
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
    public void onResize() {
        if (getWidget() instanceof RequiresResize) {
            ((RequiresResize) getWidget()).onResize();
        }
    }
}
