package com.github.jjYBdx4IL.example.solr.beans;

public enum FieldType {
    /**
     * fixed string.
     * 
     * Does <b>not</b> get analyzed/tokenized.
     */
    string,
    /**
     * string that gets analyzed/tokenized.
     */
    text_general,
    pdoubles;
}
