package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.events;

import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.events.LoginEvent.Handler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 *
 * Example event.
 * 
 * <ul>
 * <li>LoginEvent.register(eventBus, new LoginEvent.Handler() { ... implement onLogin here ...})
 * <li>eventbus.fireEvent(new LoginEvent(username, password))
 * <li>eventbus.fireEventFromSource(new LoginEvent(user, pass), this)
 * </ul>
 *
 * @author jjYBdx4IL
 */
public class LoginEvent extends GwtEvent<Handler> {

    public static interface Handler extends EventHandler {

        public void onLogin(LoginEvent event);
    }

    public static Type<Handler> TYPE = new Type<Handler>();

    public static HandlerRegistration register(final EventBus eventBus, final Handler handler) {
        return eventBus.addHandler(LoginEvent.TYPE, handler);
    }

    public static HandlerRegistration registerToSource(final EventBus eventBus, final Object source, final Handler handler) {
        return eventBus.addHandlerToSource(LoginEvent.TYPE, source, handler);
    }

    private final String username;
    private final String password;

    public LoginEvent() {
        this(null, null);
    }

    public LoginEvent(final String username, final String password) {
        this.username = username;
        this.password = password;
    }

    public String getUserName() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    @Override
    public Type<Handler> getAssociatedType() {
        return LoginEvent.TYPE;
    }

    @Override
    protected void dispatch(final Handler handler) {
        handler.onLogin(this);
    }

}
