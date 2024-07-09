package icu.suc.megawalls78.identity.impl.herebrine.skill;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.entity.HerobrineLightning;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.game.record.GameTeam;
import icu.suc.megawalls78.management.GameManager;
import icu.suc.megawalls78.util.EntityUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;

import java.util.List;

public class Wrath extends icu.suc.megawalls78.identity.trait.Skill {

    public Wrath() {
        super("wrath", "Wrath", 100);
    }

    @Override
    public void use(Player player) {
        List<Entity> nearbyEntities = player.getNearbyEntities(5, 5, 5);
        if (nearbyEntities.isEmpty()) {
            EntityUtil.spawn(player.getLocation(), EntityUtil.Type.HEROBRINE_LIGHTNING);
        } else {
            GameManager gameManager = MegaWalls78.getInstance().getGameManager();
            GameTeam team = gameManager.getPlayer(player).getTeam();
            for (Entity nearbyEntity : nearbyEntities) {
                if (nearbyEntity instanceof Player nearbyPlayer) {
                    GamePlayer gameNearbyPlayer = gameManager.getPlayer(nearbyPlayer);
                    if (gameNearbyPlayer != null && !gameNearbyPlayer.getTeam().equals(team)) {
                        EntityUtil.spawn(nearbyPlayer.getLocation(), EntityUtil.Type.HEROBRINE_LIGHTNING, entity -> {
                            ((HerobrineLightning) entity.getHandle()).setTarget(nearbyPlayer);
                            ((LightningStrike) entity).setCausingPlayer(player);
                        });
                    }
                }
            }
        }
    }
}
