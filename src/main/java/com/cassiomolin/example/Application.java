package com.cassiomolin.example;


import com.cassiomolin.example.lucene.LuceneIndexer;
import com.cassiomolin.example.lucene.LuceneSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Application entry point.
 *
 * @author cassiomolin
 */
public class Application {

    public static void main(String[] args) throws IOException {

        Weld weld = new Weld();
        WeldContainer container = weld.initialize();

        Directory index = new RAMDirectory();
        LuceneIndexer indexer = container.select(LuceneIndexer.class).get();
        LuceneSearcher searcher = container.select(LuceneSearcher.class).get();

        indexer.index(index);

        System.out.println(searcher.findAll(index).size());
        System.out.println(searcher.findBySalaryRange(index, 70_000, 75_000).size());
        System.out.println(searcher.findByGender(index, "female").size());
        System.out.println(searcher.findByDateOfBirthRange(index, LocalDate.of(1980, 1, 1), LocalDate.of(1985, 1, 1)).size());

        container.shutdown();
    }
}
