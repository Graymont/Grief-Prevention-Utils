package org.griefpreventionutils.griefPreventionUtils;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import static org.griefpreventionutils.griefPreventionUtils.GriefPreventionUtils.getMainPlugin;

public class PersistentDataManager {

    public static NamespacedKey GetNamespacedKey(String k){
        return new NamespacedKey(getMainPlugin(), k);
    }

    public static boolean hasNamespacedKey(ItemStack item, String k){
        if (item == null){
            return false;
        }

        if (item.getType() == Material.AIR){
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        return container.has(GetNamespacedKey(k));
    }

}
