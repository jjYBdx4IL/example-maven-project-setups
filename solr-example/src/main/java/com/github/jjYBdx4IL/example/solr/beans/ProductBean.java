package com.github.jjYBdx4IL.example.solr.beans;

import org.apache.solr.client.solrj.beans.Field;

public class ProductBean {

    String id;
    String name;
    String price;
    String text;

    public ProductBean(String id, String name, String price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    @Field("id")
    @FieldConfig(type = FieldType.string, indexed = true, required = true)
    protected void setId(String id) {
        this.id = id;
    }

    @Field("name")
    @FieldConfig(type = FieldType.text_general)
    protected void setName(String name) {
        this.name = name;
    }

    @Field("price")
    @FieldConfig(type = FieldType.pdoubles)
    protected void setPrice(String price) {
        this.price = price;
    }

    @Field("text")
    @FieldConfig(type = FieldType.text_general)
    protected void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getText() {
        return text;
    }

}
