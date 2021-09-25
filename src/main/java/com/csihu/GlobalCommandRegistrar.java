package com.csihu;

import discord4j.common.JacksonResources;
import discord4j.discordjson.json.ApplicationCommandData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.RestClient;
import discord4j.rest.service.ApplicationService;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;


public class GlobalCommandRegistrar {
    private final RestClient restClient;

    public GlobalCommandRegistrar(RestClient restClient) {
        this.restClient = restClient;
    }

    // This is blocking code, but it is OK since it only runs on startup
    protected void registerCommands() throws IOException {
        // Create an ObjectMapper that supports Discord4J classes
        final JacksonResources d4jMapper = JacksonResources.create();

        // Convenience variables for the sake of easier to read code below
        final ApplicationService applicationService = restClient.getApplicationService();
        final long applicationID = restClient.getApplicationId().block();

        // These are commands already registered with discord from previous runs of the bot
        Map<String, ApplicationCommandData> discordCommands = applicationService
                .getGlobalApplicationCommands(applicationID)
                .collectMap(ApplicationCommandData::name)
                .block();

        // Get our commands json from resources as command data
        Map<String, ApplicationCommandRequest> commands = new HashMap<>();
        for (String json : getCommandsJson()) {
            ApplicationCommandRequest request = d4jMapper.getObjectMapper()
                    .readValue(json, ApplicationCommandRequest.class);

            // Add to array
            commands.put(request.name(), request);

            // Check id this is a new command that has not been registered before
            assert discordCommands != null;
            if (!discordCommands.containsKey(request.name())) {
                // It is not registered, so we need to create it
                applicationService.createGlobalApplicationCommand(applicationID, request).block();

                System.out.println("Created global command: " + request.name());
            }
        }

        // Check if any commands have been deleted or changed
        assert discordCommands != null;
        for (ApplicationCommandData discordCommand : discordCommands.values()) {
            long discordCommandID = Long.parseLong(discordCommand.id());

            ApplicationCommandRequest command = commands.get(discordCommand.name());

            if (command == null) {
                // Removed command.json, delete global command
                applicationService.deleteGlobalApplicationCommand(applicationID, discordCommandID).block();

                System.out.println("Deleted global command: " + discordCommand.name());

                // Skip next step
                continue;
            }

            if (hasChanged(discordCommand, command)) {
                applicationService.modifyGlobalApplicationCommand(applicationID, discordCommandID, command).block();

                System.out.println("Updated global command: " + command.name());
            }
        }
    }

    private boolean hasChanged(ApplicationCommandData discordCommand, ApplicationCommandRequest command) {
        // Check if description has changed
        if (!discordCommand.description().equals(command.description())) return true;

        // Check if default permissions have changed
        boolean discordCommandDefaultPermission = discordCommand.defaultPermission().toOptional().orElse(true);
        boolean commandDefaultPermission = command.defaultPermission().toOptional().orElse(true);

        if (discordCommandDefaultPermission != commandDefaultPermission) return true;

        // Check and return if options have changed
        return !discordCommand.options().equals(command.options());
    }

    public static List<String> getCommandsJson() throws IOException {
        // The name of the folder the commands json is in, inside the resources folder
        final String commandsFolderName = "commands/";

        // Get the folder as a resource
        URL url = GlobalCommandRegistrar.class.getClassLoader().getResource(commandsFolderName);
        Objects.requireNonNull(url, commandsFolderName + " could no be found");

        File folder;
        try {
            folder = new File(url.toURI());
        } catch (URISyntaxException e) {
            folder = new File(url.getPath());
        }

        // Get all the files inside this folder and return the contents of the files as a list of strings
        List<String> list = new ArrayList<>();
        File[] files = Objects.requireNonNull(folder.listFiles(), folder + " is not a directory");

        for (File file : files) {
            String resourceFileAsString = getResourceFileAsString(commandsFolderName + file.getName());
            list.add(resourceFileAsString);
        }

        return list;

    }

    /**
     * Gets a specific resource file as String
     * @param fileName The file path omitting "resources/"
     * @return The contents of the file as a String, otherwise throws an exception
     */
    private static String getResourceFileAsString(String fileName) throws IOException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try (InputStream resourceAsStream = classLoader.getResourceAsStream(fileName)) {
            if (resourceAsStream == null) return null;
            try (InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream);
                 BufferedReader reader = new BufferedReader(inputStreamReader)) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }

        }
    }
}
