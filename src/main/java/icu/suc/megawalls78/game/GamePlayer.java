package icu.suc.megawalls78.game;

import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.event.EnergyChangeEvent;
import icu.suc.megawalls78.event.IdentitySelectEvent;
import icu.suc.megawalls78.game.record.GameTeam;
import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.identity.energy.EnergyHit;
import icu.suc.megawalls78.identity.trait.Passive;
import icu.suc.megawalls78.identity.trait.Skill;
import icu.suc.megawalls78.identity.trait.Trigger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GamePlayer {

    private final UUID uuid;

    private Identity identity;
    private GameTeam team;

    private int kills;
    private int deaths;
    private int assists;
    private int finalKills;
    private int finalDeaths;
    private int finalAssists;

    private int energy;
    private Map<Trigger, Skill> skills;
    private List<Passive> passives;

    public GamePlayer(Player player) {
        this.uuid = player.getUniqueId();
        setIdentity(MegaWalls78.getInstance().getIdentityManager().getPlayerIdentity(uuid));
    }

    public List<Passive> getPassives() {
        if (passives == null) {
            passives = identity.getPassives(this);
        }
        return passives;
    }

    public boolean useSkill(Action action, Material material) {
        Skill skill = skills.get(Trigger.getTrigger(action, material));
        if (skill == null) {
            return false;
        }
        if (energy < skill.getCost()) {
            return false;
        }
        decreaseEnergy(skill.getCost());
        skill.use(getBukkitPlayer());
        return true;
    }

    public void setEnergy(int energy) {
        int max = identity.getEnergy();
        if (energy > max) {
            energy = max;
        } else if (energy < 0) {
            energy = 0;
        }

        EnergyChangeEvent event = new EnergyChangeEvent(getBukkitPlayer(), this.energy, energy, max);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        this.energy = energy;
    }

    public void increaseEnergy(int increase) {
        setEnergy(energy + increase);
    }

    public void decreaseEnergy(int decrease) {
        setEnergy(energy - decrease);
    }

    public void increaseEnergy(EnergyHit energyHit) {
        setEnergy(energy + identity.getEnergyHit(energyHit));
    }

    public int getEnergy() {
        return energy;
    }

    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public UUID getUuid() {
        return uuid;
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        IdentitySelectEvent.Pre pre = new IdentitySelectEvent.Pre(uuid, identity);
        Bukkit.getPluginManager().callEvent(pre);
        if (pre.isCancelled()) {
            return;
        }

        this.identity = pre.getIdentity();
        this.skills = identity.getSkills();

        IdentitySelectEvent.Post post = new IdentitySelectEvent.Post(uuid, identity);
        Bukkit.getPluginManager().callEvent(post);
    }

    public GameTeam getTeam() {
        return team;
    }

    public void setTeam(GameTeam team) {
        this.team = team;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getAssists() {
        return assists;
    }

    public int getFinalKills() {
        return finalKills;
    }

    public int getFinalDeaths() {
        return finalDeaths;
    }

    public int getFinalAssists() {
        return finalAssists;
    }

    public void increaseKills() {
        kills++;
    }

    public void increaseDeaths() {
        deaths++;
    }

    public void increaseAssists() {
        assists++;
    }

    public void increaseFinalKills() {
        finalKills++;
    }

    public void increaseFinalDeaths() {
        finalDeaths++;
    }

    public void increaseFinalAssists() {
        finalAssists++;
    }
}
