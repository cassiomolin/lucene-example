package com.cassiomolin.example.lucene;

import com.cassiomolin.example.model.PersonDetails;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.cassiomolin.example.util.DateUtils.toDate;
import static com.cassiomolin.example.util.DateUtils.toLocalDate;

/**
 * Component that searches an index with Apache Lucene.
 *
 * @author cassiomolin
 */
@ApplicationScoped
public class LuceneSearcher {

    /**
     * Find all documents.
     *
     * @param index
     * @return
     * @throws IOException
     */
    public List<PersonDetails> findAll(Directory index) throws IOException {
        Query query = new MatchAllDocsQuery();
        List<Document> documents = executeQuery(index, query, Integer.MAX_VALUE);
        return documents.stream().map(this::toPersonDetails).collect(Collectors.toList());
    }

    /**
     * Search documents by gender.
     *
     * @param index
     * @param gender
     * @return
     * @throws IOException
     */
    public List<PersonDetails> findByGender(Directory index, String gender) throws IOException {
        Query query = new TermQuery(new Term(DocumentFields.GENDER_FIELD, gender));
        List<Document> documents = executeQuery(index, query, Integer.MAX_VALUE);
        return documents.stream().map(this::toPersonDetails).collect(Collectors.toList());
    }

    /**
     * Search documents by salary range.
     *
     * @param index
     * @param lowerValue
     * @param upperValue
     * @return
     * @throws IOException
     */
    public List<PersonDetails> findBySalaryRange(Directory index, Integer lowerValue, Integer upperValue) throws IOException {
        Query query = IntPoint.newRangeQuery(DocumentFields.SALARY_FIELD, lowerValue, upperValue);
        List<Document> documents = executeQuery(index, query, Integer.MAX_VALUE);
        return documents.stream().map(this::toPersonDetails).collect(Collectors.toList());
    }

    /**
     * Search documents by date of birth range.
     *
     * @param index
     * @param lowerValue
     * @param upperValue
     * @return
     * @throws IOException
     */
    public List<PersonDetails> findByDateOfBirthRange(Directory index, LocalDate lowerValue, LocalDate upperValue) throws IOException {

        String lowerValueAsString = DateTools.dateToString(toDate(lowerValue), DateTools.Resolution.DAY);
        String upperValueAsString = DateTools.dateToString(toDate(upperValue), DateTools.Resolution.DAY);

        Query query = new TermRangeQuery(DocumentFields.DATE_OF_BIRTH_FIELD, new BytesRef(lowerValueAsString), new BytesRef(upperValueAsString), true, true);
        List<Document> documents = executeQuery(index, query, Integer.MAX_VALUE);
        return documents.stream().map(this::toPersonDetails).collect(Collectors.toList());
    }

    /**
     * Execute a query.
     *
     * @param index
     * @param query
     * @param maxResults
     * @return
     * @throws IOException
     */
    private List<Document> executeQuery(Directory index, Query query, Integer maxResults) throws IOException {

        Sort sort = new Sort(new SortField(DocumentFields.NAME_FIELD, SortField.Type.STRING));

        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs topDocs = searcher.search(query, maxResults, sort);

        return Arrays.stream(topDocs.scoreDocs)
                .map(scoreDoc -> toDocument(scoreDoc, searcher))
                .collect(Collectors.toList());
    }

    /**
     * Get a Lucene {@link Document} from a Lucene {@link ScoreDoc}.
     *
     * @param scoreDoc
     * @param searcher
     * @return
     */
    private Document toDocument(ScoreDoc scoreDoc, IndexSearcher searcher) {
        try {
            return searcher.doc(scoreDoc.doc);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create a {@link PersonDetails} instance from a  Lucene {@link Document}.
     *
     * @param document
     * @return
     */
    private PersonDetails toPersonDetails(Document document) {

        PersonDetails personDetails = new PersonDetails();
        personDetails.setId(document.get(DocumentFields.ID_FIELD));
        personDetails.setName(document.get(DocumentFields.NAME_FIELD));
        personDetails.setGender(document.get(DocumentFields.GENDER_FIELD));

        try {
            Date date = DateTools.stringToDate(document.get(DocumentFields.DATE_OF_BIRTH_FIELD));
            LocalDate localDate = toLocalDate(date);
            personDetails.setDateOfBirth(localDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        personDetails.setJobTitle(document.get(DocumentFields.JOB_TITLE_FIELD));
        personDetails.setSalary(document.getField(DocumentFields.SALARY_FIELD).numericValue().intValue());

        return personDetails;
    }
}
