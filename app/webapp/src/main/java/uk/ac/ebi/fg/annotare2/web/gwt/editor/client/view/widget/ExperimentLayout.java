package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.EventBus;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.view.NotificationPopupPanel;
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
    SimpleLayoutPanel fileUploadDisplay;

    @UiField
    Button showHideButton;

    @UiField
    SplitLayoutPanel fileUploadPanel;

    @UiField
    SimpleLayoutPanel simpleToggleButtonPanel;

    @UiField
    DockLayoutPanel dockLayoutPanel;

    private int submissionCount;
    private boolean filePanelIsVisible;

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
        //showHideButton.setHTML("<i class=\"fa fa-chevron-circle-right\"/>");
        filePanelIsVisible = true;
        contentDisplay.addAttachHandler(new AttachEvent.Handler() {
            @Override
            public void onAttachOrDetach(AttachEvent attachEvent) {
                showNotificationMole();
            }
        });
        dockLayoutPanel.setWidgetSize (tabBarDisplay,4);
        showHideButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {

                if(filePanelIsVisible)
                {
                    showHideButton.setHTML("<i class=\"fa fa-download\" aria-hidden=\"true\"></i> File Upload ");
                    Widget w = fileUploadPanel.getWidget(0);
                    fileUploadPanel.setWidgetHidden(w,true);
                }
                else
                {
                    showHideButton.setHTML("<i class=\"fa fa-upload\" aria-hidden=\"true\"></i> File Upload ");
                    Widget w = fileUploadPanel.getWidget(0);
                    fileUploadPanel.setWidgetHidden(w,false);
                }
                filePanelIsVisible = !filePanelIsVisible;
            }
        });
    }

    private void showNotificationMole() {
        if (submissionCount==1) {
            NotificationPopupPanel.message ("Annotare automatically saves any changes that you make; Simply move the cursor to a different field!", true);
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
    public HasOneWidget getFileUploadDisplay() { return fileUploadDisplay; }

    @Override
    public void onResize() {
        if (getWidget() instanceof RequiresResize) {
            ((RequiresResize) getWidget()).onResize();
        }
    }
}
