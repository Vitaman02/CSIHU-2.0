package com.csihu.listeners;

import com.csihu.commands.*;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;


public class SlashCommandListener {
    private final static List<SlashCommand> commands = new ArrayList<>();

    static {
        // Register the commands here when the class is initialized
        commands.add(new PingCommand());
        commands.add(new CoursesCommand());
        commands.add(new GithubCommand());
        commands.add(new MoodleCommand());
        commands.add(new TagvcCommand());
    }

    public static Mono<Void> handle(ChatInputInteractionEvent event) {
        // Convert our array list to a flux, so we can iterate through it
        return Flux.fromIterable(commands)
                // Get commands that match the name of this command
                .filter(command -> command.getName().equals(event.getCommandName()))
                // Get the first item in the flux
                .next()
                // Command handles the event
                .flatMap(command -> command.handle(event));
    }
}
