package com.csihu.commands;

import discord4j.core.event.domain.interaction.SlashCommandEvent;
import reactor.core.publisher.Mono;


public class PingCommand implements SlashCommand {
    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public Mono<Void> handle(SlashCommandEvent event) {
        // Reply to the command with "Pong!" and make sure it is ephemeral (only the command user can see it)
        return event.replyEphemeral("Pong!");
    }

}
