package uk.ac.ebi.fg.annotare2.web.gwt.common.model;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.rpc.IsSerializable;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProType;
import uk.ac.ebi.fg.annotare2.submission.model.ExperimentProfile;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.ExperimentSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentSetupSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.settings.Editor;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.settings.ExperimentSettingsPanel;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.setup.HasSubmissionSettings;
import uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.setup.SetupExpSubmissionView;
//import uk.ac.ebi.fg.annotare2.web.server.rpc.updates.ExperimentUpdater;

public abstract class ExpProfileType extends ExperimentProType implements IsSerializable {

   public ExpProfileType() {
   }

   protected interface Templates extends SafeHtmlTemplates {
      @SafeHtmlTemplates.Template("<div>{0}</div>")
      SafeHtml div(SafeHtml value);
   }

   protected static Templates templates = GWT.create(Templates.class);

   protected static String aeArrayLinkOrNone(String value) {
      return value == null || value.isEmpty() ? "none" : "<a href=\"http://www.ebi.ac.uk/arrayexpress/arrays/" + value + "\" target=\"_blank\">" + value + "</a>";
   }

   protected static String valueOrNone(String value) {
      return value == null || value.isEmpty() ? "none" : value;
   }

   public ExpProfileType(String title){
       super(title);
   }

   public abstract String getTitle();

   //public abstract ExperimentUpdater getExperimentUpdater(ExperimentProfile exp);

   public abstract HasSubmissionSettings getExperimentSettings(SetupExpSubmissionView view);

   public abstract Editor<ExperimentSettings> getExperimentSettingsEditor(ExperimentSettingsPanel panel);

   //public abstract ExperimentProfile setupExperiment(ExperimentSetupSettings settings);

   public abstract SafeHtml getSettingDetails(ExperimentSettings settings);
}
