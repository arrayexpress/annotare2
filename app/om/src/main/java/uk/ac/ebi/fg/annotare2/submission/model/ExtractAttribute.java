/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.submission.model;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author Olga Melnichuk
 */
public enum ExtractAttribute {
    LIBRARY_LAYOUT("Library Layout *",
            "Whether to expect SINGLE or PAIRED end reads.",
            "",
            "SINGLE",
            "PAIRED"),

    LIBRARY_SOURCE("Library Source *",
            "The type of source material that is being sequenced.",
            "",
            "GENOMIC (Genomic DNA (includes PCR products from genomic DNA))",
            "TRANSCRIPTOMIC (Transcription products or non genomic DNA (EST, cDNA, RT-PCR, screened libraries))",
            "METAGENOMIC (Mixed material from metagenome)",
            "METATRANSCRIPTOMIC (Transcription products from community targets)",
            "SYNTHETIC (Synthetic DNA)",
            "VIRAL RNA (Viral RNA)",
            "OTHER (Other, unspecified, or unknown library source material)"),

    LIBRARY_STRATEGY("Library Strategy *",
            "The sequencing technique intended for the library.",
            "",
            "WGS (Random sequencing of the whole genome)",
            "WGA (whole genome amplification to replace some instances of RANDOM)",
            "WXS (Random sequencing of exonic regions selected from the genome)",
            "RNA-Seq (Random sequencing of whole transcriptome)",
            "miRNA-Seq (for micro RNA and other small non-coding RNA sequencing)",
            "ncRNA-Seq(Non-coding RNA)",
            "WCS (Random sequencing of a whole chromosome or other replicon isolated from a genome)",
            "CLONE (Genomic clone based (hierarchical) sequencing)",
            "POOLCLONE (Shotgun of pooled clones (usually BACs and Fosmids))",
            "AMPLICON (Sequencing of overlapping or distinct PCR or RT-PCR products)",
            "CLONEEND (Clone end (5', 3', or both) sequencing)",
            "FINISHING (Sequencing intended to finish (close) gaps in existing coverage)",
            "ChIP-Seq (Direct sequencing of chromatin immunoprecipitates)",
            "MNase-Seq (Direct sequencing following MNase digestion)",
            "DNase-Hypersensitivity (Sequencing of hypersensitive sites, or segments of open chromatin that are more readily cleaved by DNaseI)",
            "Bisulfite-Seq (Sequencing following treatment of DNA with bisulfite to convert cytosine residues to uracil depending on methylation status)",
            "EST (Single pass sequencing of cDNA templates)",
            "FL-cDNA (Full-length sequencing of cDNA templates)",
            "CTS (Concatenated Tag Sequencing)",
            "MRE-Seq (Methylation-Sensitive Restriction Enzyme Sequencing strategy)",
            "MeDIP-Seq (Methylated DNA Immunoprecipitation Sequencing strategy)",
            "MBD-Seq (Direct sequencing of methylated fractions sequencing strategy)",
            "Tn-Seq (for gene fitness determination through transposon seeding)",
            "VALIDATION",
            "FAIRE-seq (Formaldehyde-Assisted Isolation of Regulatory Elements) ",
            "SELEX (Systematic Evolution of Ligands by EXponential enrichment (SELEX) is an in vitro strategy to analyze RNA sequences that perform an activity of interest, most commonly high affinity binding to a ligand)",
            "RIP-Seq (Direct sequencing of RNA immunoprecipitates (includes CLIP-Seq, HITS-CLIP and PAR-CLI))",
            "ChiA-PET (Direct sequencing of proximity-ligated chromatin immunoprecipitates)",
            "OTHER (Library strategy not listed)"),

    LIBRARY_SELECTION("Library Selection *",
            "The method used to select and/or enrich the material being sequenced.",
            "",
            "RANDOM (Random selection by shearing or other method)",
            "PCR (Source material was selected by designed primers)",
            "RANDOM PCR (Source material was selected by randomly generated primers)",
            "RT-PCR (Source material was selected by reverse transcription PCR)",
            "HMPR (Hypo-methylated partial restriction digest)",
            "MF (Methyl Filtrated)",
            "repeat fractionation (replaces: CF-S, CF-M, CF-H, CF-T)",
            "size fractionation",
            "MSLL (Methylation Spanning Linking Library)",
            "cDNA (complementary DNA)",
            "ChIP (Chromatin immunoprecipitation)",
            "MNase (Micrococcal Nuclease (MNase) digestion)",
            "DNAse (Deoxyribonuclease (MNase) digestion)",
            "Hybrid Selection (Selection by hybridization in array or solution)",
            "Reduced Representation (Reproducible genomic subsets, often generated by restriction fragment size selection, containing a manageable number of loci to facilitate re-sampling)",
            "Restriction Digest (DNA fractionation using restriction enzymes)",
            "5-methylcytidine antibody (Selection of methylated DNA fragments using an antibody raised against 5-methylcytosine or 5-methylcytidine (m5C))",
            "MBD2 protein methyl-CpG binding domain (Enrichment by methyl-CpG binding domain)",
            "CAGE (Cap-analysis gene expression)",
            "RACE (Rapid Amplification of cDNA Ends)",
            "MDA (Multiple displacement amplification)",
            "padlock probes capture method (to be used in conjuction with Bisulfite-Seq)",
            "other (Other library enrichment, screening, or selection process)",
            "unspecified (Library enrichment, screening, or selection is not specified)"),

    LIBRARY_STRAND("Library Strand",
             "Whether the 1st or 2nd cDNA strand was used in library prep. Don't confuse this with the forward and reverse reads one would get in a paired-end sequencing reaction. Choose \"not applicable\" if the library was unstranded.",
            "",
            "not applicable",
            "first strand",
            "second strand"),

    NOMINAL_LENGTH("Nominal Length","The expected size of the insert (the fragment sequenced, e.g. as selected by size fractionation) in base pairs. No decimals or ranges (e.g. 100-200) allowed, and it cannot be zero."),

    NOMINAL_SDEV("Nominal SDev","The standard deviation of the nominal length. Decimals are allowed (e.g. 56.4) but no ranges (e.g. 34.5-42.6)."),

    ORIENTATION("Orientation",
            "The orientation of the two reads.\"5'-3'-3'-5'\" for forward-reverse pairs (most common case),\"5'-3'-5'-3'\" for forward-forward pairs.",
            "",
            "5'-3'-3'-5'",
            "5'-3'-5'-3'"),

    ADAPTER_SEQUENCE("Adapter Sequence","");

    private final String title;
    private final List<String> values;
    private final List<String> options;
    private final String helpText;

    private ExtractAttribute(String title, String helpText, String... options) {
        this.title = title;
        this.helpText = helpText;
        this.options = asList(options);
        this.values = new ArrayList<String>();
        for (String option : options) {
            int index = option.indexOf(" (");
            String value = index < 0 ? option : option.substring(0, index);
            this.values.add(value);
        }
    }

    public String getName() {
        return toString();
    }

    public String getTitle() {
        return title;
    }

    public String getHelpText() {
        return helpText;
    }


    public boolean hasOptions() {
        return !this.options.isEmpty();
    }

    public List<String> getOptions() {
        return new ArrayList<String>(options);
    }

    public String getValue(String option) {
        return values.get(options.indexOf(option));
    }

    public String getOption(String value) {
        return options.get(values.indexOf(value));
    }
}
