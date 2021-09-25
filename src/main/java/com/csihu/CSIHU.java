package com.csihu;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.SlashCommandEvent;
//import discord4j.core.event.domain.message.MessageCreateEvent;
//import discord4j.core.object.entity.Message;

import com.csihu.listeners.SlashCommandListener;


public class CSIHU {
    public static void main(final String[] args) {
        final String TOKEN = System.getenv("TOKEN");
        GatewayDiscordClient client = DiscordClient.create(TOKEN).login().block();

        assert client != null;

        // TODO no commands work at the moment because they are not registered
        // TODO The simple-bot example creates a global command registrar

        client.on(SlashCommandEvent.class, SlashCommandListener::handle)
                .then(client.onDisconnect())
                .block();

    }
}
