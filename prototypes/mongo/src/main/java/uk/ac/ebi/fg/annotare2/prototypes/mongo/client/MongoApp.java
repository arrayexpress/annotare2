package uk.ac.ebi.fg.annotare2.prototypes.mongo.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Olga Melnichuk
 */
public class MongoApp  implements EntryPoint {

    interface Binder extends UiBinder<Widget, MongoApp> {
    }

    private static final Binder binder = GWT.create(Binder.class);

    @Override
    public void onModuleLoad() {
        loadModule(RootLayoutPanel.get());
    }

    public void loadModule(final HasWidgets hasWidgets) {
        hasWidgets.add(binder.createAndBindUi(this));
    }
}