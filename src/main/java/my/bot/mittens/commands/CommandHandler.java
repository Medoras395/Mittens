package my.bot.mittens.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CommandHandler {

    private static List<Command> listeners = new ArrayList<>();

    public void addCommand(Command listener) {
        listeners.add(listener);
    }

    public void addStandardCommands() {
        addCommand(new Ping());
        addCommand(new Hey());
        addCommand(new Cat());
        addCommand(new Help());
    }

    public void handleCommand(final MessageCreateEvent event) {
        final String msgContent = event.getMessage().getContent().toLowerCase(Locale.ROOT).substring(1);

        for (Command listener : listeners) {
            if (listener.getClass().getSimpleName().toLowerCase(Locale.ROOT).equals(msgContent)) {
                listener.execute(event);
            }
        }
    }

    public static List<Command> getListeners() {
        return listeners;
    }

    public static void setListeners(final List<Command> listeners) {
        CommandHandler.listeners = listeners;
    }
}