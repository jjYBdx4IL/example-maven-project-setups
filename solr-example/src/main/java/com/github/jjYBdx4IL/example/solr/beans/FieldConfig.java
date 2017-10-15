package com.github.jjYBdx4IL.example.solr.beans;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface FieldConfig {

    boolean unique() default false;

    /**
     * required for retrieving.
     */
    boolean stored() default true;

    boolean required() default false;
    
    /**
     * required for searching.
     */
    boolean indexed() default false;
    boolean multiValued() default false;

    FieldType type() default FieldType.string;
}