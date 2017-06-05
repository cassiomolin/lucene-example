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

    public static final String ID_FIELD = "id";
    public static final String NAME_FIELD = "name";
    public static final String GENDER_FIELD = "gender";
    public static final String DATE_OF_BIRTH_FIELD = "dateOfBirth";
    public static final String JOB_TITLE_FIELD = "jobTitle";
    public static final String SALARY_FIELD = "salary";
}
