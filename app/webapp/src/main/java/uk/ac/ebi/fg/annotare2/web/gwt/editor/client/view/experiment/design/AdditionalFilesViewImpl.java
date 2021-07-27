package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import uk.ac.ebi.fg.annotare2.submission.model.FileRef;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataAssignmentColumn;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataAssignmentRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.DataFileRow;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static uk.ac.ebi.fg.annotare2.web.gwt.editor.client.EditorUtils.getSubmissionId;

public class AdditionalFilesViewImpl extends Composite implements AdditionalFilesView, RequiresResize {

    private Binder uiBinder = GWT.create(Binder.class);

    private final List<String> fileTypeheaders = new ArrayList<>();
    private final List<Object[]> dataRows = new ArrayList<>();
    private final List<DataFileRow> files = new ArrayList<>();

    interface Binder extends UiBinder<Widget, AdditionalFilesViewImpl> {}

    @UiField
    ScrollPanel additionalFilesPanel;

    public AdditionalFilesViewImpl(){
        initWidget(uiBinder.createAndBindUi(this));
        additionalFilesPanel.getElement().setId("react_app_id");
    }

    public void setDataFiles(List<DataFileRow> files) {
        this.files.addAll(files);
    }

    @Override
    public void setData(List<DataAssignmentColumn> columns, List<DataAssignmentRow> rows) {
        fileTypeheaders.addAll(columns.stream().map(col -> col.getType().getTitle()).collect(Collectors.toList()));
        rows.forEach(row -> {
            List<String> dataRow = new ArrayList<>();
            dataRow.add(row.getName());
            dataRow.add(row.getLabeledExtractId());
            dataRow.addAll(columns.stream().map(col -> {
                FileRef fileref = col.getFileRef(row);
                return fileref != null ? fileref.getName() : "";
            }).collect(Collectors.toList()));
            dataRows.add(dataRow.toArray());
        });
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        renderComponent("react_app_id");

    }

    @Override
    protected void onUnload() {
        super.onUnload();
        confirmAssignments();
    }

    @Override
    public void loadData(){
        submissionIdChangeHandler(getSubmissionId());
        columneHeadersChangeHandler(fileTypeheaders.toArray());
        dataRowsChangeHandler(dataRows.toArray());
        dataFilesChangeHandler(files.toArray());
    }

    @JsMethod(namespace = JsPackage.GLOBAL)
    public static native void renderComponent(String divId);

    @JsMethod(namespace = JsPackage.GLOBAL)
    public static native void submissionIdChangeHandler(int id);

    @JsMethod(namespace = JsPackage.GLOBAL)
    public static native void columneHeadersChangeHandler(Object[] headers);

    @JsMethod(namespace = JsPackage.GLOBAL)
    public static native void dataRowsChangeHandler(Object[] dataRows);

    @JsMethod(namespace = JsPackage.GLOBAL)
    public static native void dataFilesChangeHandler(Object[] files);

    @JsMethod(namespace = JsPackage.GLOBAL)
    public static native void confirmAssignments();

    @Override
    public void onResize() {
        if (getWidget() instanceof RequiresResize) {
            ((RequiresResize) getWidget()).onResize();
        }
    }
}

