package uk.ac.ebi.fg.annotare2.prototypes.layoutapp.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Olga Melnichuk
 */
public class MySubmissionsHome extends Composite {

    interface Binder extends UiBinder<Widget, MySubmissionsHome> {
    }

    @UiField
    Button newSubmissionBtn;

    @UiField
    SubmissionList submissionList;

    @UiHandler("newSubmissionBtn")
    protected void newExperimentClick(ClickEvent event) {
        Window.alert("Create new experiment");
    }

    public MySubmissionsHome() {
        Binder uiBinder = GWT.create(Binder.class);
        Widget widget = uiBinder.createAndBindUi(this);
        initWidget(widget);
    }
}
