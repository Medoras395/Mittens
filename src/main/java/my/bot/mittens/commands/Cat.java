package my.bot.mittens.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import my.bot.mittens.data.JsonDownloader;

public class Cat implements Command{
    @Override
    public void execute(final MessageCreateEvent event) {
        JsonDownloader json = new JsonDownloader();
        JsonDownloader.jsonDownload();
        String pictureUrl = json.getPictureUrl();

        event.getMessage()
             .getChannel().block()
             .createMessage(pictureUrl).block();
    }
}
