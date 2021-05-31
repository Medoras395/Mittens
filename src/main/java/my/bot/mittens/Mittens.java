package my.bot.mittens;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.voice.AudioProvider;
import my.bot.mittens.commands.Command;
import my.bot.mittens.commands.CommandHandler;
import my.bot.mittens.data.JokeList;
import my.bot.mittens.data.JsonDownloader;
import my.bot.mittens.musicplayer.LavaPlayerAudioProvider;
import my.bot.mittens.musicplayer.TrackScheduler;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mittens {

    static AudioProvider provider;

    private static final Map<String, Command> commands = new HashMap<>();

    static {
        commands.put("test", event -> event.getMessage()
                                           .getChannel().block()
                                           .createMessage(commands.keySet().toString()).block());

    }


    public static void main(String[] args) {

        JokeList jk = new JokeList();
        InputStream stream = jk.getFileFromResourceAsStream("jokeList.txt");
        jk.printInputStream(stream);

        CommandHandler commandHandler = new CommandHandler();
        commandHandler.addStandardCommands();


        //todo audioplayer in command class

        // Creates AudioPlayer instances and translates URLs to AudioTrack instances
        final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

        playerManager.getConfiguration()
                     .setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);

        // Allow playerManager to parse remote sources like YouTube links
        AudioSourceManagers.registerRemoteSources(playerManager);

        // Create an AudioPlayer so Discord4J can receive audio data
        final AudioPlayer player = playerManager.createPlayer();

        //creating LavaPlayerAudioProvider
        //AudioProvider provider = new LavaPlayerAudioProvider(player);
        provider = new LavaPlayerAudioProvider(player);

        commands.put("join", event -> {
            final Member member = event.getMember().orElse(null);

            if (member != null) {
                final VoiceState voiceState = member.getVoiceState().block();

                if (voiceState != null) {
                    final VoiceChannel channel = voiceState.getChannel().block();

                    if (channel != null) {
                        // join returns a VoiceConnection which would be required if we were
                        // adding disconnection features, but for now we are just ignoring it.
                        channel.join(spec -> spec.setProvider(provider)).block();
                    }
                }
            }
        });

        final TrackScheduler scheduler = new TrackScheduler(player);
        // todo add queue
        commands.put("play", event -> {
            final String content = event.getMessage().getContent();
            final List<String> command = Arrays.asList(content.split(" "));
            playerManager.loadItem(command.get(1), scheduler);
        });

        // Building the Bot
        final GatewayDiscordClient client = DiscordClientBuilder.create(args[0])
                                                                .build()
                                                                .login()
                                                                .block();

        client.getEventDispatcher().on(ReadyEvent.class)
              .subscribe(event -> {
                  final User self = event.getSelf();
                  System.out.println(String.format(
                          "Logged in as %s#%s", self.getUsername(), self.getDiscriminator()
                  ));
              });

        client.getEventDispatcher().on(MessageCreateEvent.class)
              .subscribe(event -> {
                  if (event.getMessage().getContent().startsWith("!") && !client.getSelfId().equals(event.getMember().get().getId())) {
                      commandHandler.handleCommand(event);
                  }
              });
        client.onDisconnect().block();
    }


    public static AudioProvider getProviderFromMittens() {
        return provider;
    }

    public static void setProviderFromMittens(final AudioProvider provider) {
        Mittens.provider = provider;
    }
}