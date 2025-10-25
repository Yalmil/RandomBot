package com.discordbot.listeners;

import com.discordbot.game.CatchService;
import com.discordbot.game.SpawnManager;
import com.discordbot.model.Spawn;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class InteractionListener extends ListenerAdapter {
    private final SpawnManager spawnManager;
    private final CatchService catchService;
    
    public InteractionListener(SpawnManager spawnManager, CatchService catchService) {
        this.spawnManager = spawnManager;
        this.catchService = catchService;
    }
    
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String customId = event.getComponentId();
        
        if (customId.startsWith("catch:")) {
            String spawnId = customId.substring(6); // Remove "catch:" prefix
            
            Spawn spawn = spawnManager.getSpawn(spawnId);
            if (spawn == null) {
                event.reply("Este random ya no está disponible o ha expirado.")
                     .setEphemeral(true)
                     .queue();
                return;
            }
            
            // Create modal for name input
            TextInput nameInput = TextInput.create("name", "Nombre del random", TextInputStyle.SHORT)
                    .setPlaceholder("Escribe el nombre del random...")
                    .setRequiredRange(1, 50)
                    .build();
                    
            Modal modal = Modal.create("catch-modal:" + spawnId, "Atraparlo!")
                    .addActionRow(nameInput)
                    .build();
                    
            event.replyModal(modal).queue();
        }
    }
    
    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        String customId = event.getModalId();
        
        if (customId.startsWith("catch-modal:")) {
            String spawnId = customId.substring(12); // Remove "catch-modal:" prefix
            
            String inputName = event.getValue("name").getAsString();
            String userId = event.getUser().getId();
            
            // Atomically consume the spawn - only one user can succeed
            Spawn spawn = spawnManager.tryConsumeSpawn(spawnId);
            if (spawn == null) {
                event.reply("Che, como no te sabes el nombre del random?")
                     .setEphemeral(true)
                     .queue();
                return;
            }
            
            CatchService.CatchResult result = catchService.attemptCatch(userId, inputName, spawn.getCharacterName());
            
            if (result.isSuccess()) {
                // Spawn already consumed atomically above
                
                // Edit original message to disable button and announce winner
                String messageId = spawn.getMessageId();
                if (messageId != null) {
                    event.getChannel().editMessageById(messageId, 
                            "✅ **" + event.getUser().getName() + "** ha atrapado a **" + result.getName() + "**!")
                            .setEmbeds() // Clear embeds
                            .setComponents() // Clear buttons
                            .queue();
                }
                
                event.reply(result.getResponseMessage()).queue();
                System.out.println("User " + event.getUser().getName() + " caught " + result.getName() + 
                                 (result.isNewCatch() ? " (new)" : " (duplicate)"));
            } else {
                // Catch failed - restore the spawn since it was consumed but not caught
                spawnManager.restoreSpawn(spawn);
                
                event.reply(result.getResponseMessage())
                     .setEphemeral(true)
                     .queue();
                System.out.println("User " + event.getUser().getName() + " failed to catch " + 
                                 spawn.getCharacterName() + " with input: " + inputName);
            }
        }
    }
}
