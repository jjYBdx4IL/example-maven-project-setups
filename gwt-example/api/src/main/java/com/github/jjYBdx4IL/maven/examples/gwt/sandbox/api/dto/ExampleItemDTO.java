package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.api.dto;

import java.io.Serializable;

/**
 *
 * @author jjYBdx4IL
 */
public class ExampleItemDTO implements Serializable {

    public ExampleItemDTO() {}
    
    public ExampleItemDTO(String data1, String data2) {
        this.data1 = data1;
        this.data2 = data2;
    }
    
    /**
     * @return the data1
     */
    public String getData1() {
        return data1;
    }

    /**
     * @param data1 the data1 to set
     */
    public void setData1(String data1) {
        this.data1 = data1;
    }

    /**
     * @return the data2
     */
    public String getData2() {
        return data2;
    }

    /**
     * @param data2 the data2 to set
     */
    public void setData2(String data2) {
        this.data2 = data2;
    }

    private String data1;
    private String data2;

    @Override
    public String toString() {
        return "ExampleItemDTO{" + "data1=" + data1 + ", data2=" + data2 + '}';
    }

    
}
