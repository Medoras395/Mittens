package my.bot.mittens.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.VoiceChannel;
import my.bot.mittens.Mittens;

public class Join implements Command{
    @Override
    public void execute(final MessageCreateEvent event) {
        final Member member = event.getMember().orElse(null);

        if (member != null) {
            final VoiceState voiceState = member.getVoiceState().block();

            if (voiceState != null) {
                final VoiceChannel channel = voiceState.getChannel().block();

                if (channel != null) {
                    // join returns a VoiceConnection which would be required if we were
                    // adding disconnection features, but for now we are just ignoring it.
                    channel.join(spec -> spec.setProvider(Mittens.getProviderFromMittens())).block();
                }
            }
        }
    }
}
