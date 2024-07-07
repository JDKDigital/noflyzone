package cy.jdkdigital.noflyzone.util.compat;

import cy.jdkdigital.noflyzone.NoFlyZone;
import net.minecraft.world.entity.player.Player;

public class EntityTypeCompat
{
    public static boolean isUsingFlyingEntity(Player player) {
        if (player.getVehicle() != null && player.getVehicle().getType().is(NoFlyZone.ENTITY_BLACKLIST)) {
            return true;
        }
        return false;
    }

    public static void dismountFlyingEntity(Player player) {
        if (player.getVehicle() != null && player.getVehicle().getType().is(NoFlyZone.ENTITY_BLACKLIST)) {
            player.getVehicle().ejectPassengers();
        }
    }
}
