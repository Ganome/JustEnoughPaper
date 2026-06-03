package lol.simeon.jer.listeners

import lol.simeon.jer.JustEnoughRecipes
import lol.simeon.jer.protocol.fabric.FabricPacketBuilder
import lol.simeon.jer.protocol.fabric.packet.RecipeSyncPayload
import lol.simeon.jer.protocol.fabric.recipe.FabricRecipeEntryBuilder
import lol.simeon.jer.protocol.neoforge.NeoforgePacketBuilder
import lol.simeon.jer.protocol.neoforge.packet.RecipeContentPayload
import lol.simeon.jer.protocol.neoforge.recipe.NeoforgeRecipeEntryBuilder
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

@Suppress("UnstableApiUsage")
class ConnectionHandler(val instance: JustEnoughRecipes) : Listener {

    init {
        Bukkit.getPluginManager().registerEvents(this, instance)
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        val entries = FabricRecipeEntryBuilder.buildRecipeSyncEntries()
        val fabricPayload = RecipeSyncPayload(entries)

        FabricPacketBuilder.sendRecipeSyncAsDiscarded(player, fabricPayload)

        val (types, recipes) = NeoforgeRecipeEntryBuilder.buildAll()
        val neoforgePayload = RecipeContentPayload(types, recipes)

        NeoforgePacketBuilder.sendRecipeSyncAsDiscarded(player, neoforgePayload)
    }
}