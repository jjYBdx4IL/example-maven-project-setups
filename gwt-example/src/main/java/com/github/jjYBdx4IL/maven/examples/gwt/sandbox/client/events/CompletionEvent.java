package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.events;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 *
 * @author jjYBdx4IL
 */
public class CompletionEvent<T> extends GwtEvent<CompletionEvent.Handler<T>> {

    public static interface Handler<T> extends EventHandler {

        public void onCompletion(CompletionEvent<T> event);
    }

    public static Type<Handler<?>> TYPE = new Type<Handler<?>>();

    public static HandlerRegistration register(final EventBus eventBus, final Handler<?> handler) {
        return eventBus.addHandler(CompletionEvent.TYPE, handler);
    }

    public static HandlerRegistration registerToSource(final EventBus eventBus, final Object source, final Handler<?> handler) {
        return eventBus.addHandlerToSource(CompletionEvent.TYPE, source, handler);
    }

    private final T value;

    public CompletionEvent() {
        this(null);
    }

    public CompletionEvent(final T value) {
        this.value = value;
    }

    public T getValue() {
        return this.value;
    }

    @Override
    public Type<CompletionEvent.Handler<T>> getAssociatedType() {
        return (Type) CompletionEvent.TYPE;
    }

    @Override
    protected void dispatch(Handler<T> handler) {
        handler.onCompletion(this);
    }

}
