package my.bot.mittens.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;

public class Help implements Command{
    @Override
    public void execute(final MessageCreateEvent event) {
        event.getMessage().getChannel().block().createMessage("Try these! Miau!").block();

        for (Command listener : CommandHandler.getListeners()) {
            event.getMessage()
                 .getChannel().block()
                 .createMessage("!" + listener.getClass().getSimpleName()).block();
        }
    }
}
