package icu.suc.megawalls78.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import icu.suc.megawalls78.MegaWalls78;
import icu.suc.megawalls78.game.GamePlayer;
import icu.suc.megawalls78.management.GameManager;
import icu.suc.megawalls78.util.LP;
import io.papermc.paper.adventure.PaperAdventure;
import io.papermc.paper.chat.ChatRenderer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.Optionull;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.LazyHashSet;
import org.bukkit.craftbukkit.util.LazyPlayerSet;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;

public class ShoutCommand extends Command {

    public static LiteralCommandNode<CommandSourceStack> register(String name, String permission) {
        return Commands.literal(name)
                .requires(source -> {
                    if (hasPermission(source, permission)) {
                        GameManager gameManager = MegaWalls78.getInstance().getGameManager();
                        return gameManager.inFighting() && source.getPlayer() instanceof ServerPlayer player && !gameManager.isSpectator(player.getUUID());
                    }
                    return false;
                })
                .then(Commands.argument("message", MessageArgument.message())
                        .executes(context -> {
                            CommandSourceStack source = context.getSource();
                            MessageArgument.resolveChatMessage(
                                    context, "message", message -> new ChatProcessor(source.getServer(), source.getPlayer(), message).process()
                            );
                            return 0;
                        })
                )
                .build();
    }

    @DefaultQualifier(NonNull.class)
    public static final class ChatProcessor implements ChatRenderer {

        static final ResourceKey<ChatType> PAPER_RAW;
        final MinecraftServer server;
        final ServerPlayer player;
        final PlayerChatMessage message;
        final net.kyori.adventure.text.Component paper$originalMessage;
        final OutgoingChatMessage outgoing;

        public ChatProcessor(final MinecraftServer server, final ServerPlayer player, final PlayerChatMessage message) {
            this.server = server;
            this.player = player;
            this.message = message;
            this.paper$originalMessage = PaperAdventure.asAdventure(this.message.decoratedContent());
            this.outgoing = OutgoingChatMessage.create(this.message);
        }

        @SuppressWarnings("deprecated")
        public void process() {
            this.processModern(
                    new LazyChatAudienceSet(this.server),
                    this.paper$originalMessage,
                    this.player.getBukkitEntity()
            );
        }

        private void processModern(LazyHashSet<Audience> viewers, net.kyori.adventure.text.Component message, Player player) {
            this.complete(viewers, message, player);
        }

        private void complete(LazyHashSet<Audience> viewers, net.kyori.adventure.text.Component message, Player player) {
            final CraftPlayer craftPlayer = ((CraftPlayer) player);
            final net.kyori.adventure.text.Component displayName = displayName(craftPlayer);

            final ChatType.Bound chatType = ChatType.bind(PAPER_RAW, this.player.level().registryAccess(), PaperAdventure.asVanilla(displayName));

            OutgoingChat outgoingChat = viewers.isLazy() ? new ServerOutgoingChat() : new ViewersOutgoingChat();
            outgoingChat.sendFormatChangedViewerAware(craftPlayer, displayName, message, viewers, chatType);
        }

        interface OutgoingChat {
            void sendFormatChangedViewerAware(CraftPlayer player, net.kyori.adventure.text.Component displayName, net.kyori.adventure.text.Component message, Set<Audience> viewers, ChatType.Bound chatType);
        }

        final class ServerOutgoingChat implements OutgoingChat {
            @Override
            public void sendFormatChangedViewerAware(CraftPlayer player, net.kyori.adventure.text.Component displayName, net.kyori.adventure.text.Component message, Set<Audience> viewers, ChatType.Bound chatType) {
                ChatProcessor.this.server.getPlayerList().broadcastChatMessage(ChatProcessor.this.message, ChatProcessor.this.player, chatType, viewer -> PaperAdventure.asVanilla(render(player, displayName, message, viewer)));
            }
        }

        final class ViewersOutgoingChat implements OutgoingChat {
            @Override
            public void sendFormatChangedViewerAware(CraftPlayer player, net.kyori.adventure.text.Component displayName, net.kyori.adventure.text.Component message, Set<Audience> viewers, ChatType.Bound chatType) {
                this.broadcastToViewers(viewers, chatType, v -> PaperAdventure.asVanilla(render(player, displayName, message, v)));
            }

            private void broadcastToViewers(Collection<Audience> viewers, final ChatType.Bound chatType, final @Nullable Function<Audience, Component> msgFunction) {
                for (Audience viewer : viewers) {
                    if (acceptsNative(viewer)) {
                        this.sendNative(viewer, chatType, msgFunction);
                    } else {
                        final @Nullable Component unsigned = Optionull.map(msgFunction, f -> f.apply(viewer));
                        final PlayerChatMessage msg = unsigned == null ? ChatProcessor.this.message : ChatProcessor.this.message.withUnsignedContent(unsigned);
                        viewer.sendMessage(msg.adventureView(), this.adventure(chatType));
                    }
                }
            }

