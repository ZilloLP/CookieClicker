package de.zillolp.cookieclicker.listener;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.customevents.CookieClickerInteractEvent;
import de.zillolp.cookieclicker.handler.ClickerHandler;
import de.zillolp.cookieclicker.interfaces.PacketReader;
import de.zillolp.cookieclicker.manager.ClickerPlayerManager;
import de.zillolp.cookieclicker.manager.CookieClickerManager;
import de.zillolp.cookieclicker.manager.VersionManager;
import de.zillolp.cookieclicker.profiles.ClickerGameProfile;
import de.zillolp.cookieclicker.utils.ReflectionUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PacketReader_v26_R2 implements PacketReader {
    private final String READER_PREFIX = "Reader-";
    private final CookieClicker plugin;
    private final ReflectionUtil reflectionUtil;
    private final ClickerHandler clickerHandler;
    private final CookieClickerManager cookieClickerManager;
    private final ClickerPlayerManager clickerPlayerManager;
    private final ArrayList<Location> clickerLocations;
    private final HashMap<UUID, Channel> readerChannels = new HashMap<>();
    private String fieldName;
    private String channelFieldName;
    private String interactPacketName;
    private String attackPacketName;
    private String adventureAttackPacketName;
    private String blockHitName;
    private String blockHitResultName;
    private String interactHandName;
    private String blockPosName;
    private String actionName;
    private String handName;

    public PacketReader_v26_R2(CookieClicker plugin) {
        this.plugin = plugin;
        reflectionUtil = plugin.getReflectionUtil();
        clickerHandler = plugin.getClickerHandler();
        VersionManager versionManager = plugin.getVersionManager();
        cookieClickerManager = plugin.getCookieClickerManager();
        clickerPlayerManager = plugin.getClickerPlayerManager();
        clickerLocations = cookieClickerManager.getClickerLocations();
        boolean isSpigot = reflectionUtil.isSpigot();
        fieldName = "connection";
        channelFieldName = "channel";
        interactPacketName = "ServerboundUseItemOnPacket";
        attackPacketName = "ServerboundPlayerActionPacket";
        adventureAttackPacketName = "ServerboundSwingPacket";
        blockHitName = "blockHit";
        blockHitResultName = "blockPos";
        interactHandName = "hand";
        blockPosName = "pos";
        actionName = "action";
        handName = "hand";
        /*if (isSpigot) {
            fieldName = "e";
            channelFieldName = "n";
            if (versionManager.getVersionNumber() == 21 && versionManager.getSubVersion() == 11) {
                channelFieldName = "k";
            }
            interactPacketName = "PacketPlayInUseItem";
            attackPacketName = "PacketPlayInBlockDig";
            adventureAttackPacketName = "PacketPlayInArmAnimation";
            blockHitName = "b";
            blockHitResultName = "c";
            interactHandName = "c";
            blockPosName = "b";
            actionName = "d";
            handName = "b";
        }*/
    }

    public void inject(Player player) {
        UUID uuid = player.getUniqueId();
        String readerName = READER_PREFIX + uuid;
        Connection connection = (Connection) reflectionUtil.getValue(reflectionUtil.getPlayerConnection(player), fieldName);
        Channel channel = (Channel) reflectionUtil.getValue(connection, channelFieldName);

        ChannelPipeline channelPipeline = channel.pipeline();
        if (channel.pipeline() == null) {
            return;
        }
        if (channelPipeline.names().contains(readerName)) {
            channelPipeline.remove(readerName);
        }
        if (channelPipeline.get("decoder") == null) {
            return;
        }
        channelPipeline.addAfter("decoder", readerName, new MessageToMessageDecoder<Object>() {
            @Override
            protected void decode(ChannelHandlerContext channelHandlerContext, Object packet, List<Object> out) {
                readPacket(player, packet, out);
            }
        });
        readerChannels.put(uuid, channel);
    }

    public void unInject(Player player) {
        UUID uuid = player.getUniqueId();
        if (!(readerChannels.containsKey(uuid))) {
            return;
        }
        Channel channel = readerChannels.get(uuid);
        if (channel != null) {
            String readerName = READER_PREFIX + uuid;
            ChannelPipeline channelPipeline = channel.pipeline();
            if (channelPipeline.names().contains(readerName)) {
                channelPipeline.remove(readerName);
            }
        }
        readerChannels.remove(uuid);
    }

    private void readPacket(Player player, Object packet, List<Object> list) {
        String packetName = packet.getClass().getSimpleName();
        if (packetName.equalsIgnoreCase(interactPacketName)) {
            BlockHitResult blockHitResult = (BlockHitResult) reflectionUtil.getValue(packet, blockHitName);
            if (blockHitResult == null) {
                list.add(packet);
                return;
            }

            BlockPos blockPos = (BlockPos) reflectionUtil.getValue(blockHitResult, blockHitResultName);
            if (blockPos == null) {
                list.add(packet);
                return;
            }
            Location location = new Location(player.getWorld(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
            if (clickerLocations.stream().noneMatch(location1 -> reflectionUtil.isSameLocation(location1, location))) {
                list.add(packet);
                return;
            }
            PlayerInventory playerInventory = player.getInventory();
            ItemStack itemStack = playerInventory.getItemInMainHand();
            CookieClickerInteractEvent.InteractType interactType = CookieClickerInteractEvent.InteractType.HAND_RIGHT_CLICK;
            if (reflectionUtil.getValue(packet, interactHandName) == InteractionHand.OFF_HAND) {
                interactType = CookieClickerInteractEvent.InteractType.OFF_HAND_RIGHT_CLICK;
                itemStack = playerInventory.getItemInOffHand();
            }
            if (itemStack.getType() != Material.AIR && itemStack.getType().isBlock()) {
                list.add(packet);
                return;
            }
            execute(player, location, interactType).thenAccept(cookieClickerInteractEvent -> {
                if (!(cookieClickerInteractEvent.isCancelled())) {
                    return;
                }
                list.add(packet);
            });
            return;
        }
        if (packetName.equalsIgnoreCase(attackPacketName)) {
            BlockPos blockPos = (BlockPos) reflectionUtil.getValue(packet, blockPosName);
            if (blockPos == null) {
                list.add(packet);
                return;
            }
            if (!(reflectionUtil.getValue(packet, actionName).toString().equalsIgnoreCase("START_DESTROY_BLOCK"))) {
                list.add(packet);
                return;
            }
            Location location = new Location(player.getWorld(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
            if (clickerLocations.stream().noneMatch(location1 -> reflectionUtil.isSameLocation(location1, location))) {
                list.add(packet);
                return;
            }
            execute(player, location, CookieClickerInteractEvent.InteractType.LEFT_CLICK).thenAccept(cookieClickerInteractEvent -> {
                if (cookieClickerInteractEvent.isCancelled()) {
                    list.add(packet);
                    return;
                }
                reflectionUtil.setValue(packet, actionName, ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK);
                list.add(packet);
            });
            return;
        }
        list.add(packet);
        if (packetName.equalsIgnoreCase(adventureAttackPacketName)) {
            Block clickedBlock = player.getTargetBlock(null, 5);
            if (player.getGameMode() != GameMode.ADVENTURE || clickedBlock.getType() == Material.AIR || (!(reflectionUtil.getValue(packet, handName).toString().equalsIgnoreCase("MAIN_HAND")))) {
                return;
            }
            Location location = clickedBlock.getLocation();
            if (clickerLocations.stream().noneMatch(location1 -> reflectionUtil.isSameLocation(location1, location))) {
                return;
            }
            ClickerGameProfile clickerGameProfile = clickerPlayerManager.getGameProfile(player.getUniqueId());
            if (clickerGameProfile.isOverLastClickerInteraction(cookieClickerManager.adventureClickDelay)) {
                return;
            }
            clickerGameProfile.updateLastClickerInteraction();
            execute(player, location, CookieClickerInteractEvent.InteractType.ADVENTURE_LEFT_CLICK);
        }
    }

    private CompletableFuture<CookieClickerInteractEvent> execute(Player player, Location location, CookieClickerInteractEvent.InteractType interactType) {
        CompletableFuture<CookieClickerInteractEvent> completableFuture = new CompletableFuture<>();
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            CookieClickerInteractEvent cookieClickerInteractEvent = new CookieClickerInteractEvent(player, location, interactType, false);
            plugin.getServer().getPluginManager().callEvent(cookieClickerInteractEvent);

            if (!(cookieClickerInteractEvent.isCancelled())) {
                clickerHandler.handleClickerInteract(cookieClickerInteractEvent);
            }
            completableFuture.complete(cookieClickerInteractEvent);
        });
        return completableFuture;
    }
}
