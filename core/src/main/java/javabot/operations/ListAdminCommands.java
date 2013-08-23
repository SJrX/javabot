package javabot.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import javax.inject.Inject;

import com.antwerkz.maven.SPI;
import com.google.inject.Injector;
import javabot.IrcEvent;
import javabot.Javabot;
import javabot.Message;
import javabot.commands.AdminCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created Dec 17, 2008
 *
 * @author <a href="mailto:jlee@antwerkz.com">Justin Lee</a>
 */
@SPI({AdminCommand.class})
public class ListAdminCommands extends AdminCommand {
  @Inject
  private Injector injector;

  private static final String ADMIN_PREFIX = "admin ";

  @Override
  public List<Message> execute(Javabot bot, IrcEvent event) {
    final List<Message> responses = new ArrayList<>();
    final StringBuilder builder = new StringBuilder();
    for (final AdminCommand AdminCommand : listCommands()) {
      if (builder.length() != 0) {
        builder.append(", ");
      }
      final String name = AdminCommand.getClass().getSimpleName();
      builder.append(name.substring(0, 1).toLowerCase()).append(name.substring(1));
    }
    responses.add(new Message(event.getChannel(), event,
        event.getSender() + ", I know of the following commands: " + builder));
    return responses;
  }

  public List<AdminCommand> listCommands() {
    final ServiceLoader<AdminCommand> loader = ServiceLoader.load(AdminCommand.class);
    final List<AdminCommand> list = new ArrayList<>();
    for (final AdminCommand command : loader) {
      injector.injectMembers(command);
      list.add(command);
    }
    return list;
  }
}