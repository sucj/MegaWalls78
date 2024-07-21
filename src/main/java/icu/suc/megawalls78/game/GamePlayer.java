package icu.suc.megawalls78.game;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.event.EnergyChangeEvent;
import icu.suc.megawalls78.event.IdentitySelectEvent;
import icu.suc.megawalls78.event.IncreaseStatsEvent;
import icu.suc.megawalls78.game.record.GameTeam;
import icu.suc.megawalls78.identity.EnergyWay;
import icu.suc.megawalls78.identity.Identity;
import icu.suc.megawalls78.identity.trait.Gathering;
import icu.suc.megawalls78.identity.trait.IActionbar;
import icu.suc.megawalls78.identity.trait.Passive;
import icu.suc.megawalls78.identity.trait.Skill;
import icu.suc.megawalls78.util.SupplierComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;

import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private Map<Skill.Trigger, Skill> skills;
    private List<Passive> passives;
    private Gathering gathering;

    private List<ComponentLike> actionbar;

    public GamePlayer(Player player) {
        this.uuid = player.getUniqueId();
        setIdentity(MegaWalls78.getInstance().getIdentityManager().getPlayerIdentity(uuid));
    }

    public void enablePassives() {
        for (Passive passive : passives) {
            Bukkit.getPluginManager().registerEvents(passive, MegaWalls78.getInstance());
        }
    }

    public void disablePassives() {
        for (Passive passive : passives) {
            passive.unregister();
            HandlerList.unregisterAll(passive);
        }
    }

    public boolean useSkill(Action action, Material material) {
        Skill skill = skills.get(Skill.Trigger.getTrigger(action, material));
        if (skill == null) {
            return false;
        }
        if (energy < skill.getCost()) {
            return false;
        }
        Player player = getBukkitPlayer();
        if (player.isSneaking()) {
            return false;
        }
        if (skill.use(player)) {
            decreaseEnergy(skill.getCost());
            return true;
        }
        return false;
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

    public void increaseEnergy(EnergyWay way) {
        increaseEnergy(identity.getEnergyByWay(way));
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
        this.passives = identity.getPassives(this);
        this.gathering = identity.getGathering(this, passives);

        this.actionbar = Lists.newArrayList();
        Set<Class<? extends Skill>> skillClasses = Sets.newHashSet();
        for (Skill skill : skills.values()) {
            Class<? extends Skill> skillClass = skill.getClass();
            if (!skillClasses.contains(skillClass)) {
                add2Actionbar(Component.translatable("mw78.acb." + skill.getId(), identity.getColor(), TextDecoration.BOLD), SupplierComponent.create(skill::acbValue));
                skillClasses.add(skillClass);
            }
        }
        for (Passive passive : passives) {
            if (passive instanceof IActionbar) {
                add2Actionbar(Component.translatable("mw78.acb." + passive.getId(), identity.getColor(), TextDecoration.BOLD), SupplierComponent.create(((IActionbar) passive)::acbValue));
            }
        }

        IdentitySelectEvent.Post post = new IdentitySelectEvent.Post(uuid, identity);
        Bukkit.getPluginManager().callEvent(post);
    }

    public List<ComponentLike> getActionbar() {
        return actionbar;
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
        IncreaseStatsEvent.Kill event = new IncreaseStatsEvent.Kill(uuid, false);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        kills++;
    }

    public void increaseDeaths() {
        IncreaseStatsEvent.Death event = new IncreaseStatsEvent.Death(uuid, false);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        deaths++;
    }

    public void increaseAssists() {
        IncreaseStatsEvent.Assist event = new IncreaseStatsEvent.Assist(uuid, false);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        assists++;
    }

    public void increaseFinalKills() {
        IncreaseStatsEvent.Kill event = new IncreaseStatsEvent.Kill(uuid, true);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        finalKills++;
    }

    public void increaseFinalDeaths() {
        IncreaseStatsEvent.Death event = new IncreaseStatsEvent.Death(uuid, true);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        finalDeaths++;
    }

    public void increaseFinalAssists() {
        IncreaseStatsEvent.Assist event = new IncreaseStatsEvent.Assist(uuid, true);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        finalAssists++;
    }

    private void add2Actionbar(Component name, ComponentLike value) {
        if (actionbar.size() % 4 == 0) {
            actionbar.addFirst(value);
            actionbar.addFirst(name);
        } else {
            actionbar.addLast(name);
            actionbar.addLast(value);
        }
    }

    public Map<Skill.Trigger, Skill> getSkills() {
        return skills;
    }

    public List<Passive> getPassives() {
        return passives;
    }

    public Gathering getGathering() {
        return gathering;
    }
}
