package lol.simeon.jer.protocol.fabric.recipe

import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.item.crafting.RecipeSerializer
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.CraftServer

object FabricRecipeEntryBuilder {

    fun buildRecipeSyncEntries(): List<RecipeEntry> {
        val craftServer = Bukkit.getServer() as CraftServer
        val mcServer = craftServer.server

        val all: Collection<RecipeHolder<*>> = mcServer.recipeManager.recipes.values()

        val grouped: Map<RecipeSerializer<*>, List<RecipeHolder<*>>> = all.groupBy { holder ->
            val recipe = holder.value()
            recipe.serializer
        }

        return grouped.entries.map { (serializer, holders) -> RecipeEntry(serializer, holders) }
            .sortedBy { entry -> entry.serializer.toString() }
    }
}