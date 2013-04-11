package uk.ac.ebi.fg.annotare2.prototypes.rf.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.web.bindery.requestfactory.shared.RequestFactory;
import uk.ac.ebi.fg.annotare2.prototypes.rf.client.shared.TestRequestFactory;

/**
 * @author Olga Melnichuk
 */
public class TestApp implements EntryPoint {

    @UiField
    Button changeButton;

    interface Binder extends UiBinder<DockLayoutPanel, TestApp> {
        static final Binder BINDER = GWT.create(Binder.class);
    }

    private RequestFactory requestFactory;

    public void onModuleLoad() {
        loadModule(RootLayoutPanel.get());
    }

    public void loadModule(final HasWidgets hasWidgets) {
        hasWidgets.add(Binder.BINDER.createAndBindUi(this));

        final EventBus eventBus = new SimpleEventBus();
        requestFactory = GWT.create(TestRequestFactory.class);
        requestFactory.initialize(eventBus);
    }
}
