package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.activity.experiment;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentDetailsDto;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExtractAttributesRow;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.SingleCellExtractAttributesRow;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.place.ExpDesignPlace;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.proxy.ExperimentDataProxy;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design.ExtractAttributesView;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.design.SingleCellExtractAttributesView;

import java.util.List;

public class SingleCellExtractAttributesActivity extends AbstractActivity implements SingleCellExtractAttributesView.Presenter {

    private final SingleCellExtractAttributesView view;
    private final ExperimentDataProxy expData;

    @Inject
    public SingleCellExtractAttributesActivity(SingleCellExtractAttributesView view,
                                     ExperimentDataProxy expData) {
        this.view = view;
        this.expData = expData;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        view.setPresenter(this);
        panel.setWidget(view);
        loadAsync();

    }

    public SingleCellExtractAttributesActivity withPlace(ExpDesignPlace designPlace) {
        return this;
    }

    @Override
    public void updateRow(SingleCellExtractAttributesRow row) {
        expData.updateSingleCellExtractAttributeRow(row);
    }

    private void loadAsync() {
        expData.getDetailsAsync(
                new ReportingAsyncCallback<ExperimentDetailsDto>(ReportingAsyncCallback.FailureMessage.UNABLE_TO_LOAD_SUBMISSION_DETAILS) {
                    @Override
                    public void onSuccess(ExperimentDetailsDto result) {
                        view.setAeExperimentType(result.getAeExperimentType());
                    }
                }
        );

        expData.getSingleCellExtractAttributeRowsAsync(
                new ReportingAsyncCallback<List<SingleCellExtractAttributesRow>>(ReportingAsyncCallback.FailureMessage.UNABLE_TO_LOAD_EXTRACT_ATTRIBUTES) {
                    @Override
                    public void onSuccess(List<SingleCellExtractAttributesRow> result) {
                        view.setData(result);
                    }
                }
        );
    }
}
