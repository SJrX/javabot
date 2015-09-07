package javabot.operations;

import com.antwerkz.sofia.Sofia;
import javabot.Message;
import javabot.dao.FactoidDao;
import javabot.model.Factoid;
import org.pircbotx.Channel;

import javax.inject.Inject;

public class ForgetFactoidOperation extends BotOperation implements StandardOperation  {
    @Inject
    private FactoidDao factoidDao;

    @Override
    public boolean handleMessage(final Message event) {
        final Channel channel = event.getChannel();
        String message = event.getValue();
        boolean handled = false;
        if (message.startsWith("forget ")) {
            if ((channel == null || !channel.getName().startsWith("#")) && !isAdminUser(event.getUser())) {
                getBot().postMessageToChannel(event, Sofia.privmsgChange());
            } else {
                message = message.substring("forget ".length());
                if (message.endsWith(".") || message.endsWith("?") || message.endsWith("!")) {
                    message = message.substring(0, message.length() - 1);
                }
                handled = forget(event, message.toLowerCase());
            }
        }
        return handled;
    }

    protected boolean forget(final Message event, final String key) {
        final Factoid factoid = factoidDao.getFactoid(key);
        if (factoid != null) {
            if (!factoid.getLocked() || isAdminUser(event.getUser())) {
                getBot().postMessageToChannel(event, Sofia.factoidForgotten(key, event.getUser().getNick()));
                factoidDao.delete(event.getUser().getNick(), key);
            } else {
                getBot().postMessageToChannel(event, Sofia.factoidDeleteLocked(event.getUser().getNick()));
            }
        } else {
            getBot().postMessageToChannel(event, Sofia.factoidDeleteUnknown(key, event.getUser().getNick()));
        }

        return true;
    }
}
