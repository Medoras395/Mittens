package my.bot.mittens;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mittens {

    private static final Map<String, Command> commands = new HashMap<>();

    static {
        commands.put("ping", event -> event.getMessage()
                                           .getChannel().block()
                                           .createMessage("Pong!").block());

        commands.put("hey", event -> event.getMessage()
                                          .getChannel().block()
                                          .createMessage("Hello!").block());

        commands.put("cat", event -> {
            JsonDownloader json = new JsonDownloader();
            json.jsonDownload();
            String pictureUrl = json.getPictureUrl();

            event.getMessage()
                 .getChannel().block()
                 .createMessage(pictureUrl).block();
        });
        commands.put("help", event -> {
            for (Map.Entry<String, Command> entry : commands.entrySet()) {
                String command = entry.getKey();

                event.getMessage()
                     .getChannel().block()
                     .createMessage(command).block();
            }
        });
        commands.put("test", event -> event.getMessage()
                                           .getChannel().block()
                                           .createMessage(commands.keySet().toString()).block());

        commands.put("loop", event -> event.getMessage()
                                           .getChannel().block()
                                           .createMessage("!loop").block());

        //        commands.put("join", event -> {
        //            final Member member = event.getMember().orElse(null);
        //
        //            if (member != null) {
        //                final VoiceState voiceState = member.getVoiceState().block();
        //
        //                if (voiceState != null) {
        //                    final VoiceChannel channel = voiceState.getChannel().block();
        //
        //                    if (channel != null) {
        //                        // join returns a VoiceConnection which would be required if we were
        //                        // adding disconnection features, but for now we are just ignoring it.
        //                        channel.join(spec -> spec.setProvider(provider)).block();
        //                    }
        //                }
        //            }
        //        });
    }



    public static void main(String[] args) {

        // Creates AudioPlayer instances and translates URLs to AudioTrack instances
        final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

        playerManager.getConfiguration()
                     .setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);

        // Allow playerManager to parse remote sources like YouTube links
        AudioSourceManagers.registerRemoteSources(playerManager);

        // Create an AudioPlayer so Discord4J can receive audio data
        final AudioPlayer player = playerManager.createPlayer();

        //creating LavaPlayerAudioProvider
        AudioProvider provider = new LavaPlayerAudioProvider(player);

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

        //        from the first tutorial
        //        client.getEventDispatcher().on(MessageCreateEvent.class)
        //                .map(MessageCreateEvent::getMessage)
        //                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
        //                .filter(message -> message.getContent().equalsIgnoreCase("!ping"))
        //                .flatMap(Message::getChannel)
        //                .flatMap(channel -> channel.createMessage("Pong!"))
        //                .subscribe();

        // second tutorial comparing text with hashmap commands
        client.getEventDispatcher().on(MessageCreateEvent.class)
              .subscribe(event -> {
                  final String content = event.getMessage().getContent();

                  for (final Map.Entry<String, Command> entry : commands.entrySet()) {
                      if (content.startsWith("!" + entry.getKey())) {
                          entry.getValue().execute(event);
                          break;
                      }
                  }
              });
        client.onDisconnect().block();

        //        String apiKey = "667252f1-5d34-46ba-86c0-8c2ef95fc49a";
        //        String apiUrl = "https://api.thecatapi.com/v1/images/search";
        //
        //        HttpClient client = HttpClient.newHttpClient();
        //        HttpRequest request = HttpRequest.newBuilder()
        //                .GET()
        //                .header("x-api-key", apiKey)
        //                .uri(URI.create(apiUrl))
        //                .build();
        //
        //        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
        //                .thenApply(HttpResponse::body)
        //                .thenApply(Mittens::parse)   // class name
        //                //.thenAccept(System.out::println)
        //                .join();

    }
    //    public static String parse(String responseBody) {
    //        JSONArray albums = new JSONArray(responseBody);
    //        JSONObject album = albums.getJSONObject(0);
    //        String picture = album.getString("url");
    //        System.out.println(picture);
    //
    //        return null;
    //    }
}