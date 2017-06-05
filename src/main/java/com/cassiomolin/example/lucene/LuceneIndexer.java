package com.cassiomolin.example.lucene;

import com.cassiomolin.example.model.PersonDetails;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.cassiomolin.example.util.DateUtils.toDate;

/**
 * Component that indexes JSON data with Apache Lucene.
 *
 * @author cassiomolin
 */
@ApplicationScoped
public class LuceneIndexer {

    @Inject
    private ObjectMapper mapper;

    /**
     * Index JSON data.
     *
     * @param index
     * @throws IOException
     */
    public void index(Directory index) throws IOException {

        InputStream stream = this.getClass().getResourceAsStream("/data.json");
        List<PersonDetails> list = mapper.readValue(stream, new TypeReference<List<PersonDetails>>() {});

        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(index, config);

        list.stream().map(this::toDocument).forEach(document -> this.addToIndex(document, indexWriter));

        indexWriter.close();
    }


    /**
     * Create a Lucene {@link Document} instance from a {@link PersonDetails} instance.
     *
     * @param personDetails
     * @return
     */
    private Document toDocument(PersonDetails personDetails) {

        Document document = new Document();

        document.add(new StringField(DocumentFields.ID_FIELD, personDetails.getId(), Field.Store.YES));

        document.add(new TextField(DocumentFields.NAME_FIELD, personDetails.getName(), Field.Store.YES));
        document.add(new SortedDocValuesField(DocumentFields.NAME_FIELD, new BytesRef(personDetails.getName())));

        document.add(new TextField(DocumentFields.GENDER_FIELD, personDetails.getGender(), Field.Store.YES));

        document.add(new StringField(DocumentFields.DATE_OF_BIRTH_FIELD,
                DateTools.dateToString(toDate(personDetails.getDateOfBirth()), DateTools.Resolution.DAY), Field.Store.YES));

        document.add(new TextField(DocumentFields.JOB_TITLE_FIELD, personDetails.getJobTitle(), Field.Store.YES));

        document.add(new StoredField(DocumentFields.SALARY_FIELD, personDetails.getSalary()));
        document.add(new IntPoint(DocumentFields.SALARY_FIELD, personDetails.getSalary()));

        return document;
    }

    /**
     * Add a document to the index.
     *
     * @param document
     * @param indexWriter
     */
    private void addToIndex(Document document, IndexWriter indexWriter) {

        try {
            indexWriter.addDocument(document);
            indexWriter.commit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
