package com.csihu.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;

import java.time.Instant;


public class MoodleCommand implements SlashCommand {
    @Override
    public String getName() {
        return "moodle";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {
        // Create an embed to show the link
        EmbedCreateSpec embed = getEmbed();

        return event.reply().withEmbeds(embed).withEphemeral(true);
    }

    private EmbedCreateSpec getEmbed() {
        return EmbedCreateSpec.builder()
                .color(Color.of(0xff3030))
                .title("Moodle Link")
                .addField("\u200B", "https://moodle.cs.ihu.gr/", false)
                .timestamp(Instant.now())
                .build();
    }
}
