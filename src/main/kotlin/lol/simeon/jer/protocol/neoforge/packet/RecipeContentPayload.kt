package lol.simeon.jer.protocol.neoforge.packet

import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.Identifier
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.item.crafting.RecipeType

data class RecipeContentPayload(
    val recipeTypes: Set<RecipeType<*>>,
    val recipes: List<RecipeHolder<*>>
) : CustomPacketPayload {

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

    companion object {
        val TYPE: CustomPacketPayload.Type<RecipeContentPayload> =
            CustomPacketPayload.Type(Identifier.fromNamespaceAndPath("neoforge", "recipe_content"))

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, RecipeContentPayload> =
            StreamCodec.composite(
                ByteBufCodecs.registry(Registries.RECIPE_TYPE)
                    .apply(ByteBufCodecs.collection(::HashSet)),
                RecipeContentPayload::recipeTypes,

                RecipeHolder.STREAM_CODEC
                    .apply(ByteBufCodecs.list()),
                RecipeContentPayload::recipes,

                ::RecipeContentPayload
            )

        /**
         * Create the payload for a set of recipe types, filtering the given recipes to only those types.
         */
        fun create(recipeTypes: Collection<RecipeType<*>>, allRecipes: Collection<RecipeHolder<*>>): RecipeContentPayload {
            val typeSet = recipeTypes.toSet()

            if (typeSet.isEmpty()) {
                return RecipeContentPayload(typeSet, emptyList())
            }

            val subset = allRecipes.filter { holder ->
                @Suppress("UNCHECKED_CAST")
                val type = holder.value().type
                typeSet.contains(type)
            }

            return RecipeContentPayload(typeSet, subset)
        }
    }
}