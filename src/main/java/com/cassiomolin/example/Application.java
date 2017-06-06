package com.cassiomolin.example;


import com.cassiomolin.example.lucene.LuceneIndexer;
import com.cassiomolin.example.lucene.LuceneSearcher;
import com.cassiomolin.example.model.ShoppingList;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

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

        System.out.println("\nFind all shopping lists");
        System.out.println("----------------------------------------------");
        searcher.findAll(index).forEach(Application::printShoppingList);

        System.out.println("\nFind shopping lists by person name");
        System.out.println("----------------------------------------------");
        searcher.findByPersonName(index, "John Doe").forEach(Application::printShoppingList);

        System.out.println("\nFind shopping lists by item");
        System.out.println("----------------------------------------------");
        searcher.findByItem(index, "Milk").forEach(Application::printShoppingList);

        System.out.println("\nFind shopping lists date range");
        System.out.println("----------------------------------------------");
        searcher.findByDateRange(index, LocalDate.of(2017, 1, 1), LocalDate.of(2017, 1, 2)).forEach(Application::printShoppingList);

        container.shutdown();
    }

    private static void printShoppingList(ShoppingList shoppingList) {
        System.out.println("file: " + shoppingList.getFileName());
        System.out.println("name: " + shoppingList.getName());
        System.out.println("date: " + shoppingList.getDate().format(DateTimeFormatter.ISO_DATE));
        System.out.println("items: " + shoppingList.getItems().stream().collect(Collectors.joining(", ")));
        System.out.println();
    }
}
