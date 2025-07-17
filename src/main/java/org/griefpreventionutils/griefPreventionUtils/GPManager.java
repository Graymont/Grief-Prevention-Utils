package org.griefpreventionutils.griefPreventionUtils;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

import static org.claymoreutils.claymoreUtils.UserInterface.*;
import static org.griefpreventionutils.griefPreventionUtils.GriefPreventionUtils.getMainPlugin;


public class GPManager implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        AdjustLimits(event.getPlayer());

        tracker.putIfAbsent(player.getUniqueId(), false);
    }

    public static void AdjustLimits(OfflinePlayer player) {
        PlayerData data = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        int accrued = data.getAccruedClaimBlocks();
        int maxAccruedLimit = GriefPrevention.instance.config_claims_maxAccruedBlocks_default;

        if (accrued > maxAccruedLimit) {
            data.setAccruedClaimBlocks(maxAccruedLimit);
            if (player.isOnline()) {
                Player p = player.getPlayer();
                assert p != null;
                consoleLog(sendText("&eAccrued claim blocks of "+p.getName()+" were reached the limit and have been adjusted to &6" + maxAccruedLimit));
            }
        }
    }



    public static void TrackClaim(Player player){
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), false, null);
        if (claim != null) {
            player.sendMessage(sendText("&aYou're in a claim of: &2"+claim.getOwnerName()));
        }else{
            player.sendMessage(sendText("&cThere's no claim here!"));
        }
    }

    public static boolean isPlayerInClaim(Player player){
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(player.getLocation(), false, null);
        return claim != null;
    }

    public static HashMap<UUID, Boolean> tracker = new HashMap<>();

    public static void Tracker(Player player){

        tracker.putIfAbsent(player.getUniqueId(), false);

        if (tracker.get(player.getUniqueId())){
            tracker.put(player.getUniqueId(), false);
            player.sendMessage(sendText("&cTurned off claim tracker!"));
            PlaySoundAt(Sound.BLOCK_NOTE_BLOCK_GUITAR, player.getLocation(), 1, 1);
        }else{
            tracker.put(player.getUniqueId(), true);
            player.sendMessage(sendText("&aTurned on claim tracker!"));
            PlaySoundAt(Sound.BLOCK_NOTE_BLOCK_GUITAR, player.getLocation(), 1, 0);
        }
    }

    public static PlayerData GetPlayerData(OfflinePlayer player){
        return GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
    }

    public class ClaimLimitUtils {

        public static void addLimit(PlayerData playerData, int amount) {
            int current = playerData.getAccruedClaimBlocks();
            int maxAccruedLimit = GriefPrevention.instance.config_claims_maxAccruedBlocks_default;
            int addedTotal = Math.min(current + amount, maxAccruedLimit);
            playerData.setAccruedClaimBlocks(addedTotal);
        }

        public static void setLimit(PlayerData playerData, int amount) {
            int maxAccruedLimit = GriefPrevention.instance.config_claims_maxAccruedBlocks_default;
            int setTotal = Math.min(amount, maxAccruedLimit);
            playerData.setAccruedClaimBlocks(setTotal);
        }

        public static void removeLimit(PlayerData playerData, int amount) {
            int maxAccruedLimit = GriefPrevention.instance.config_claims_maxAccruedBlocks_default;
            int current = playerData.getAccruedClaimBlocks();
            int newTotal = Math.max(0, current - amount);
            playerData.setAccruedClaimBlocks(newTotal);
        }
    }


    public static void TrackerTrigger(){
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (tracker.get(player.getUniqueId())){
                        if (isPlayerInClaim(player)){
                            player.sendTitle(sendText("&bTracking Claim..."), sendText("&a[You're in a claim land]"));
                        }else{
                            player.sendTitle(sendText("&fTracking Claim..."), sendText("&8[You're not in a claim land]"));
                        }
                    }
                }
            }
        }.runTaskTimer(getMainPlugin(), 0L, 20L);
    }


}
