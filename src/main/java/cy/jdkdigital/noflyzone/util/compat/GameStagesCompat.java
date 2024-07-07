package cy.jdkdigital.noflyzone.util.compat;

import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.world.entity.player.Player;

public class GameStagesCompat
{
    public static boolean hasUnlockedStage(Player player, String stage) {
        return GameStageHelper.hasStage(player, stage);
    }

    public static boolean stageExists(String stage) {
        return stage != null && GameStageHelper.isStageKnown(stage);
    }
}
