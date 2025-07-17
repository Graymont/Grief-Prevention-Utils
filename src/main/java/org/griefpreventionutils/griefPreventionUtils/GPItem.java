package org.griefpreventionutils.griefPreventionUtils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

import static org.claymoreutils.claymoreUtils.UserInterface.sendText;
import static org.griefpreventionutils.griefPreventionUtils.GPManager.GetPlayerData;
import static org.griefpreventionutils.griefPreventionUtils.PersistentDataManager.GetNamespacedKey;
import static org.griefpreventionutils.griefPreventionUtils.PersistentDataManager.hasNamespacedKey;

public class GPItem implements Listener {

    public static String claimLicenseKey = "claimLicense";
    public static String claimLicenseAmountKey = "claimLicenseAmount";

    public static ItemStack GetClaimLicense(int amount){

        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();

        PersistentDataContainer container = meta.getPersistentDataContainer();

        container.set(GetNamespacedKey(claimLicenseKey), PersistentDataType.BOOLEAN, true);
        container.set(GetNamespacedKey(claimLicenseAmountKey), PersistentDataType.INTEGER, amount);

        List<String> itemLore = new ArrayList<>();

        meta.setDisplayName(sendText("&fClaim License"));
        itemLore.add(sendText("&9Miscellaneous"));
        itemLore.add(sendText(" "));
        itemLore.add(sendText("&a+"+amount+" &7Claim Blocks"));
        itemLore.add(sendText(" "));
        itemLore.add(sendText("&fRight-Click &7to consume"));

        meta.setLore(itemLore);
        item.setItemMeta(meta);

        return item;
    }

    @EventHandler
    public void OnRightClick(PlayerInteractEvent event){
        if (event.getAction().isRightClick()){
            Player player = event.getPlayer();
            ItemStack item = player.getInventory().getItemInMainHand();

            if (item.getType() == Material.AIR){
                return;
            }

            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            if (hasNamespacedKey(item, claimLicenseKey)){

                Integer amount = container.get(GetNamespacedKey(claimLicenseAmountKey), PersistentDataType.INTEGER);
                if (amount == null){
                    amount = 0;
                }

                item.setAmount(item.getAmount()-1);

                GPManager.ClaimLimitUtils.addLimit(GetPlayerData(player), amount);

                player.sendMessage(sendText("&a+"+amount+" &7Claim Blocks"));
            }

        }
    }

}
