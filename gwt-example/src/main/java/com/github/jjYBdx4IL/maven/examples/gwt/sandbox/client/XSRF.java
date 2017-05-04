package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client;

import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.events.CompletionEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;
import com.google.gwt.user.client.rpc.RpcTokenException;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.rpc.XsrfToken;
import com.google.gwt.user.client.rpc.XsrfTokenService;
import com.google.gwt.user.client.rpc.XsrfTokenServiceAsync;
import com.google.web.bindery.event.shared.EventBus;

/**
 *
 * @author jjYBdx4IL
 */
public class XSRF {

    static void init(final EventBus eventBus) {
        XsrfTokenServiceAsync xsrf = (XsrfTokenServiceAsync) GWT.create(XsrfTokenService.class);
        ((ServiceDefTarget) xsrf).setServiceEntryPoint(GWT.getModuleBaseURL() + "xsrf");
        xsrf.getNewXsrfToken(new AsyncCallback<XsrfToken>() {

            public void onSuccess(XsrfToken token) {
                ((HasRpcToken) ResourceBundle.asyncService).setRpcToken(token);

                eventBus.fireEvent(new CompletionEvent(token));
            }

            public void onFailure(Throwable caught) {
                try {
                    throw caught;
                } catch (RpcTokenException e) {
                    // Can be thrown for several reasons:
                    //   - duplicate session cookie, which may be a sign of a cookie
                    //     overwrite attack
                    //   - XSRF token cannot be generated because session cookie isn't
                    //     present
                } catch (Throwable e) {
                    // unexpected
                }
            }
        });
    }
}
