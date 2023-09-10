package cy.jdkdigital.noflyzone.network.packets;

import cy.jdkdigital.noflyzone.util.FlightHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public record StopFlightMessage(UUID playerUUID)
{
    public static void encode(StopFlightMessage message, FriendlyByteBuf buffer) {
        buffer.writeUUID(message.playerUUID);
    }

    public static StopFlightMessage decode(FriendlyByteBuf buffer) {
        return new StopFlightMessage(buffer.readUUID());
    }

    public static void handle(StopFlightMessage message, Supplier<NetworkEvent.Context> context) {
        var player = Minecraft.getInstance().level.getPlayerByUUID(message.playerUUID);
        if (player != null) {
            FlightHelper.stopFlying(player);
        }
        context.get().setPacketHandled(true);
    }
}
