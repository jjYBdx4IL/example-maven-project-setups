package com.github.jjYBdx4IL.maven.examples.aspectj;

/**
 *
 * @author jjYBdx4IL
 */
public class ValidatableImpl implements Validatable {

    public int testCounter = 0;
    
    public ValidatableImpl() {
    }
    
    public ValidatableImpl(String unused) {
    }
    
    @Override
    public void validateInternalState() {
        this.testCounter++;
    }

}
