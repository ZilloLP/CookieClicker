package de.zillolp.cookieclicker.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import de.zillolp.cookieclicker.CookieClicker;
import de.zillolp.cookieclicker.interfaces.NmsBridge;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.TeamColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.profile.PlayerProfile;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NmsBridge_v26_R2 implements NmsBridge {
    private final ReflectionUtil reflectionUtil;
    private final Logger logger;
    private final String teamName = "CookieExplosion";

    public NmsBridge_v26_R2(CookieClicker plugin) {
        reflectionUtil = plugin.getReflectionUtil();
        logger = plugin.getLogger();
    }

    @Override
    public void sendSkullBlock(Player player, Location location, BlockData blockData, PlayerProfile playerProfile) {
        GameProfile gameProfile = reflectionUtil.convertPlayerProfileToGameProfile(playerProfile);
        sendSkullBlockInternal(player, location, blockData, gameProfile, playerProfile);
    }

    @Override
    public void sendRealPlayerSkullBlock(Player player, Location location, BlockData blockData) {
        boolean isSpigot = reflectionUtil.isSpigot();
        GameProfile gameProfile;
        try {
            Object craftPlayer = reflectionUtil.getCraftObjectClass(isSpigot, "entity.CraftPlayer").cast(player);
            gameProfile = (GameProfile) craftPlayer.getClass().getMethod("getProfile").invoke(craftPlayer);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            logger.log(Level.SEVERE, "Error extracting GameProfile from CraftPlayer", exception);
            return;
        }
        sendSkullBlockInternal(player, location, blockData, gameProfile, null);
    }

    private void sendSkullBlockInternal(Player player, Location location, BlockData blockData, GameProfile gameProfile, PlayerProfile playerProfile) {
        World world = location.getWorld();
        if (world == null) {
            return;
        }
        boolean isSpigot = reflectionUtil.isSpigot();

        BlockPos blockPos = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        SkullBlockEntity skullBlockEntity = new SkullBlockEntity(blockPos, (BlockState) reflectionUtil.getServerObject(blockData.createBlockState(), reflectionUtil.getCraftObjectClass(isSpigot, "block.CraftBlockState")));
        skullBlockEntity.setLevel((net.minecraft.world.level.Level) reflectionUtil.getServerObject(world, reflectionUtil.getCraftObjectClass(isSpigot, "CraftWorld")));

        reflectionUtil.setValue(skullBlockEntity, "owner", getResolvedProfile(gameProfile));
        reflectionUtil.sendPacket(new ClientboundBlockUpdatePacket(blockPos, (BlockState) reflectionUtil.invokeMethod(skullBlockEntity, "getBlockState")), player);
        try {
            CompoundTag profileTag = createProfileTag(gameProfile, playerProfile);
            CompoundTag nbtTag = new CompoundTag();
            nbtTag.put("profile", profileTag);
            reflectionUtil.sendPacket(new ClientboundBlockEntityDataPacket(blockPos, BlockEntityTypes.SKULL, nbtTag), player);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            logger.log(Level.SEVERE, "Error creating manual NBT packet", exception);
        }
    }

    private Object getResolvedProfile(GameProfile gameProfile) {
        try {
            return ResolvableProfile.class.getMethod("createResolved", GameProfile.class).invoke(null, gameProfile);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException exception) {
            logger.log(Level.SEVERE, "Error creating resolved profile", exception);
            return null;
        }
    }

    private int[] convertUuidToIntArray(UUID uuid) {
        return new int[]{
                (int) (uuid.getMostSignificantBits() >> 32),
                (int) uuid.getMostSignificantBits(),
                (int) (uuid.getLeastSignificantBits() >> 32),
                (int) uuid.getLeastSignificantBits()
        };
    }

    private CompoundTag createProfileTag(GameProfile gameProfile, PlayerProfile playerProfile) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        CompoundTag profileTag = new CompoundTag();
        UUID uuid = (UUID) gameProfile.getClass().getMethod(reflectionUtil.getIdMethodName()).invoke(gameProfile);
        profileTag.putIntArray("id", convertUuidToIntArray(uuid));
        profileTag.putString("name", (String) gameProfile.getClass().getMethod(reflectionUtil.getNameMethodName()).invoke(gameProfile));

        if (playerProfile != null) {
            URL skinURL = playerProfile.getTextures().getSkin();
            if (skinURL != null) {
                addCustomTextureProperty(profileTag, skinURL);
                return profileTag;
            }
        }

        addGameProfileProperties(profileTag, gameProfile);
        return profileTag;
    }

    private void addCustomTextureProperty(CompoundTag profileTag, URL skinURL) {
        ListTag propertiesList = new ListTag();
        CompoundTag propertyTag = new CompoundTag();
        propertyTag.putString("name", "textures");
        propertyTag.putString("value", Base64.getEncoder().encodeToString(
                String.format("{\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}", skinURL)
                        .getBytes(StandardCharsets.UTF_8)
        ));
        propertiesList.add(propertyTag);
        profileTag.put("properties", propertiesList);
    }

    private void addGameProfileProperties(CompoundTag profileTag, GameProfile gameProfile) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        PropertyMap propertyMap = (PropertyMap) gameProfile.getClass().getMethod(reflectionUtil.getPropertiesMethodName()).invoke(gameProfile);
        if (!propertyMap.isEmpty()) {
            ListTag propertiesList = new ListTag();
            for (Property property : propertyMap.get("textures")) {
                CompoundTag propertyTag = new CompoundTag();
                propertyTag.putString("name", property.name());
                propertyTag.putString("value", property.value());
                if (property.signature() != null) {
                    propertyTag.putString("signature", property.signature());
                }
                propertiesList.add(propertyTag);
            }
            profileTag.put("properties", propertiesList);
        }
    }

    @Override
    public void sendEntityRemovePacket(Player player, int[] entityIds) {
        reflectionUtil.sendPacket(new ClientboundRemoveEntitiesPacket(entityIds), player);
    }

    @Override
    public void sendTakeItemPacket(Player player, int itemEntityId, int collectorEntityId, int amount) {
        reflectionUtil.sendPacket(new ClientboundTakeItemEntityPacket(itemEntityId, collectorEntityId, amount), player);
    }

    @Override
    public void sendGlowPacket(Player viewer, UUID entityUuid) {
        PlayerTeam playerTeam = new PlayerTeam(new Scoreboard(), teamName);
        playerTeam.setColor(Optional.of(TeamColor.GOLD));
        reflectionUtil.sendPacket(ClientboundSetPlayerTeamPacket.createPlayerPacket(playerTeam, teamName, ClientboundSetPlayerTeamPacket.Action.ADD), viewer);
        @SuppressWarnings("unchecked")
        Collection<String> players = (Collection<String>) reflectionUtil.invokeMethod(playerTeam, "getPlayers");
        String uuidString = entityUuid.toString();
        players.remove(uuidString);
        players.add(uuidString);
        reflectionUtil.sendPacket(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(playerTeam, true), viewer);
    }
}
