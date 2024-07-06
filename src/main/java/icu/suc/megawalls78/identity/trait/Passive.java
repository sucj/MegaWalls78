package icu.suc.megawalls78.identity.trait;

import icu.suc.megawalls78.game.GamePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class Passive implements Listener {

  private final String id;
  private final String name;

  private GamePlayer player;

  protected Passive(String id, String name) {
    this.id = id;
    this.name = name;
  }

  protected boolean shouldPassive(Player player) {
    return player != null && player.getUniqueId().equals(this.player.getUuid());
  }

  public abstract void unregister();

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public GamePlayer getPlayer() {
    return player;
  }

  public void setPlayer(GamePlayer player) {
    this.player = player;
  }
}
