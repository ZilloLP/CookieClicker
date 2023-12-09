package de.zillolp.cookieclicker.listener;

import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.events.ClickerInteractEvent;
import de.zillolp.cookieclicker.utils.ReflectionUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.phys.BlockHitResult;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class PacketReader {
    private final CookieClicker plugin;

    private Channel channel;

    public PacketReader(CookieClicker plugin) {
        this.plugin = plugin;
    }

    public void inject(Player player) {
        UUID uuid = player.getUniqueId();
        String readerName = "Reader-" + uuid;
        channel = ((Connection) ReflectionUtil.getSuperValue(((CraftPlayer) player).getHandle().connection, "c")).channel;
        if (channel.pipeline() == null || channel.pipeline().get(readerName) != null) {
            return;
        }
        channel.pipeline().addAfter("decoder", readerName, new MessageToMessageDecoder() {
            @Override
            protected void decode(ChannelHandlerContext channelHandlerContext, Object packet, List list) throws Exception {
                readPacket(player, packet, list);
            }
        });
    }

    public void unInject(Player player) {
        UUID uuid = player.getUniqueId();
        String readerName = "Reader-" + uuid;
        if (channel.pipeline() == null || channel.pipeline().get(readerName) == null) {
            return;
        }
        channel.pipeline().remove(readerName);
    }

    private void readPacket(Player player, Object packet, List list) {
        switch (packet.getClass().getSimpleName()) {
            case "PacketPlayInBlockDig":
                if (!(ReflectionUtil.getValue(packet, "c").toString().equalsIgnoreCase("START_DESTROY_BLOCK"))) {
                    break;
                }
                BlockPos blockPos = (BlockPos) ReflectionUtil.getValue(packet, "a");
                if (!(isClicker(player, blockPos))) {
                    break;
                }
                ReflectionUtil.setValue(packet, "c", ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK);
                execute(player, blockPos, ClickerInteractEvent.InteractType.ATTACK);
                break;
            case "PacketPlayInUseItem":
                BlockHitResult blockHitResult = (BlockHitResult) ReflectionUtil.getValue(packet, "a");
                if (blockHitResult == null) {
                    break;
                }
                BlockPos blockPos1 = blockHitResult.getBlockPos();
                if (!(isClicker(player, blockPos1))) {
                    break;
                }
                execute(player, blockPos1, ClickerInteractEvent.InteractType.INTERACT);
                return;
            case "PacketPlayInArmAnimation":
                Block clickedBlock = player.getTargetBlock(null, 5);
                if (player.getGameMode() != GameMode.ADVENTURE || clickedBlock.getType() == Material.AIR || (!(ReflectionUtil.getValue(packet, "a").toString().equalsIgnoreCase("MAIN_HAND")))) {
                    break;
                }
                Location location = clickedBlock.getLocation();
                BlockPos blockPos2 = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
                if (!(isClicker(player, blockPos2))) {
                    break;
                }
                execute(player, blockPos2, ClickerInteractEvent.InteractType.ADVENTURE_ATTACK);
                break;
        }
        list.add(packet);
    }

    private boolean isClicker(Player player, BlockPos blockPos) {
        if (blockPos == null) {
            return false;
        }
        return plugin.getCookieClickerManager().getClickerLocations().contains(new Location(player.getWorld(), blockPos.getX(), blockPos.getY(), blockPos.getZ()));
    }

    private void execute(Player player, BlockPos blockPos, ClickerInteractEvent.InteractType interactType) {
        Bukkit.getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                Bukkit.getServer().getPluginManager().callEvent(new ClickerInteractEvent(player, blockPos, interactType));
            }
        });
    }
}
