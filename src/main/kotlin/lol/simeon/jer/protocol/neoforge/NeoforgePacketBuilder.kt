package lol.simeon.jer.protocol.neoforge

import io.netty.buffer.Unpooled
import lol.simeon.jer.protocol.SharedHelper
import lol.simeon.jer.protocol.neoforge.packet.RecipeContentPayload
import net.minecraft.network.Connection
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket
import net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket
import net.minecraft.network.protocol.common.custom.DiscardedPayload
import net.minecraft.resources.Identifier
import net.minecraft.server.MinecraftServer
import net.minecraft.tags.TagNetworkSerialization
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player

object NeoforgePacketBuilder: SharedHelper() {

    fun sendRecipeSyncAsDiscarded(player: Player, payload: RecipeContentPayload) {
        val serverPlayer = (player as CraftPlayer).handle
        val server = MinecraftServer.getServer()

        val registryAccess = serverPlayer.registryAccess()
        val connection = scanForNetworkManager(serverPlayer.connection)

        val nettyBuf = Unpooled.buffer()
        val registryFriendlyByteBuf = RegistryFriendlyByteBuf(nettyBuf, registryAccess)

        RecipeContentPayload.STREAM_CODEC.encode(registryFriendlyByteBuf, payload)

        val bytes = ByteArray(nettyBuf.readableBytes())
        nettyBuf.readBytes(bytes)

        val id = Identifier.fromNamespaceAndPath("neoforge", "recipe_content")
        val discarded = constructDiscardedPayload(id, bytes)

        connection.send(ClientboundCustomPayloadPacket(discarded))
        connection.send(ClientboundUpdateTagsPacket(TagNetworkSerialization.serializeTagsToNetwork(server.registries())))
    }
}