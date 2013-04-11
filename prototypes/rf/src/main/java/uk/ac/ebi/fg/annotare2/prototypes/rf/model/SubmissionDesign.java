package uk.ac.ebi.fg.annotare2.prototypes.rf.model;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Olga Melnichuk
 */
public class SubmissionDesign {

    private final List<Sample> samples = new ArrayList<Sample>();
    private final List<ResultFile> files = new ArrayList<ResultFile>();

    public List<ResultFile> getFiles() {
        return ImmutableList.copyOf(files);
    }

    public List<Sample> getSamples() {
        return ImmutableList.copyOf(samples);
    }

    public void addSample(Sample sample) {
        samples.add(sample);
    }

    public void addFile(ResultFile file) {
        files.add(file);
    }
}
