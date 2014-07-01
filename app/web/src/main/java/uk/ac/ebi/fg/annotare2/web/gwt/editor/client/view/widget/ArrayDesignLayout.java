package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Olga Melnichuk
 */
public class ArrayDesignLayout extends Composite implements EditorLayout {

    @UiField
    SimpleLayoutPanel topBarDisplay;

    @UiField
    SimpleLayoutPanel titleBarDisplay;

    @UiField
    SimpleLayoutPanel tabBarDisplay;

    @UiField
    MinimizableScrollPanel leftMenuDisplay;

    @UiField
    SimpleLayoutPanel contentDisplay;

    interface Binder extends UiBinder<Widget, ArrayDesignLayout> {
        Binder BINDER = GWT.create(Binder.class);
    }

    public ArrayDesignLayout() {
        initWidget(Binder.BINDER.createAndBindUi(this));
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
        return null;
    }

    //@Override
    //public HasOneWidget getDockBarDisplay() {
    //    return null;
    //}

    //@Override
    //public HasOneWidget getDockBarPanelDisplay() {
    //    return null;
    //}
}
