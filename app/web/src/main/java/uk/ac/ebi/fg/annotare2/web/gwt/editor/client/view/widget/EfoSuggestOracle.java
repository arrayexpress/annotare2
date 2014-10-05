package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.widget;

import com.google.gwt.user.client.ui.SuggestOracle;
import uk.ac.ebi.fg.annotare2.submission.model.OntologyTerm;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback;
import uk.ac.ebi.fg.annotare2.web.gwt.common.client.rpc.ReportingAsyncCallback.FailureMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class EfoSuggestOracle extends SuggestOracle {

    private final SuggestService<OntologyTerm> suggestService;

    public EfoSuggestOracle(SuggestService<OntologyTerm> suggestService) {
        this.suggestService = suggestService;
    }

    @Override
    public void requestSuggestions(final Request request, final Callback callback) {
        suggestService.suggest(request.getQuery(), request.getLimit(),
                new ReportingAsyncCallback<List<OntologyTerm>>(FailureMessage.UNABLE_TO_LOAD_EFO) {
                    @Override
                    public void onSuccess(List<OntologyTerm> result) {
                        callback.onSuggestionsReady(request, createResponse(result));
                    }
        });
    }

    private Response createResponse(List<OntologyTerm> result) {
        List<EfoTermSuggestion> suggestions = new ArrayList<EfoTermSuggestion>();
        for (OntologyTerm term : result) {
            suggestions.add(new EfoTermSuggestion(term));
        }
        return new Response(suggestions);
    }

    public static class EfoTermSuggestion implements SuggestOracle.Suggestion {
        private final OntologyTerm term;

        private EfoTermSuggestion(OntologyTerm term) {
            this.term = term;
        }

        public OntologyTerm getTerm() {
            return term;
        }

        @Override
        public String getDisplayString() {
            return term.getLabel() + " (" + term.getAccession().trim() + ")";
        }

        @Override
        public String getReplacementString() {
            return term.getLabel();
        }
    }
}
