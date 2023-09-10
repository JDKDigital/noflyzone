package cy.jdkdigital.noflyzone.event;

import cy.jdkdigital.noflyzone.Config;
import cy.jdkdigital.noflyzone.NoFlyZone;
import cy.jdkdigital.noflyzone.util.CompatHandler;
import cy.jdkdigital.noflyzone.util.FlightHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NoFlyZone.MODID)
public class EventHandler
{
    @SubscribeEvent
    public static void onEntityTeleport(EntityTeleportEvent event) {
        if (!Config.allowTeleporting && event.getEntity() instanceof Player player && !FlightHelper.isAllowedToFly(player)) {
            event.setCanceled(true);
            FlightHelper.sendTeleportNotice(player);
        }
    }

    @SubscribeEvent
    public static void onEntityUseItem(PlayerInteractEvent.RightClickItem event) {
        if (!Config.allowFlyingDevices && CompatHandler.isFlyingDevice(event.getItemStack()) && !FlightHelper.isAllowedToFly(event.getEntity())) {
            event.setCanceled(true);
            FlightHelper.sendFlightNotice(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player.tickCount % Config.checkInterval == 0 && FlightHelper.isFlying(event.player) && !event.player.isCreative()) {
            if (!FlightHelper.isAllowedToFly(event.player)) {
                FlightHelper.stopFlying(event.player);
            }
        }
    }
}
