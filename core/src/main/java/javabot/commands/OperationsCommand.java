package javabot.commands;

import java.util.List;

import javabot.IrcEvent;
import javabot.Javabot;
import javabot.Message;
import org.apache.commons.lang.StringUtils;

/**
 * Created Jan 26, 2009
 *
 * @author <a href="mailto:jlee@antwerkz.com">Justin Lee</a>
 */
public abstract class OperationsCommand extends AdminCommand {
  protected void listCurrent(final List<Message> responses, final Javabot bot, final IrcEvent event) {
    responses.add(new Message(event.getChannel(), event, "I am currently running the following operations:"));
    responses.add(new Message(event.getChannel(), event, StringUtils.join(bot.getOperations(), ",")));
  }
}