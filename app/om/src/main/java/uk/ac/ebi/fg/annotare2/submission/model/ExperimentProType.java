package uk.ac.ebi.fg.annotare2.submission.model;

import java.io.Serializable;

public abstract class ExperimentProType implements Serializable {

    protected String title;

    public ExperimentProType(){
        // GWT Serialisation
    }

   public ExperimentProType(String title){
       this.title = title;
   }

   public boolean isMicroarray() {
        return title.equalsIgnoreCase("One-color microarray") || isTwoColorMicroarray();
   }

    public boolean isPlantMicroarray() {
        return title.equalsIgnoreCase("Plant - One-color microarray") || isPlantTwoColorMicroarray();
    }

    public boolean isTwoColorMicroarray() {
        return title.equalsIgnoreCase("Two-color microArray");
    }

    public boolean isPlantTwoColorMicroarray() { return title.equalsIgnoreCase("Two-color microArray"); }

    public boolean isSequencing() {
        return title.equalsIgnoreCase("High-throughput sequencing");
    }

    public boolean isPlantSequncing() {return title.equalsIgnoreCase("Plant - High-throughput sequencing");}


    public abstract String getTitle();
}
