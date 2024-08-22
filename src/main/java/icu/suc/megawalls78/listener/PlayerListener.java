package icu.suc.megawalls78.listener;

import com.destroystokyo.paper.event.player.PlayerClientOptionsChangeEvent;
import com.google.common.collect.Lists;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.event.IncreaseStatsEvent;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.game.GameRunner;
import icu.suc.megawalls78.game.GameState;
import icu.suc.megawalls78.game.record.GameTeam;
import icu.suc.megawalls78.gui.*;
import icu.suc.megawalls78.identity.EnergyWay;
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
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class PlayerListener implements Listener {

    private static final Set<String> NEED_PLAYER_KEYS = Set.of("death.attack.anvil", "death.attack.cactus", "death.attack.cramming", "death.attack.dragonBreath", "death.attack.drown", "death.attack.dryout", "death.attack.fall", "death.attack.fallingBlock", "death.attack.fallingStalactite", "death.attack.fireworks",  "death.attack.flyIntoWall", "death.attack.freeze", "death.attack.generic", "death.attack.genericKill", "death.attack.hotFloor", "death.attack.inFire", "death.attack.inWall", "death.attack.lava", "death.attack.lightningBolt", "death.attack.magic", "death.attack.onFire", "death.attack.outOfWorld", "death.attack.outsideBorder", "death.attack.sonic_boom", "death.attack.stalagmite", "death.attack.starve", "death.attack.sting", "death.attack.sweetBerryBush", "death.attack.wither");

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        Player player = event.getPlayer();
        if (gameManager.inWaiting()) {
            player.getInventory().setItem(0, IdentityGui.trigger(player));
            player.getInventory().setItem(1, SkinGui.trigger(player));
            player.getInventory().setItem(2, PatternGui.trigger(player));
            player.getInventory().setItem(3, TrimGui.trigger(player));
            player.getInventory().setItem(7, TeamGui.trigger(player));
            MegaWalls78.getInstance().getSkinManager().applySkin(player);
            event.joinMessage(Component.translatable("multiplayer.player.joined", player.teamDisplayName().color(LP.getNameColor(player.getUniqueId())))
                    .append(Component.space())
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
        player.sendPlayerListHeader(Component.text("748889666", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD));

        Component footer = Component.text("MC.SUC.ICU", NamedTextColor.AQUA, TextDecoration.BOLD);
        if (gameManager.inFighting()) {
            player.sendPlayerListFooter(gameManager.footer().appendNewline().append(footer));
        } else {
            player.sendPlayerListFooter(footer);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (gameManager.inWaiting()) {
            event.quitMessage(Component.translatable("multiplayer.player.left", NamedTextColor.AQUA, player.teamDisplayName().color(LP.getNameColor(uuid))));
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
        MegaWalls78.getInstance().getIdentityManager().clearCache(uuid);
        MegaWalls78.getInstance().getEquipmentManager().clearCache(uuid);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        Player player = event.getEntity();
        List<ItemStack> drops = event.getDrops();
        if (gameManager.inFighting()) {
            if (gameManager.isSpectator(player)) {
                drops.clear();
            } else {
                GamePlayer gamePlayer = gameManager.getPlayer(player);
                drops.removeIf(ItemUtil::mw78SoulBound);
                Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(20);
                GameTeam team = gamePlayer.getTeam();
                boolean dead = gameManager.isWitherDead(team);
                if (event.deathMessage() instanceof TranslatableComponent deathMessage) {
                    String key = deathMessage.key();
                    if (key.endsWith(".item")) {
                        List<TranslationArgument> arguments = Lists.newArrayList(deathMessage.arguments());
                        arguments.removeLast();
                        deathMessage = deathMessage.arguments(arguments);
                        deathMessage = deathMessage.key(key.substring(0, key.length() - ".item".length()));
                        String keyed = deathMessage.key();
                        if (NEED_PLAYER_KEYS.contains(keyed)) {
                            deathMessage = deathMessage.key(keyed + ".player");
                        }
                    }
                    if (dead) {
                        deathMessage = deathMessage.append(Component.space()).append(Component.translatable("mw78.kill.final", NamedTextColor.AQUA, TextDecoration.BOLD));
                    }
                    event.deathMessage(deathMessage.color(NamedTextColor.GRAY));
                }
                UUID killerId = PlayerUtil.getKiller(event);
                GamePlayer gamePlayerKiller = gameManager.getPlayer(killerId);
                if (gamePlayerKiller == null) {
                    killerId = null;
                }
                if (killerId != null) {
                    if (dead) {
                        gameManager.getPlayer(killerId).increaseFinalKills(event);
                    } else {
                        gameManager.getPlayer(killerId).increaseKills(event);
                    }
                    gameManager.saveAssists(player, killerId, dead);
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
                gamePlayer.stopDurationSkills();
                gamePlayer.disablePassives();
                ComponentUtil.sendMessage(event.deathMessage(), Bukkit.getOnlinePlayers());
                if (gamePlayer.getFinalDeaths() != 0) {
                    gameManager.addSpectator(player);
                }
            }
            PlayerUtil.setLastDeathLocation(player, player.getLocation());
        } else {
            drops.clear();
        }
        event.deathMessage(null);
        Bukkit.getScheduler().runTaskLater(MegaWalls78.getInstance(), () -> {
            if (player.isDead()) {
                player.spigot().respawn();
            }
        }, 10L);
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
            if (gamePlayer == null || gamePlayer.getFinalDeaths() != 0) {
                gameManager.addSpectator(player);
                event.setSpawnLocation(gameManager.getMap().spectator());
                Bukkit.getScheduler().runTask(MegaWalls78.getInstance(), () -> {
                    if (gamePlayer != null && gamePlayer.getFinalDeaths() != 0) {
                        ComponentUtil.sendTitle(Component.translatable("mw78.died", NamedTextColor.RED), Component.empty(), ComponentUtil.DEFAULT_TIMES, player);
                    }
                    player.setGameMode(GameMode.SPECTATOR);
//                    player.setAllowFlight(true);
//                    player.setFlying(true);
//                    player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 1));
                });
            } else if (gamePlayer.getFinalDeaths() == 0) {
                event.setSpawnLocation(RandomUtil.getRandomSpawn(gamePlayer.getTeam().spawn()));
                Bukkit.getScheduler().runTask(MegaWalls78.getInstance(), () -> {
                    player.setGameMode(GameMode.SURVIVAL);
                    Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(40);
                    player.setHealth(40);
                    PlayerUtil.setStarvation(player, 20);
                    MegaWalls78.getInstance().getSkinManager().applySkin(player);
                    gamePlayer.getIdentity().getKit().equip(player);
                    float energy = gamePlayer.getEnergy();
                    player.setLevel((int) energy);
                    player.setExp(energy / gamePlayer.getIdentity().getEnergy());
                    gamePlayer.enablePassives();
                });
            }
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
            if (gameManager.getPlayer(player) != null) {
                ComponentUtil.sendTitle(Component.translatable("mw78.died", NamedTextColor.RED), Component.empty(), ComponentUtil.DEFAULT_TIMES, player);
                MegaWalls78.getInstance().getSkinManager().resetSkin(player);
            }
            Location deathLocation = PlayerUtil.getLastDeathLocation(player);
            if (deathLocation == null) {
                event.setRespawnLocation(gameManager.getMap().spectator());
            } else {
                event.setRespawnLocation(deathLocation);
            }
            player.setGameMode(GameMode.SPECTATOR);
//            player.setAllowFlight(true);
//            player.setFlying(true);
//            Bukkit.getScheduler().runTask(MegaWalls78.getInstance(), () -> player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 1)));
        } else {
            GamePlayer gamePlayer = gameManager.getPlayer(player);
            event.setRespawnLocation(RandomUtil.getRandomSpawn(gamePlayer.getTeam().spawn()));
            Bukkit.getScheduler().runTask(MegaWalls78.getInstance(), () -> {
                player.setGameMode(GameMode.SURVIVAL);
                Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(40);
                player.setHealth(40);
                PlayerUtil.setStarvation(player, 20);
                gamePlayer.getIdentity().getKit().equip(player);
                float energy = gamePlayer.getEnergy();
                player.setLevel((int) energy);
                player.setExp(energy / gamePlayer.getIdentity().getEnergy());
                gamePlayer.enablePassives();
            });
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
                if (causingEntity != null) {
                    Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
                    if (Objects.equals(scoreboard.getEntityTeam(causingEntity), scoreboard.getPlayerTeam(player)) || EntityUtil.traceableTeamed(causingEntity, player)) {
                        event.setCancelled(true);
                    }
//                    if (causingEntity instanceof Player causingPlayer && !causingPlayer.equals(player)) {
//                        GamePlayer gamePlayerCause = gameManager.getPlayer(causingPlayer);
//                        if (event.getDamageSource().getDirectEntity() instanceof AbstractArrow) {
//                            gamePlayer.increaseEnergy(EnergyWay.BOW_WHEN);
//                            gamePlayerCause.increaseEnergy(EnergyWay.BOW_PER);
//                        } else if (!event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)) {
//                            gamePlayer.increaseEnergy(EnergyWay.MELEE_WHEN);
//                            gamePlayerCause.increaseEnergy(EnergyWay.MELEE_PER);
//                        }
//                        gameManager.addAssist(player, causingPlayer);
//                    }
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDamagePost(EntityDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }
        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        GamePlayer gamePlayer = null;
        GamePlayer gamePlayerCause = null;
        Entity causingEntity = event.getDamageSource().getCausingEntity();
        if (causingEntity == null) {
            return;
        }
        if (event.getEntity() instanceof LivingEntity entity) {
            if (causingEntity.equals(entity)) {
                return;
            }
            Player player = null;
            if (entity instanceof Player) {
                player = (Player) entity;
                gamePlayer = gameManager.getPlayer(player);
            }
            Player causingPlayer = null;
            if (causingEntity instanceof Player) {
                causingPlayer = (Player) causingEntity;
                gamePlayerCause = gameManager.getPlayer(causingPlayer);
            }
            if (gamePlayer != null && gamePlayerCause != null) {
                if (EntityUtil.isArrowAttack(event)) {
                    gamePlayer.increaseEnergy(EnergyWay.BOW_WHEN);
                    gamePlayerCause.increaseEnergy(EnergyWay.BOW_PER);
                } else if (EntityUtil.isMeleeAttack(event)) {
                    gamePlayer.increaseEnergy(EnergyWay.MELEE_WHEN);
                    gamePlayerCause.increaseEnergy(EnergyWay.MELEE_PER);
                }
                gameManager.addAssist(player, causingPlayer);
            }
            double damage = Math.min(entity.getHealth(), event.getFinalDamage());
            if (gamePlayer != null) {
                gamePlayer.increaseDamageTaken(damage - event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING));
            }
            if (gamePlayerCause != null) {
                gamePlayerCause.increaseDamageDealt(damage);
                if (gamePlayerCause.getTeam().equals(gameManager.getRunner().inPalace(causingPlayer.getLocation()))) {
                    gamePlayerCause.increaseDamageGuard(damage);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        Player player = event.getPlayer();
        if (gameManager.isSpectator(player)) {
            event.setCancelled(true);
        } else {
            Action action = event.getAction();
            if (gameManager.inFighting()) {
                switch (action) {
                    case RIGHT_CLICK_BLOCK:
                    case RIGHT_CLICK_AIR: {
                        if (ItemUtil.isMW78Item(event.getItem(), ItemUtil.ENDER_CHEST)) {
                            InventoryUtil.openEnderChest(player);
                            event.setCancelled(true);
                            return;
                        } else if (ItemUtil.isMW78Item(event.getItem(), ItemUtil.COMPASS) && gameManager.getRunner().isDm()) {
                            GamePlayer gamePlayer = gameManager.getPlayer(player);
                            if (gameManager.getTeamPlayersMap().size() > 1) {
                                List<GameTeam> teams = gameManager.getTeams();
                                int i = teams.indexOf(gamePlayer.getTracking()) + 1;
                                GameTeam team = teams.get(i % teams.size());
                                while (gameManager.isEliminated(team)) {
                                    team = teams.get((++i) % teams.size());
                                }
                                gamePlayer.setTracking(team);
                            }
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
                if (player.isSneaking() || !BlockUtil.canInteract(event.getClickedBlock()) || action.equals(Action.LEFT_CLICK_BLOCK)) {
                    gameManager.getPlayer(player).useSkill(player, action, event.getMaterial());
                }
            } else if (gameManager.inWaiting()) {
                switch (action) {
                    case RIGHT_CLICK_BLOCK:
                    case RIGHT_CLICK_AIR: {
                        int slot = player.getInventory().getHeldItemSlot();
                        switch (slot) {
                            case 0 -> IdentityGui.open(player, 1);
                            case 1 -> SkinGui.open(player, 1);
                            case 2 -> PatternGui.open(player, 1);
                            case 3 -> TrimGui.open(player, 1);
                            case 7 -> TeamGui.open(player, 1);
                        }
                    }
                }
                event.setCancelled(true);
            } else {
                event.setCancelled(true);
            }
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
                if (ItemUtil.mw78SoulBound(itemStack)) {
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
            GameRunner runner = gameManager.getRunner();
            Location to = event.getTo();
            GameTeam team = runner.inPalace(to);
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
            if (gameManager.isSpectator(player)) {
                return;
            }
            GameState state = gameManager.getState();
            if (state.equals(GameState.OPENING)) {
                Vector vector = to.toBlockLocation().toVector();
                if (!runner.getSpawn(gameManager.getPlayer(player).getTeam()).contains(vector)) {
                    event.setCancelled(true);
                }
            } else if (state.equals(GameState.PREPARING)) {
                GameTeam playerTeam = gameManager.getPlayer(player).getTeam();
                Vector vector = to.toBlockLocation().toVector();
                if (!(runner.getTeamRegion(playerTeam).contains(vector) || runner.getSpawn(playerTeam).contains(vector))) {
                    event.setCancelled(true);
                }
            }
            if (event.isCancelled()) {
                Entity vehicle = player.getVehicle();
                if (vehicle == null) {
                    return;
                }
                vehicle.remove();
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
        if (gameManager.isSpectator(event.getPlayer()) || !gameManager.inFighting()) {
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
            event.setCancelled(MegaWalls78.getInstance().getGameManager().getPlayer(player).useSkill(player, Action.LEFT_CLICK_AIR, player.getEquipment().getItemInMainHand().getType()));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteractAtEntity(PlayerInteractEntityEvent event) {
        if (ItemUtil.mw78SoulBound(event.getPlayer().getEquipment().getItem(event.getHand()))) {
            if (event.getRightClicked() instanceof ItemFrame itemFrame) {
                if (itemFrame.getItem().isEmpty()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        if (ItemUtil.mw78SoulBound(event.getPlayerItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerClientOptionsChange(PlayerClientOptionsChangeEvent event) {
        if (event.hasLocaleChanged()) {
            event.getPlayer().updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof AbstractArrow arrow) {
            if (ItemUtil.mw78SoulBound(arrow.getItemStack())) {
                arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
            }
        }
    }

    @EventHandler
    public void onIncreaseFK(IncreaseStatsEvent.Kill event) {
        if (event.isFinal()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendPlayerListFooter(MegaWalls78.getInstance().getGameManager().footer().appendNewline().append(Component.text("MC.SUC.ICU", NamedTextColor.AQUA, TextDecoration.BOLD)));
            }
        }
    }

    @EventHandler
    public void onIncreaseFD(IncreaseStatsEvent.Death event) {
        if (event.isFinal()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendPlayerListFooter(MegaWalls78.getInstance().getGameManager().footer().appendNewline().append(Component.text("MC.SUC.ICU", NamedTextColor.AQUA, TextDecoration.BOLD)));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityPotionEffect(EntityPotionEffectEvent event) {
        if (event.getEntity() instanceof Player player) {
            GameManager gameManager = MegaWalls78.getInstance().getGameManager();
            if (gameManager.isSpectator(player)) {
                return;
            }
            GamePlayer gamePlayer = gameManager.getPlayer(player);
            if (gamePlayer == null) {
                return;
            }
            if (gameManager.isWitherDead(gamePlayer.getTeam())) {
                if (event.getModifiedType().equals(PotionEffectType.WITHER) && event.getCause().equals(EntityPotionEffectEvent.Cause.ATTACK)) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
