package com.discordbot;

import com.discordbot.game.CatchService;
import com.discordbot.game.SpawnManager;
import com.discordbot.listeners.InteractionListener;
import com.discordbot.storage.JsonUserCollectionStore;
import com.discordbot.storage.UserCollectionStore;
import com.discordbot.model.UserLevel;
import com.discordbot.game.LevelService;
import com.discordbot.storage.JsonUserLevelStore;
import com.discordbot.storage.UserLevelStore;
import java.util.Random;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class DiscordBot extends ListenerAdapter {

    // Message counter variable
    private int messages = 0;
    private Random random = new Random();
    private String[] randomNames = {"AureComay", "SaantyBl", "Abricraft", "Nickname", "Yalmil", "Nienie", "Channel", "Pazmania"};
    private SpawnManager spawnManager;
    private CatchService catchService;
    private LevelService levelService; // Declare LevelService

    public static void main(String[] args) {
        // Get bot token from environment variable
        String token = System.getenv("DISCORD_BOT_TOKEN");

        if (token == null || token.isEmpty()) {
            System.err.println("ERROR: DISCORD_BOT_TOKEN environment variable is not set!");
            System.err.println("Please set your Discord bot token in the Secrets tab.");
            return;
        }

        try {
            // Create services
            UserCollectionStore collectionStore = new JsonUserCollectionStore("data/collections.json");
            UserLevelStore levelStore = new JsonUserLevelStore("data/levels.json");
            SpawnManager spawnManager = new SpawnManager();
            CatchService catchService = new CatchService(collectionStore);
            LevelService levelService = new LevelService(levelStore);
            InteractionListener interactionListener = new InteractionListener(spawnManager, catchService);

            DiscordBot mainBot = new DiscordBot();
            mainBot.spawnManager = spawnManager;
            mainBot.catchService = catchService;
            mainBot.levelService = levelService; // Assign LevelService to the bot instance

            // Create JDA instance
            JDA jda = JDABuilder.createDefault(token)
                    .setActivity(Activity.playing("A cazar Randomballs"))
                    .addEventListeners(mainBot, interactionListener)
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                    .build();

            System.out.println("RandomDex se esta iniciando...");

            // Wait for the bot to be ready
            jda.awaitReady();

        } catch (Exception e) {
            System.err.println("Failed to start Discord bot: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onReady(ReadyEvent event) {
        System.out.println(event.getJDA().getSelfUser().getName() + " Está conectado y listo para escuchar");
        System.out.println("Bot conectado para el servidor de los randoms (" + event.getJDA().getGuilds().size() + ")");
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Ignore messages from bots (including this bot)
        if (event.getAuthor().isBot()) {
            return;
        }

        String message = event.getMessage().getContentRaw();
        String channelName = event.getChannel().getName();
        String authorName = event.getAuthor().getName();
        String authorId = event.getAuthor().getId();

        System.out.println("Message received in #" + channelName + " from " + authorName + ": " + message);

        // Process message for leveling system
        LevelService.LevelUpResult levelResult = levelService.processMessage(authorId);
        
        // Check if user leveled up and send congratulations
        if (levelResult.isLeveledUp()) {
            String levelUpMessage = levelResult.getLevelUpMessage(authorName);
            if (levelUpMessage != null) {
                event.getChannel().sendMessage(levelUpMessage).queue();
            }
        }

        // Increment global message counter for spawn logic
        messages++;
        System.out.println("Messages counted: " + messages);

        // Check if we've reached 20 messages for spawn
        if (messages >= 20) {
            String randomName = randomNames[random.nextInt(randomNames.length)];

            SpawnManager.SpawnData spawnData = spawnManager.createSpawn(randomName, event.getChannel().getId());

            event.getChannel()
                    .sendMessageEmbeds(spawnData.getEmbed())
                    .addActionRow(spawnData.getActionRow().getComponents())
                    .queue(sentMessage -> {
                        // Store message ID for later editing
                        spawnData.getSpawn().setMessageId(sentMessage.getId());
                        System.out.println("Spawned " + randomName + " with ID: " + spawnData.getSpawn().getId() +
                                         " (Message: " + sentMessage.getId() + ")");
                    });

            System.out.println("Creating spawn for " + randomName);
            messages = 0; // Reset counter
        }

        // Ping command with comma prefix
        if (message.equalsIgnoreCase(",ping")) {
            long ping = event.getJDA().getGatewayPing();
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Pong! 🏓")
                    .setDescription("Mi ping es de " + ping + " ms")
                    .setColor(0x00FF00);
            event.getChannel().sendMessageEmbeds(embed.build()).queue();
        }

        // Collection command with comma prefix
        if (message.equalsIgnoreCase(",coleccion")) {
            String userId = event.getAuthor().getId();
            String username = event.getAuthor().getName();

            UserCollectionStore collectionStore = new JsonUserCollectionStore("data/collections.json");
            java.util.Map<String, Integer> userCollection = collectionStore.getUserCollectionWithCounts(userId);

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("RandomBalls de " + username)
                    .setColor(0xFF5733);

            if (userCollection.isEmpty()) {
                embed.setDescription("No tienes ningún RandomBall en tu colección. Es enserio?");
            } else {
                StringBuilder description = new StringBuilder("Tienes los siguientes RandomBalls:\n\n");

                // Character emojis mapping for collection display
                java.util.Map<String, String> characterEmojis = new java.util.HashMap<>();
                characterEmojis.put("Nienie", "<:nienie:1416972928746000425>");
                characterEmojis.put("AureComay", "<:aure:1416973274868482181>");
                characterEmojis.put("SaantyBl", "<:saanty:1416973215791710320>");
                characterEmojis.put("Nickname", "<:nickname_:1416973090906312714>");
                characterEmojis.put("Abricraft", "<:abricraft:1416973128952709131>");


characterEmojis.put("Yalmil", "<:yalmil:1416973450345451560>");                
                characterEmojis.put("Channel", "<:channel:1416973400168988694>");
                characterEmojis.put("Pazmania", "<:pazmania:1416973340916318230>");

                int totalCount = 0;
                for (java.util.Map.Entry<String, Integer> entry : userCollection.entrySet()) {
                    String character = entry.getKey();
                    int count = entry.getValue();
                    String emoji = characterEmojis.getOrDefault(character, "•");
                    description.append(emoji).append(" ").append(character).append(" (").append(count).append(")\n");
                    totalCount += count;
                }

                description.append("\n**Total: ").append(totalCount).append(" RandomBalls**");
                embed.setDescription(description.toString());
            }

            event.getChannel().sendMessageEmbeds(embed.build()).queue();
        }

        // Level command with comma prefix
        if (message.equalsIgnoreCase(",nivel") || message.equalsIgnoreCase(",level")) {
            String userId = event.getAuthor().getId();
            String username = event.getAuthor().getName();

            UserLevel userLevel = levelService.getUserLevel(userId);

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("📊 Nivel de " + username)
                    .setColor(0x9932CC);

            StringBuilder description = new StringBuilder();
            description.append("**Nivel:** ").append(userLevel.getLevel()).append("\n");
            description.append("**Experiencia:** ").append(userLevel.getExperience()).append(" XP\n");
            description.append("**Mensajes enviados:** ").append(userLevel.getMessagesCount()).append("\n\n");

            // Progress bar for current level
            int currentLevelXP = userLevel.getXPProgressInCurrentLevel();
            int neededXP = userLevel.getXPNeededForCurrentLevel();
            int nextLevelXP = userLevel.getXPForNextLevel();

            description.append("**Progreso al Nivel ").append(userLevel.getLevel() + 1).append(":**\n");
            description.append(currentLevelXP).append(" / ").append(neededXP).append(" XP\n");

            // Create progress bar
            int progressBarLength = 20;
            int filledBars = (int) ((double) currentLevelXP / neededXP * progressBarLength);
            StringBuilder progressBar = new StringBuilder();
            for (int i = 0; i < progressBarLength; i++) {
                if (i < filledBars) {
                    progressBar.append("█");
                } else {
                    progressBar.append("░");
                }
            }
            description.append("`").append(progressBar.toString()).append("`\n");
            description.append("*").append(nextLevelXP - userLevel.getExperience()).append(" XP restante*");

            embed.setDescription(description.toString());

            event.getChannel().sendMessageEmbeds(embed.build()).queue();
        }
    }
}