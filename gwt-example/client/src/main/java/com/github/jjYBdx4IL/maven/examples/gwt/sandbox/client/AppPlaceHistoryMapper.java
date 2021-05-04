package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client;


import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

@WithTokenizers({HelloPlace.Tokenizer.class, GoodbyePlace.Tokenizer.class})
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper
{
}
