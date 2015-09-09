package uk.ac.ebi.fg.annotare2.web.server.services.utils;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.util.Version;

import java.io.Reader;

public final class LuceneEfoTermAnalyzer extends Analyzer {
    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        Tokenizer source = new WhitespaceTokenizer(Version.LUCENE_43, reader);
        TokenStream filter = new LowerCaseFilter(Version.LUCENE_43, source);
        return new TokenStreamComponents(source, filter);
    }
}