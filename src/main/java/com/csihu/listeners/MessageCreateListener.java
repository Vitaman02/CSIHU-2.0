package com.csihu.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import discord4j.common.JacksonResources;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.publisher.Mono;

import com.csihu.GlobalCommandRegistrar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MessageCreateListener {

    public static Mono<Void> handle(MessageCreateEvent event) {
        String message = event.getMessage().getContent();
        if (message.equals("!commands")) {
            final JacksonResources d4jMapper = JacksonResources.create();

            final MessageChannel channel = event.getMessage().getChannel().block();

            List<String> commandsJSON = getCommands();
            Map<String, ApplicationCommandRequest> commands = new HashMap<>();
            StringBuilder out = new StringBuilder("```\n");
            for (String json : commandsJSON) {
                try  {
                    ApplicationCommandRequest request = d4jMapper.getObjectMapper().readValue(json, ApplicationCommandRequest.class);
                    out.append(request.name()).append("\n");
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }

            out.append("```");

            assert channel != null;
            channel.createMessage(out.toString()).block();
        }

        return Mono.empty();
    }

    private static List<String> getCommands() {
        try {
            return GlobalCommandRegistrar.getCommandsJson();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
