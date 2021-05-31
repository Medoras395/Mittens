package my.bot.mittens.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import my.bot.mittens.data.JokeList;

import java.util.Random;

public class Joke implements Command{
    @Override
    public void execute(final MessageCreateEvent event) {

        Random rnd = new Random();
        int max = JokeList.jokeList.size();
        // inclusive 0 and exclusive maxValue
        int result = rnd.nextInt(max+1);

        try {
            event.getMessage().getChannel().block().createMessage(JokeList.jokeList.get(result)).block();
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
}
