package lol.simeon.jer.protocol.neoforge.recipe

import net.minecraft.server.MinecraftServer
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.item.crafting.RecipeType
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.CraftServer

object NeoforgeRecipeEntryBuilder {

    /**
     * Collect all recipe types and all recipes.
     * Equivalent to NeoForge syncing everything.
     */
    fun buildAll(): Pair<Set<RecipeType<*>>, List<RecipeHolder<*>>> {
        val server = (Bukkit.getServer() as CraftServer).server
        val recipes = allRecipes(server)

        val types = recipes
            .map { holder -> holder.value().type }
            .toSet()

        return types to recipes
    }

    /**
     * Collect only recipes matching the given recipe types.
     */
    fun buildForTypes(
        recipeTypes: Set<RecipeType<*>>
    ): Pair<Set<RecipeType<*>>, List<RecipeHolder<*>>> {
        if (recipeTypes.isEmpty()) {
            return emptySet<RecipeType<*>>() to emptyList()
        }

        val server = (Bukkit.getServer() as CraftServer).server
        val recipes = allRecipes(server)

        val filtered = recipes.filter { holder ->
            val recipeType = holder.value().type
            recipeTypes.contains(recipeType)
        }

        return recipeTypes to filtered
    }

    /**
     * Internal helper to get all recipe holders from the server.
     */
    private fun allRecipes(server: MinecraftServer): List<RecipeHolder<*>> {
        return server.recipeManager.recipes.values().toList()
    }
}