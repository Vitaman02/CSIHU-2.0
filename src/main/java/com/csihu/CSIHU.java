package com.csihu;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;

import com.csihu.listeners.SlashCommandListener;
import com.csihu.listeners.MessageCreateListener;


public class CSIHU {
    public static void main(final String[] args) {
        final String TOKEN = System.getenv("TOKEN");
        DiscordClient client = DiscordClient.create(TOKEN);

        Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> {
            // Ready event
            Mono<Void> ready = gateway.on(ReadyEvent.class, event ->
                    Mono.fromRunnable(() -> {
                        final User self = event.getSelf();
                        System.out.printf("Logged in as %s#%s%n", self.getUsername(), self.getDiscriminator());
                    })).then();

            // Call registrar to crud global slash commands
            try {
                new GlobalCommandRegistrar(gateway.getRestClient()).registerCommands();
            } catch (Exception e) {
                System.out.println("Error trying to register global slash commands: " + e);
            }

            Mono<Void> slashCommands = gateway.on(ChatInputInteractionEvent.class, SlashCommandListener::handle).then();

            Mono<Void> onMessage = gateway.on(MessageCreateEvent.class, MessageCreateListener::handle).then();

            return ready.and(slashCommands).and(onMessage);
        }).then();


        login.block();
    }
}
