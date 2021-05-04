package com.github.jjYBdx4IL.example.solr.beans;

import com.github.jjYBdx4IL.utils.solr.beans.FieldConfig;
import com.github.jjYBdx4IL.utils.solr.beans.FieldType;
import org.apache.solr.client.solrj.beans.Field;

public class ProductBean {

    String id;
    String name;
    double price;
    String text;

    public ProductBean() {
    }

    public ProductBean(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    @Field("id")
    @FieldConfig(type = FieldType.string, indexed = true, required = true, stored = true)
    protected void setId(String id) {
        this.id = id;
    }

    @Field("name")
    @FieldConfig(type = FieldType.text_general, indexed=true)
    protected void setName(String name) {
        this.name = name;
    }

    @Field("price")
    @FieldConfig(type = FieldType.pdoubles, indexed = true)
    protected void setPrice(double price) {
        this.price = price;
    }

    @Field("text")
    @FieldConfig(type = FieldType.text_general, indexed=true)
    protected void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getText() {
        return text;
    }

}
