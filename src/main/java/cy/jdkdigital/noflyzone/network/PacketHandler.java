package cy.jdkdigital.noflyzone.network;

import cy.jdkdigital.noflyzone.NoFlyZone;
import cy.jdkdigital.noflyzone.network.packets.StopFlightMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler
{
    private static int id = 0;
    private static final String PROTOCOL_VERSION = "1";

    public static SimpleChannel channel = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(NoFlyZone.MODID, "noflyzone"))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();

    public static void init() {
        channel.registerMessage(id++, StopFlightMessage.class, StopFlightMessage::encode, StopFlightMessage::decode, StopFlightMessage::handle);
    }

    public static void sendStopFlightToPlayer(ServerPlayer player) {
        channel.send(PacketDistributor.PLAYER.with(() -> player), new StopFlightMessage(player.getUUID()));
    }
}
