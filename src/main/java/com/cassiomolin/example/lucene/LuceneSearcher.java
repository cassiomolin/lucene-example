package com.cassiomolin.example.lucene;

import com.cassiomolin.example.model.ShoppingList;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
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
    public List<ShoppingList> findAll(Directory index) throws IOException {
        Query query = new MatchAllDocsQuery();
        List<Document> documents = executeQuery(index, query, Integer.MAX_VALUE);
        return documents.stream().map(this::toShoppingList).collect(Collectors.toList());
    }

    /**
     * Search documents by person name.
     *
     * @param index
     * @param personName
     * @return
     * @throws IOException
     */
    public List<ShoppingList> findByPersonName(Directory index, String personName) throws IOException {
        Query query = new TermQuery(new Term(DocumentFields.NAME_FIELD, personName));
        List<Document> documents = executeQuery(index, query, Integer.MAX_VALUE);
        return documents.stream().map(this::toShoppingList).collect(Collectors.toList());
    }

    /**
     * Search documents by item.
     *
     * @param index
     * @return
     * @throws IOException
     */
    public List<ShoppingList> findByItem(Directory index, String item) throws IOException {
        Query query = new TermQuery(new Term(DocumentFields.ITEM_FIELD, item));
        List<Document> documents = executeQuery(index, query, Integer.MAX_VALUE);
        return documents.stream().map(this::toShoppingList).collect(Collectors.toList());
    }

    /**
     * Search documents by a range of date.
     *
     * @param index
     * @param lowerValue
     * @param upperValue
     * @return
     * @throws IOException
     */
    public List<ShoppingList> findByDateRange(Directory index, LocalDate lowerValue, LocalDate upperValue) throws IOException {

        String lowerValueAsString = DateTools.dateToString(toDate(lowerValue), DateTools.Resolution.DAY);
        String upperValueAsString = DateTools.dateToString(toDate(upperValue), DateTools.Resolution.DAY);

        Query query = new TermRangeQuery(DocumentFields.DATE_FIELD, new BytesRef(lowerValueAsString), new BytesRef(upperValueAsString), true, true);
        List<Document> documents = executeQuery(index, query, Integer.MAX_VALUE);
        return documents.stream().map(this::toShoppingList).collect(Collectors.toList());
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
     * Create a {@link ShoppingList} instance from a Lucene {@link Document}.
     *
     * @param document
     * @return
     */
    private ShoppingList toShoppingList(Document document) {

        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setName(document.get(DocumentFields.NAME_FIELD));

        try {
            Date date = DateTools.stringToDate(document.get(DocumentFields.DATE_FIELD));
            LocalDate localDate = toLocalDate(date);
            shoppingList.setDate(localDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        shoppingList.setItems(Arrays.stream(document.getFields(DocumentFields.ITEM_FIELD))
                .map(IndexableField::stringValue).collect(Collectors.toList()));
        shoppingList.setFileName(document.get(DocumentFields.FILE_NAME_FIELD));

        return shoppingList;
    }
}
