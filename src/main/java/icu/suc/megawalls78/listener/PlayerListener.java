package icu.suc.megawalls78.listener;

import com.google.common.collect.Lists;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.game.GameState;
import icu.suc.megawalls78.game.record.GameTeam;
import icu.suc.megawalls78.gui.IdentityGui;
import icu.suc.megawalls78.identity.energy.EnergyHit;
import icu.suc.megawalls78.identity.trait.Passive;
import icu.suc.megawalls78.management.GameManager;
import icu.suc.megawalls78.util.*;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.TranslationArgument;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.List;
import java.util.Objects;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        Player player = event.getPlayer();
        if (gameManager.inWaiting()) {
            player.getInventory().setItem(0, IdentityGui.trigger(player));
            MegaWalls78.getInstance().getSkinManager().applySkin(player);
            event.joinMessage(Component.translatable("multiplayer.player.joined", player.displayName().color(LP.getNameColor(player)))
                    .append(ComponentUtil.BLANK_COMPONENT)
                    .append(Component.translatable("mw78.online", Component.text(gameManager.getPlayers().values().size(), NamedTextColor.WHITE), Component.text(MegaWalls78.getInstance().getConfigManager().maxPlayer, NamedTextColor.WHITE)))
                    .color(NamedTextColor.AQUA));
            if (gameManager.getState().equals(GameState.COUNTDOWN)) {
                long timer = gameManager.getRunner().getTimer();
                if (timer != MegaWalls78.getInstance().getConfigManager().waitingTime && timer > 10000L || timer < 10000L && timer > 5000L) {
                    ComponentUtil.sendMessage(Component.translatable("mw78.start.in", NamedTextColor.AQUA, Component.translatable("mw78.seconds", ComponentUtil.second(timer))), player);
                    ComponentUtil.sendTitle(Component.empty(), ComponentUtil.second(timer), ComponentUtil.ONE_SEC_TIMES, player);
                }
            }
        } else {
            event.joinMessage(null);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        Player player = event.getPlayer();
        if (gameManager.inWaiting()) {
            event.quitMessage(Component.translatable("multiplayer.player.left", NamedTextColor.AQUA, player.displayName().color(LP.getNameColor(player))));
            gameManager.removePlayer(player);
        } else if (gameManager.inFighting()) {
            if (gameManager.isSpectator(player)) {
                gameManager.removeSpectator(player);
            } else {
                player.setHealth(0);
            }
            event.quitMessage(null);
        } else {
            event.quitMessage(null);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        Player player = event.getEntity();
        List<ItemStack> drops = event.getDrops();
        if (gameManager.getState().equals(GameState.OPENING)) {
            drops.clear();
        } else if (gameManager.inFighting()) {
            if (gameManager.isSpectator(player)) {
                drops.clear();
            } else {
                GamePlayer gamePlayer = gameManager.getPlayer(player);
                drops.removeIf(ItemUtil::isSoulBound);
                Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(20);
                DamageSource damageSource = event.getDamageSource();
                GameTeam team = gamePlayer.getTeam();
                boolean dead = gameManager.isWitherDead(team);
//                if (causingEntity instanceof Player causingPlayer) {
//                    Component component = Component.translatable("death.attack." + damageSource.getDamageType().getTranslationKey(), NamedTextColor.GRAY, player.displayName().color(team.color()), causingPlayer.displayName().color(gameManager.getPlayer(causingPlayer).getTeam().color()));
//                    if (dead) {
//                        Bukkit.broadcast(component.append(Component.text(" FINAL KILL!", NamedTextColor.AQUA, TextDecoration.BOLD)));
//                        gameManager.getPlayer(causingPlayer).increaseFinalKills();
//                    } else {
//                        Bukkit.broadcast(component);
//                        gameManager.getPlayer(causingPlayer).increaseKills();
//                    }
//                    gameManager.saveAssists(player, causingPlayer);
//                } else {
//                    Player killer = player.getKiller();
//                    if (killer == null) {
//                        if (causingEntity instanceof Wither wither) {
//                            Component component = Component.translatable("death.attack." + damageSource.getDamageType().getTranslationKey(), NamedTextColor.GRAY, player.displayName().color(team.color()), wither.name());
//                            if (dead) {
//                                Bukkit.broadcast(component.append(Component.text(" FINAL KILL!", NamedTextColor.AQUA, TextDecoration.BOLD)));
//                            } else {
//                                Bukkit.broadcast(component);
//                            }
//                        } else {
//                            Component component = Component.translatable("death.attack." + damageSource.getDamageType().getTranslationKey(), NamedTextColor.GRAY, player.displayName().color(team.color()));
//                            if (dead) {
//                                Bukkit.broadcast(component.append(Component.text(" FINAL KILL!", NamedTextColor.AQUA, TextDecoration.BOLD)));
//                            } else {
//                                Bukkit.broadcast(component);
//                            }
//                        }
//                    } else {
//                        Component component = Component.translatable("death.attack." + damageSource.getDamageType().getTranslationKey() + ".player", NamedTextColor.GRAY, player.displayName().color(team.color()), killer.displayName().color(gameManager.getPlayer(killer).getTeam().color()));
//                        if (dead) {
//                            Bukkit.broadcast(component.append(Component.text(" FINAL KILL!", NamedTextColor.AQUA, TextDecoration.BOLD)));
//                            gameManager.getPlayer(killer).increaseFinalKills();
//                        } else {
//                            Bukkit.broadcast(component);
//                            gameManager.getPlayer(killer).increaseKills();
//                        }
//                        gameManager.saveAssists(player, killer);
//                    }
//                }
                if (event.deathMessage() instanceof TranslatableComponent component) {
                    Component deathMessage = component;
                    String key = component.key();
                    if (key.endsWith(".item")) {
                        List<TranslationArgument> arguments = Lists.newArrayList(component.arguments());
                        arguments.removeLast();
                        component = component.arguments(arguments);
                        deathMessage = component.key(key.substring(0, key.length() - ".item".length()));
                    }
                    if (dead) {
                        deathMessage = deathMessage.append(ComponentUtil.BLANK_COMPONENT).append(Component.translatable("mw78.kill.final", NamedTextColor.AQUA, TextDecoration.BOLD));
                    }
                    event.deathMessage(deathMessage.color(NamedTextColor.GRAY));
                }
                Player killer = player.getKiller();
                if (damageSource.getCausingEntity() instanceof Player || killer != null) {
                    if (dead) {
                        gameManager.getPlayer(killer).increaseFinalKills();
                    } else {
                        gameManager.getPlayer(killer).increaseKills();
                    }
                    gameManager.saveAssists(player, killer, dead);
                }
                if (dead) {
                    gamePlayer.increaseFinalDeaths();
                    boolean eliminated = true;
                    for (Player teammate : gameManager.getTeammates(player)) {
                        if (gameManager.getPlayer(teammate).getFinalDeaths() == 0) {
                            eliminated = false;
                            break;
                        }
                    }
                    gameManager.setTeamEliminate(team, eliminated);
                } else {
                    gamePlayer.increaseDeaths();
                }
                for (Passive passive : gamePlayer.getPassives()) {
                    passive.unregister();
                    HandlerList.unregisterAll(passive);
                }
                Bukkit.broadcast(event.deathMessage());
                gameManager.addSpectator(player, gamePlayer.getFinalDeaths() == 0);
            }
        } else {
            drops.clear();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerSpawn(PlayerSpawnLocationEvent event) {
        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        Player player = event.getPlayer();
        MegaWalls78.getInstance().getSkinManager().addPlayerSkin(player);
        if (gameManager.inWaiting()) {
            gameManager.addPlayer(player);
            event.setSpawnLocation(RandomUtil.getRandomSpawn(gameManager.getMap().spawn()));
            player.setGameMode(GameMode.ADVENTURE);
        } else if (gameManager.inFighting()) {
            GamePlayer gamePlayer = gameManager.getPlayer(player);
            if (gamePlayer == null) {
                gameManager.addSpectator(player);
            } else if (gamePlayer.getFinalDeaths() == 0) {
                gameManager.addSpectator(player, true);
            }
            event.setSpawnLocation(gameManager.getMap().spectator());
            player.setGameMode(GameMode.ADVENTURE);
            Bukkit.getScheduler().runTask(MegaWalls78.getInstance(), () -> {
                player.setAllowFlight(true);
                player.setFlying(true);
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 1));
            });
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        Player player = event.getPlayer();
        if (gameManager.inWaiting()) {
            event.setRespawnLocation(RandomUtil.getRandomSpawn(gameManager.getMap().spawn()));
            player.setGameMode(GameMode.ADVENTURE);
        } else if (gameManager.isSpectator(player)) {
            event.setRespawnLocation(gameManager.getMap().spectator());
            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(true);
            player.setFlying(true);
            Bukkit.getScheduler().runTask(MegaWalls78.getInstance(), () -> player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 1)));
        } else {
            GamePlayer gamePlayer = gameManager.getPlayer(player);
            gamePlayer.getIdentity().getKit().equip(player);
            gamePlayer.setEnergy(gamePlayer.getEnergy());
            event.setRespawnLocation(RandomUtil.getRandomSpawn(gamePlayer.getTeam().spawn()));
            player.setGameMode(GameMode.SURVIVAL);
            Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(40);
            for (Passive passive : gamePlayer.getPassives()) {
                Bukkit.getPluginManager().registerEvents(passive, MegaWalls78.getInstance());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            GameManager gameManager = MegaWalls78.getInstance().getGameManager();
            if (gameManager.isSpectator(player)) {
                event.setCancelled(true);
            } else if (!gameManager.getState().equals(GameState.OPENING) && gameManager.inFighting()) {
                Entity causingEntity = event.getDamageSource().getCausingEntity();
                if (causingEntity instanceof Player causingPlayer) {
//                    if (gameManager.getPlayer(causingPlayer).getTeam().equals(team) && causingEntity != player) {
//                        event.setCancelled(true);
//                    } else {
                    if (causingEntity != player) {
                        if (event.getDamageSource().getDirectEntity() instanceof Arrow) {
                            gameManager.getPlayer(causingPlayer).increaseEnergy(EnergyHit.BOW);
                        } else if (!event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)) {
                            gameManager.getPlayer(causingPlayer).increaseEnergy(EnergyHit.MELEE);
                        }
                        gameManager.addAssist(player, causingPlayer);
                    }
//                    }
                } else if (causingEntity instanceof Wither wither) {
                    if (gameManager.getPlayer(player).getTeam().equals(gameManager.getWitherTeam(wither))) {
                        event.setCancelled(true);
                    }
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        Player player = event.getPlayer();
        if (gameManager.isSpectator(player)) {
            event.setCancelled(true);
        } else if (gameManager.inFighting()) {
            if (!gameManager.getPlayer(player).useSkill(event.getAction(), event.getMaterial())) {
                switch (event.getAction()) {
                    case RIGHT_CLICK_BLOCK:
                    case RIGHT_CLICK_AIR: {
                        if (ItemUtil.isEnderChest(event.getItem())) {
                            InventoryUtils.openEnderChest(player);
                        }
                    }
                }
            }
        } else if (gameManager.inWaiting()) {
            event.setCancelled(true);
            switch (event.getAction()) {
                case RIGHT_CLICK_BLOCK:
                case RIGHT_CLICK_AIR: {
                    if (event.getMaterial().equals(gameManager.getPlayer(player).getIdentity().getMaterial())) {
                        IdentityGui.open(player, 1);
                    }
                }
            }
        } else {
            event.setCancelled(true);
        }

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player player) {
            GameManager gameManager = MegaWalls78.getInstance().getGameManager();
            if (gameManager.isSpectator(player) || !gameManager.inFighting()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        Player player = event.getPlayer();
        if (gameManager.inWaiting()) {
            event.setCancelled(true);
        } else if (gameManager.inFighting()) {
            if (gameManager.isSpectator(player)) {
                event.setCancelled(true);
            } else {
                ItemStack itemStack = event.getItemDrop().getItemStack();
                if (ItemUtil.isSoulBound(itemStack)) {
                    Integer slot = InventoryListener.LAST_SLOTS.get(player.getUniqueId());
                    if (slot == null) {
                        event.setCancelled(true);
                    } else {
                        ItemStack slotItem = player.getInventory().getItem(slot);
                        if (slotItem == null) {
                            event.setCancelled(true);
                        } else {
                            event.getItemDrop().setItemStack(slotItem);
                        }
                        player.getInventory().setItem(slot, itemStack);
                        InventoryListener.LAST_SLOTS.remove(player.getUniqueId());
                    }
                }
            }
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        if (gameManager.inFighting()) {
            GameTeam team = gameManager.getRunner().inPalace(event.getTo());
            Player player = event.getPlayer();
            if (team == null) {
                for (BossBar bossBar : player.activeBossBars()) {
                    if (gameManager.getWitherBossBars().containsValue(bossBar)) {
                        player.hideBossBar(bossBar);
                        return;
                    }
                }
            } else if (!gameManager.getWither(team).isDead()) {
                player.showBossBar(gameManager.getBossBar(team));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (!MegaWalls78.getInstance().getGameManager().inFighting()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerExpChange(EntitySpawnEvent event) {
        if (event.getEntity() instanceof ExperienceOrb) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPrePlayerAttackEntity(PrePlayerAttackEntityEvent event) {
        Player player = event.getPlayer();
        if (MegaWalls78.getInstance().getGameManager().isSpectator(player)) {
            event.setCancelled(true);
        } else {
            event.setCancelled(MegaWalls78.getInstance().getGameManager().getPlayer(player).useSkill(Action.LEFT_CLICK_AIR, player.getItemInHand().getType()));
        }
    }
}
