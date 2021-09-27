package com.csihu.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import reactor.core.publisher.Mono;

import java.time.Instant;


public class GithubCommand implements SlashCommand {
    @Override
    public String getName() {
        return "github";
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
                .title("Github Link")
                .addField("\u200B", "https://github.com/Vitaman02/CSIHU-2.0", false)
                .timestamp(Instant.now())
                .build();
    }
}
