# index-and-search-with-lucene

Lucene is a high-performance text search engine library written entirely in Java. This application demonstrates how to create an index and perform search operation with [Apache Lucene].

## Indexing and searching with Lucene

Here's an example on how to perform some simple operations with Lucene:

```java
// Create an index in memory
Directory index = new RAMDirectory();

// Create an index writer
StandardAnalyzer analyzer = new StandardAnalyzer();
IndexWriterConfig config = new IndexWriterConfig(analyzer);
IndexWriter indexWriter = new IndexWriter(index, config);

// Add a document to the index
Document document = new Document();
document.add(new TextField("name", "John Doe", Field.Store.YES));
document.add(new TextField("address", "80 Summer Hill", Field.Store.YES));
indexWriter.addDocument(document);

// Add a document to the index
document = new Document();
document.add(new TextField("name", "Jane Doe", Field.Store.YES));
document.add(new TextField("address", "9 Main Circle", Field.Store.YES));
indexWriter.addDocument(document);

// Add a document to the index
document = new Document();
document.add(new TextField("name", "John Smith", Field.Store.YES));
document.add(new TextField("address", "9 Dexter Avenue", Field.Store.YES));
indexWriter.addDocument(document);
    
indexWriter.close();

// Create an index searcher
IndexReader reader = DirectoryReader.open(index);
IndexSearcher searcher = new IndexSearcher(reader);

// Create a query to look for people with "doe" in the name
Query query = new TermQuery(new Term("name", "doe"));

// Execute que query and show the results
TopDocs topDocs = searcher.search(query, 10);

// Show addresses
for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
    document = searcher.doc(scoreDoc.doc);
    System.out.println(document.get("address"));
}
```

## What will you find in this application?

This application parses some JSON files with Jackson, indexes their content with Lucene and performs some searches.

## How to build and run this application?

To build and run this application, follow these steps:

1. Open a command line window or terminal.
1. Navigate to the root directory of the project, where the `pom.xml` resides.
1. Compile the project: `mvn clean compile`.
1. Package the application: `mvn package`.
1. Change into the `target` directory: `cd target`
1. You should see a file with the following or a similar name: `index-and-search-with-lucene-1.0.jar`.
1. Execute the JAR: `java -jar index-and-search-with-lucene-1.0.jar`.
1. The application should be executed and the result should be displayed in the console.

  [Apache Lucene]: http://lucene.apache.org/core/