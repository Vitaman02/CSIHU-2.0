package com.csihu.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.VoiceChannel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;


public class TagvcCommand implements SlashCommand {
    @Override
    public String getName() {
        return "tagvc";

    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {

        Optional<Member> optionalMember = event.getInteraction().getMember();

        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            Mono<VoiceState> voice = member.getVoiceState();
            Flux<VoiceState> voices = voice.flatMapMany(v -> v.getChannel().flatMapMany(VoiceChannel::getVoiceStates));

            String memberMention = member.getMention();
            StringBuilder out = new StringBuilder();
            out.append(memberMention).append(" tagged: ");
            for (VoiceState v : voices.toIterable()) {
                System.out.println(v.toString());
                Member currentMember = Objects.requireNonNull(v.getMember().map(Member::asFullMember).block()).block();
                assert currentMember != null;
                String mention = currentMember.getMention();
                if (mention.equals(memberMention)) continue;
                out.append(mention).append("-");
            }
            out.deleteCharAt(out.length()-1);

            return event.reply(out.toString());
        }

        return Mono.empty();
    }
}
