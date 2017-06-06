package com.cassiomolin.example.lucene;

/**
 * Constants for document field names.
 *
 * @author cassiomolin
 */
public final class DocumentFields {

    private DocumentFields() {
        throw new AssertionError("No instances for you!");
    }

    public static final String NAME_FIELD = "name";
    public static final String DATE_FIELD = "date";
    public static final String ITEM_FIELD = "item";
    public static final String FILE_NAME_FIELD = "fileName";
}
