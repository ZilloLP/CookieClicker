package de.zillolp.cookieclicker.interfaces;

import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.profile.PlayerProfile;

import java.util.UUID;

public interface NmsBridge {

    void sendSkullBlock(Player player, Location location, BlockData blockData, PlayerProfile playerProfile);

    void sendRealPlayerSkullBlock(Player player, Location location, BlockData blockData);

    void sendEntityRemovePacket(Player player, int[] entityIds);

    void sendTakeItemPacket(Player player, int itemEntityId, int collectorEntityId, int amount);

    void sendGlowPacket(Player viewer, UUID entityUuid);
}
