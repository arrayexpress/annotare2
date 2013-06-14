/*
 * Copyright 2009-2013 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or impl
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.web.gwt.editor.client.view.experiment.setup;

import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import uk.ac.ebi.fg.annotare2.web.gwt.common.shared.exepriment.ExperimentSetupSettings;

import static uk.ac.ebi.fg.annotare2.configmodel.ExperimentConfigType.SEQUENCING;


/**
 * @author Olga Melnichuk
 */
public class HighThroughputSeqSettings extends Composite implements HasSubmissionSettings {

    public HighThroughputSeqSettings() {
        initWidget(new HTML(
                SafeHtmlUtils.fromSafeConstant("An example is " +
                        "<a target='_blank' href='http://www.ebi.ac.uk/arrayexpress/experiments/E-MTAB-582/'>E-MTAB-582</a>, " +
                        "<a target='_blank' href='http://europepmc.org/abstract/MED/21983088/reload=0;jsessionid=tcoeGSkyb7AXGQXpR7vz.6'>" +
                        "Europe PMC 21983088</a>. Sequencing experiments produce raw sequence data " +
                        "generated by next-generation sequencing platforms such as Illumina " +
                        "Genome Analyzer/HiSeq, 454 and ABI SOLiD.")));
    }

    @Override
    public ExperimentSetupSettings getSettings() {
        return new ExperimentSetupSettings.Builder()
                .setExperimentType(SEQUENCING)
                .build();
    }
}
