package uk.ac.ebi.fg.annotare2.prototypes.layoutapp.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

import java.util.Date;

import static java.util.Arrays.asList;

/**
 * @author Olga Melnichuk
 */
public class ExpSubmissionList extends Composite {

    interface Binder extends UiBinder<Widget, ExpSubmissionList> {
    }

    @UiField(provided = true)
    CellTable<SubmissionInfo> cellTable;

    @UiField(provided = true)
    SimplePager pager;

    public ExpSubmissionList() {
        cellTable = new CellTable<SubmissionInfo>();
        cellTable.setWidth("100%", true);
        cellTable.addColumn(new TextColumn<SubmissionInfo>() {
            @Override
            public String getValue(SubmissionInfo object) {
                return object.getName();
            }
        });

        cellTable.addColumn(new TextColumn<SubmissionInfo>() {
            @Override
            public String getValue(SubmissionInfo object) {
                return object.getCreated().toString();
            }
        });

        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        pager = new SimplePager(SimplePager.TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(cellTable);

        ListDataProvider<SubmissionInfo> dataProvider = new ListDataProvider<SubmissionInfo>();
        dataProvider.addDataDisplay(cellTable);

        dataProvider.setList(asList(
                new SubmissionInfo("Submission1", new Date(), "not ready"),
                new SubmissionInfo("Submission1", new Date(), "not ready"),
                new SubmissionInfo("Submission1", new Date(), "not ready")
                ));
        // Create the UiBinder.
        Binder uiBinder = GWT.create(Binder.class);
        Widget widget = uiBinder.createAndBindUi(this);

        initWidget(widget);

    }


}
