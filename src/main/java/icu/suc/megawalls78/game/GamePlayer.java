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
import icu.suc.megawalls78.identity.trait.passive.Passive;
import icu.suc.megawalls78.identity.trait.skill.DurationSkill;
import icu.suc.megawalls78.identity.trait.skill.Skill;
import icu.suc.megawalls78.util.ComponentUtil;
import icu.suc.megawalls78.util.SupplierComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;

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

    private float energy;
    private Map<Skill.Trigger, Skill> skills;
    private Set<Skill> uniqueSkills;
    private List<Passive> passives;
    private Gathering gathering;

    private List<ComponentLike> actionbar;

    private GameTeam tracking;

    private double damageDealt;
    private double damageTaken;
    private double damageGuard;
    private double damageWither;
    private double healed;

    public GamePlayer(Player player) {
        this.uuid = player.getUniqueId();
        setIdentity(MegaWalls78.getInstance().getIdentityManager().getPlayerIdentity(uuid));
    }

    public void enablePassives() {
        for (Passive passive : passives) {
            passive.register();
            Bukkit.getPluginManager().registerEvents(passive, MegaWalls78.getInstance());
        }
    }

    public void disablePassives() {
        for (Passive passive : passives) {
            passive.unregister();
            HandlerList.unregisterAll(passive);
        }
    }

    public void stopDurationSkills() {
        for (Skill skill : uniqueSkills) {
            if (skill instanceof DurationSkill durationSkill) {
                durationSkill.stop();
            }
        }
    }

    public boolean useSkill(Player player, Action action, Material material) {
        Skill.Trigger trigger = Skill.Trigger.getTrigger(action, material);
        if (trigger == null) {
            return false;
        }
        if (player.isSneaking() != MegaWalls78.getInstance().getTriggerManager().sneak(player.getUniqueId(), trigger)) {
            return false;
        }
        Skill skill = skills.get(trigger);
        if (skill == null) {
            return false;
        }
        if (skill.use(player)) {
            decreaseEnergy(skill.getCost());
            return true;
        }
        return false;
    }

    public void setEnergy(float energy) {
        float max = identity.getEnergy();
        if (energy > max) {
            energy = max;
        } else if (energy < 0) {
            energy = 0;
        }

        if (this.energy == energy) {
            return;
        }

        EnergyChangeEvent event = new EnergyChangeEvent(getBukkitPlayer(), this.energy, energy, max);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        this.energy = energy;
    }

    public void increaseEnergy(float increase) {
        setEnergy(energy + increase);
    }

    public void decreaseEnergy(float decrease) {
        setEnergy(energy - decrease);
    }

    public void increaseEnergy(EnergyWay way) {
        if (way.equals(EnergyWay.DM)) {
            if (getEnergy() >= Math.min(identity.getEnergyByWay(EnergyWay.MELEE_PER), identity.getEnergyByWay(EnergyWay.BOW_PER)) * 2) {
                increaseEnergy(identity.getEnergyByWay(way));
            }
        } else {
            increaseEnergy(identity.getEnergyByWay(way));
        }
    }

    public float getEnergy() {
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
        this.passives = identity.getPassives(this);
        this.skills = identity.getSkills(this, passives);
        this.uniqueSkills = Sets.newHashSet(skills.values());
        this.gathering = identity.getGathering(this, passives);

        this.actionbar = Lists.newArrayList();
        for (Skill skill : uniqueSkills) {
            add2Actionbar(Component.translatable("mw78.acb." + skill.getId(), identity.getColor(), TextDecoration.BOLD), SupplierComponent.create(skill::acb));
        }
        for (Passive passive : passives) {
            if (passive instanceof IActionbar) {
                add2Actionbar(Component.translatable("mw78.acb." + passive.getId(), identity.getColor(), TextDecoration.BOLD), SupplierComponent.create(((IActionbar) passive)::acb));
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
        MegaWalls78.getInstance().getGameManager().getTeamPlayersMap().computeIfAbsent(team, k -> Sets.newHashSet()).add(this);
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

    public void increaseKills(PlayerDeathEvent playerDeathEvent) {
        IncreaseStatsEvent.Kill event = new IncreaseStatsEvent.Kill(this, false, playerDeathEvent);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        kills++;

        Bukkit.getScheduler().runTask(MegaWalls78.getInstance(), () -> {
            Player player = getBukkitPlayer();
            if (player != null) {
                ComponentUtil.sendMessage(Component.translatable("mw78.message.kill", NamedTextColor.AQUA), player);
            }
        });
    }

    public void increaseDeaths() {
        IncreaseStatsEvent.Death event = new IncreaseStatsEvent.Death(this, false);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        deaths++;
    }

    public void increaseAssists() {
        IncreaseStatsEvent.Assist event = new IncreaseStatsEvent.Assist(this, false);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        assists++;

        Bukkit.getScheduler().runTask(MegaWalls78.getInstance(), () -> {
            Player player = getBukkitPlayer();
            if (player != null) {
                ComponentUtil.sendMessage(Component.translatable("mw78.message.assist", NamedTextColor.AQUA), player);
            }
        });
    }

    public void increaseFinalKills(PlayerDeathEvent playerDeathEvent) {
        IncreaseStatsEvent.Kill event = new IncreaseStatsEvent.Kill(this, true, playerDeathEvent);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        finalKills++;

        Bukkit.getScheduler().runTask(MegaWalls78.getInstance(), () -> {
            Player player = getBukkitPlayer();
            if (player != null) {
                ComponentUtil.sendMessage(Component.translatable("mw78.message.final_kill", NamedTextColor.AQUA), player);
            }
        });
    }

    public void increaseFinalDeaths() {
        IncreaseStatsEvent.Death event = new IncreaseStatsEvent.Death(this, true);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        finalDeaths++;
    }

    public void increaseFinalAssists() {
        IncreaseStatsEvent.Assist event = new IncreaseStatsEvent.Assist(this, true);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        finalAssists++;
        Bukkit.getScheduler().runTask(MegaWalls78.getInstance(), () -> {
            Player player = getBukkitPlayer();
            if (player != null) {
                ComponentUtil.sendMessage(Component.translatable("mw78.message.final_assist", NamedTextColor.AQUA), player);
            }
        });
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

    public GameTeam getTracking() {
        if (tracking == null || MegaWalls78.getInstance().getGameManager().isEliminated(tracking)) {
            setTracking(team);
        }
        return tracking;
    }

    public void setTracking(GameTeam tracking) {
        this.tracking = tracking;
    }

    public double getDamageDealt() {
        return damageDealt;
    }

    public double getDamageTaken() {
        return damageTaken;
    }

    public double getDamageGuard() {
        return damageGuard;
    }

    public double getDamageWither() {
        return damageWither;
    }

    public double getHealed() {
        return healed;
    }

    public void increaseDamageDealt(double damageDealt) {
        this.damageDealt += damageDealt;
    }

    public void increaseDamageTaken(double damageTaken) {
        this.damageTaken += damageTaken;
    }

    public void increaseDamageWither(double damageWither) {
        this.damageWither += damageWither;
    }

    public void increaseDamageGuard(double damageGuard) {
        this.damageGuard += damageGuard;
    }
}