            private static final Map<String, net.kyori.adventure.chat.ChatType> BUILT_IN_CHAT_TYPES = Util.make(() -> {
                final Map<String, net.kyori.adventure.chat.ChatType> map = new HashMap<>();
                for (final Field declaredField : net.kyori.adventure.chat.ChatType.class.getDeclaredFields()) {
                    if (Modifier.isStatic(declaredField.getModifiers()) && declaredField.getType().equals(ChatType.class)) {
                        try {
                            final net.kyori.adventure.chat.ChatType type = (net.kyori.adventure.chat.ChatType) declaredField.get(null);
                            map.put(type.key().asString(), type);
                        } catch (final ReflectiveOperationException ignore) {
                        }
                    }
                }
                return map;
            });

            private net.kyori.adventure.chat.ChatType.Bound adventure(ChatType.Bound chatType) {
                @Subst("key:value") final String stringKey = Objects.requireNonNull(
                        chatType.chatType().unwrapKey().orElseThrow().location(),
                        () -> "No key for '%s' in CHAT_TYPE registry.".formatted(chatType)
                ).toString();
                net.kyori.adventure.chat.@Nullable ChatType adventure = BUILT_IN_CHAT_TYPES.get(stringKey);
                if (adventure == null) {
                    adventure = net.kyori.adventure.chat.ChatType.chatType(Key.key(stringKey));
                }
                return adventure.bind(
                        PaperAdventure.asAdventure(chatType.name()),
                        chatType.targetName().map(PaperAdventure::asAdventure).orElse(null)
                );
            }

            private static boolean acceptsNative(final Audience viewer) {
                if (viewer instanceof Player || viewer instanceof ConsoleCommandSender) {
                    return true;
                }
                if (viewer instanceof ForwardingAudience.Single single) {
                    return acceptsNative(single.audience());
                }
                return false;
            }

            private void sendNative(final Audience viewer, final ChatType.Bound chatType, final @Nullable Function<Audience, Component> msgFunction) {
                switch (viewer) {
                    case ConsoleCommandSender ignored -> this.sendToServer(chatType, msgFunction);
                    case CraftPlayer craftPlayer ->
                            craftPlayer.getHandle().sendChatMessage(ChatProcessor.this.outgoing, ChatProcessor.this.player.shouldFilterMessageTo(craftPlayer.getHandle()), chatType, Optionull.map(msgFunction, f -> f.apply(viewer)));
                    case ForwardingAudience.Single single -> this.sendNative(single.audience(), chatType, msgFunction);
                    default ->
                            throw new IllegalStateException("Should only be a Player or Console or ForwardingAudience.Single pointing to one!");
                }
            }

            private void sendToServer(final ChatType.Bound chatType, final @Nullable Function<Audience, Component> msgFunction) {
                final PlayerChatMessage toConsoleMessage = msgFunction == null ? ChatProcessor.this.message : ChatProcessor.this.message.withUnsignedContent(msgFunction.apply(ChatProcessor.this.server.console));
                ChatProcessor.this.server.logChatMessage(toConsoleMessage.decoratedContent(), chatType, ChatProcessor.this.server.getPlayerList().verifyChatTrusted(toConsoleMessage) ? null : "Not Secure");
            }
        }

        static net.kyori.adventure.text.Component displayName(final CraftPlayer player) {
            if (((CraftWorld) player.getWorld()).getHandle().paperConfig().scoreboards.useVanillaWorldScoreboardNameColoring) {
                return player.teamDisplayName();
            }
            return player.displayName();
        }

        @Override
        public @NotNull net.kyori.adventure.text.Component render(@NotNull Player player, @NotNull net.kyori.adventure.text.Component component, @NotNull net.kyori.adventure.text.Component component1, @NotNull Audience audience) {
            UUID uuid = player.getUniqueId();
            GamePlayer gamePlayer = MegaWalls78.getInstance().getGameManager().getPlayer(uuid);
            return net.kyori.adventure.text.Component.translatable("mw78.brackets", NamedTextColor.GOLD, net.kyori.adventure.text.Component.translatable("mw78.shout")).appendSpace().append(net.kyori.adventure.text.Component.translatable("mw78.chat", NamedTextColor.GRAY, net.kyori.adventure.text.Component.translatable("mw78.brackets", gamePlayer.getTeam().color(), gamePlayer.getTeam().chat()).appendSpace().append(LP.getPrefix(uuid)).append(player.name().color(LP.getNameColor(uuid))).hoverEvent(player).clickEvent(player.teamDisplayName().clickEvent()), component1.color(NamedTextColor.WHITE)));
        }

        static {
            ResourceKey<ChatType> key;
            try {
                key = (ResourceKey<ChatType>) FieldUtils.readStaticField(io.papermc.paper.adventure.ChatProcessor.class, "PAPER_RAW", true);
            } catch (IllegalAccessException e) {
                key = ResourceKey.create(Registries.CHAT_TYPE, ResourceLocation.fromNamespaceAndPath(ResourceLocation.PAPER_NAMESPACE, "raw"));
            }
            PAPER_RAW = key;
        }

        static final class LazyChatAudienceSet extends LazyHashSet<Audience> {
            private final MinecraftServer server;

            public LazyChatAudienceSet(final MinecraftServer server) {
                this.server = server;
            }

            @Override
            protected Set<Audience> makeReference() {
                final Set<Player> playerSet = LazyPlayerSet.makePlayerSet(this.server);
                final HashSet<Audience> audiences = new HashSet<>(playerSet);
                audiences.add(Bukkit.getConsoleSender());
                return audiences;
            }
        }
    }
}
