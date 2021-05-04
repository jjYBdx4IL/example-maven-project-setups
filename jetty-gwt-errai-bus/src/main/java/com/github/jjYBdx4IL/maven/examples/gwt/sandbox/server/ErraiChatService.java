package com.github.jjYBdx4IL.maven.examples.gwt.sandbox.server;

import com.github.jjYBdx4IL.maven.examples.gwt.sandbox.client.chat.PojoMessage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.jboss.errai.bus.client.api.base.CommandMessage;
import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.bus.client.api.messaging.MessageCallback;
import org.jboss.errai.bus.client.api.messaging.RequestDispatcher;
import org.jboss.errai.bus.server.annotations.Service;

/**
 *
 * @author jjYBdx4IL
 */
@Service
public class ErraiChatService implements MessageCallback {

    private static final Logger LOG = Logger.getLogger(ErraiChatService.class.getName());
    private static final long serialVersionUID = 1L;

    private final RequestDispatcher dispatcher;

    @Inject
    public ErraiChatService(RequestDispatcher dispatcher) {
        LOG.info("ErraiChatService new");
        this.dispatcher = dispatcher;
    }

    @Override
    public void callback(Message message) {
        LOG.log(Level.INFO, "ErraiChatService.callback1 {0}", message);
        MessageBuilder.createMessage()
                .toSubject("BroadcastReceiver")
                .with("message", message.get(PojoMessage.class, "message"))
                .noErrorHandling()
                .sendGlobalWith(dispatcher);
    }

    public void callback(CommandMessage message) {
        LOG.log(Level.INFO, "ErraiChatService.callback2 {0}", message);
    }
}
