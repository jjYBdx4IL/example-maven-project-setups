package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client;


import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

public interface GoodbyeView extends IsWidget {
    void setName(String goodbyeName);
    void setPresenter(Presenter presenter);

    public interface Presenter {
        void goTo(Place place);
    }
}
