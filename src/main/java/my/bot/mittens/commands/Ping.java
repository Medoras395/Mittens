package my.bot.mittens.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;

public class Ping implements Command{
    @Override
    public void execute(final MessageCreateEvent event) {
        try {
            event.getMessage().getChannel().block().createMessage("Pong!").block();
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
}
