package cy.jdkdigital.noflyzone.util.compat;

import com.possible_triangle.create_jetpack.item.JetpackItem;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import cy.jdkdigital.noflyzone.Config;
import net.minecraft.world.entity.player.Player;

public class CreateJetpackCompat
{
    public static boolean isUsingJetpack(Player player) {
        if (player.onGround()) {
            return false;
        }
        return BacktankUtil.getAllWithAir(player).stream().anyMatch(itemStack -> {
            return itemStack.getItem() instanceof JetpackItem jetpack && !player.getCooldowns().isOnCooldown(jetpack);
        });
    }

    public static void disableJetpack(Player player) {
        BacktankUtil.getAllWithAir(player).forEach(itemStack -> {
            if (itemStack.getItem() instanceof JetpackItem jetpack) {
                player.getCooldowns().addCooldown(jetpack, Config.checkInterval);
            }
        });
    }
}
