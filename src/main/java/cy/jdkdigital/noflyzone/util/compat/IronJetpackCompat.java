package cy.jdkdigital.noflyzone.util.compat;

import com.blakebr0.ironjetpacks.util.JetpackUtils;
import net.minecraft.world.entity.player.Player;

public class IronJetpackCompat
{
    public static boolean isUsingJetpack(Player player) {
        var jetpack = JetpackUtils.getEquippedJetpack(player);
        return !jetpack.isEmpty() && JetpackUtils.isEngineOn(jetpack);
    }

    public static void disableJetpack(Player player) {
        var jetpack = JetpackUtils.getEquippedJetpack(player);
        if (!jetpack.isEmpty() && JetpackUtils.isEngineOn(jetpack)) {
            JetpackUtils.toggleEngine(jetpack);
        }
    }
}
