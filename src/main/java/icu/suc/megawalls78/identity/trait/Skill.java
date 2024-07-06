package icu.suc.megawalls78.identity.trait;

import org.bukkit.entity.Player;

public abstract class Skill {

  private final String id;
  private final String name;
  private final int cost;

  protected Skill(String id, String name, int cost) {
    this.id = id;
    this.name = name;
    this.cost = cost;
  }

  public abstract void use(Player player);

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public int getCost() {
    return cost;
  }
}
