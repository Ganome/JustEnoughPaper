package lol.simeon.jer.protocol

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import net.minecraft.network.Connection
import net.minecraft.network.protocol.common.custom.DiscardedPayload
import net.minecraft.resources.Identifier
import net.minecraft.server.network.ServerCommonPacketListenerImpl
import net.minecraft.server.network.ServerGamePacketListenerImpl

abstract class SharedHelper {

    fun constructDiscardedPayload(id: Identifier, bytes: ByteArray): DiscardedPayload {
        val clazz = DiscardedPayload::class.java
        try {
            val constructor = clazz.getDeclaredConstructor(Identifier::class.java, ByteBuf::class.java)
            return constructor.newInstance(id, Unpooled.copiedBuffer(bytes))
        } catch (e: NoSuchMethodException) {
            return DiscardedPayload(id, bytes)
        }
    }

    fun scanForNetworkManager(connection: ServerGamePacketListenerImpl): Connection {
        val clazz = ServerCommonPacketListenerImpl::class.java
        val field = try {
            clazz.getDeclaredField("e")
        } catch (e: NoSuchFieldException) {
            return connection.connection
        }
        field.isAccessible = true
        return field[connection] as Connection
    }
}