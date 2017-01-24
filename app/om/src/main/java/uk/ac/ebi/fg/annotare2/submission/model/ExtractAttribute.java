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
            "Whether to expect SINGLE or PAIRED end reads",
            "",
            "SINGLE",
            "PAIRED"),

    LIBRARY_SOURCE("Library Source *",
            "The type of source material that is being sequenced",
            "",
            "GENOMIC (Genomic DNA (includes PCR products from genomic DNA))",
            "TRANSCRIPTOMIC (Transcription products or non genomic DNA (EST, cDNA, RT-PCR, screened libraries))",
            "TRANSCRIPTOMIC SINGLE CELL",
            "METAGENOMIC (Mixed material from metagenome)",
            "METATRANSCRIPTOMIC (Transcription products from community targets)",
            "SYNTHETIC (Synthetic DNA)",
            "VIRAL RNA (Viral RNA)",
            "OTHER (Other, unspecified, or unknown library source material)"),

    LIBRARY_STRATEGY("Library Strategy *",
            "The sequencing technique intended for the library",
            "",
            "WGS (Whole Genome Sequencing - random sequencing of the whole genome (see pubmed 10731132 for details))",
            "WGA (Whole Genome Amplification followed by random sequencing. (see pubmed 1631067,8962113 for details))",
            "WXS (Random sequencing of exonic regions selected from the genome. (see pubmed 20111037 for details))",
            "RNA-Seq (Random sequencing of whole transcriptome, also known as Whole Transcriptome Shotgun Sequencing, or WTSS. (see pubmed 18611170 for details) )",
            "ssRNA-seq (Strand-specific RNA sequencing)",
            "miRNA-seq (Micro RNA sequencing strategy designed to capture post-transcriptional RNA elements and include non-coding functional elements. (see pubmed 21787409 for details))",
            "ncRNA-Seq (Capture of other non-coding RNA types, including post-translation modification types such as snRNA (small nuclear RNA) or snoRNA (small nucleolar RNA), or expression regulation types such as siRNA (small interfering RNA) or piRNA/piwi/RNA (piwi-interacting RNA).)",
            "FL-cDNA (Full-length sequencing of cDNA templates)",
            "EST (Single pass sequencing of cDNA templates)",
            "Hi-C (Chromosome Conformation Capture technique where a biotin-labeled nucleotide is incorporated at the ligation junction, enabling selective purification of chimeric DNA ligation junctions followed by deep sequencing.)",
            "ATAC-seq (Assay for Transposase-Accessible Chromatin (ATAC) strategy is used to study genome-wide chromatin accessibility. alternative method to DNase-seq that uses an engineered Tn5 transposase to cleave DNA and to integrate primer DNA sequences into the cleaved genomic DNA. )",
            "WCS (Random sequencing of a whole chromosome or other replicon isolated from a genome)",
            "RAD-seq",
            "CLONE (Genomic clone based (hierarchical) sequencing)",
            "POOLCLONE (Shotgun of pooled clones (usually BACs and Fosmids))",
            "AMPLICON (Sequencing of overlapping or distinct PCR or RT-PCR products)",
            "CLONEEND (Clone end (5', 3', or both) sequencing)",
            "FINISHING (Sequencing intended to finish (close) gaps in existing coverage)",
            "ChIP-Seq (Direct sequencing of chromatin immunoprecipitates)",
            "MNase-Seq (Direct sequencing following MNase digestion)",
            "DNase-Hypersensitivity (Sequencing of hypersensitive sites, or segments of open chromatin that are more readily cleaved by DNaseI)",
            "Bisulfite-Seq (Sequencing following treatment of DNA with bisulfite to convert cytosine residues to uracil depending on methylation status)",
            "CTS (Concatenated Tag Sequencing)",
            "MRE-Seq (Methylation-Sensitive Restriction Enzyme Seqencing)",
            "MeDIP-Seq (Methylated DNA Immunoprecipitation Sequencing)",
            "MBD-Seq (Methyl CpG Binding Domain Sequencing)",
            "Tn-Seq (Quantitatively determine fitness of bacterial genes based on how many times a purposely seeded transposon gets inserted into each gene of a colony after some time. )",
            "VALIDATION",
            "FAIRE-seq (Formaldehyde-Assisted Isolation of Regulatory Elements. Reveals regions of open chromatin) ",
            "SELEX (Systematic Evolution of Ligands by Exponential enrichment)",
            "RIP-Seq (Direct sequencing of RNA immunoprecipitates (includes CLIP-Seq, HITS-CLIP and PAR-CLI))",
            "ChiA-PET (Direct sequencing of proximity-ligated chromatin immunoprecipitates)",
            "Synthetic-Long-Read (binning and barcoding of large DNA fragments to facilitate assembly of the fragment)",
            "Targeted-Capture (Enrichment of a targeted subset of loci.)",
            "Tethered Chromatin Conformation Capture",
            "OTHER (Library strategy not listed)"),

    LIBRARY_SELECTION("Library Selection *",
            "The method used to select and/or enrich the material being sequenced",
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
            "cDNA_randomPriming",
            "PolyA (PolyA selection or enrichment for messenger RNA (mRNA))",
            "Oligo-dT (enrichment of messenger RNA (mRNA) by hybridization to Oligo-dT)",
            "Inverse rRNA (depletion of ribosomal RNA by oligo hybridization)",
            "Inverse rRNA selection (depletion of ribosomal RNA by inverse oligo hybridization)",
            "ChIP (Chromatin immunoprecipitation)",
            "MNase (Micrococcal Nuclease (MNase) digestion)",
            "DNase (DNase I endonuclease digestion and size selection reveals regions of chromatin where the DNA is highly sensitive to DNase I)",
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
            "The orientation of the two reads. \"5'-3'-3'-5'\" for forward-reverse pairs (most common case),\"5'-3'-5'-3'\" for forward-forward pairs.",
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

    public boolean hasValue(String value) {
        return values.contains(value);
    }

    public String getOption(String value) {
        return options.get(values.indexOf(value));
    }
}
