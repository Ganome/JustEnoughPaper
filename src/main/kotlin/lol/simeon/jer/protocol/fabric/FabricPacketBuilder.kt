package lol.simeon.jer.protocol.fabric

import io.netty.buffer.Unpooled
import lol.simeon.jer.protocol.SharedHelper
import lol.simeon.jer.protocol.fabric.packet.RecipeSyncPayload
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket
import net.minecraft.resources.Identifier
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player

object FabricPacketBuilder: SharedHelper() {

    fun sendRecipeSyncAsDiscarded(player: Player, payload: RecipeSyncPayload) {
        val serverPlayer = (player as CraftPlayer).handle
        val connection = scanForNetworkManager(serverPlayer.connection)

        val registryAccess = serverPlayer.registryAccess()
        val nettyBuf = Unpooled.buffer()
        val registryFriendlyByteBuf = RegistryFriendlyByteBuf(nettyBuf, registryAccess)

        // encode your payload into bytes
        RecipeSyncPayload.CODEC.encode(registryFriendlyByteBuf, payload)

        val bytes = ByteArray(nettyBuf.readableBytes())
        nettyBuf.readBytes(bytes)

        val id = Identifier.fromNamespaceAndPath("fabric", "recipe_sync")
        val discarded = constructDiscardedPayload(id, bytes)

        connection.send(ClientboundCustomPayloadPacket(discarded))
    }
}