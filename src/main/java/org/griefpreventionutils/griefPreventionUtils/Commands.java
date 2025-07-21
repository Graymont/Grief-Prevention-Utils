package org.griefpreventionutils.griefPreventionUtils;

import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.claymoreutils.claymoreUtils.UserInterface.sendText;
import static org.griefpreventionutils.griefPreventionUtils.GPItem.GetClaimLicense;
import static org.griefpreventionutils.griefPreventionUtils.GPManager.*;

public class Commands implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (command.getName().equalsIgnoreCase("griefpreventionutils")) {

            if (args.length == 0) {
                sender.sendMessage(sendText("&cUsage: /griefpreventionutils managelimit <add|set|remove> <player> <amount>"));
                return true;
            }

            String mainSub = args[0].toLowerCase();

            if (mainSub.equals("managelimit")) {
                if (args.length < 4) {
                    sender.sendMessage(sendText("&cUsage: /griefpreventionutils managelimit <add|set|remove> <player> <amount>"));
                    return true;
                }

                String action = args[1].toLowerCase();
                String targetName = args[2];
                String amountStr = args[3];

                OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
                if ((!target.hasPlayedBefore() && !target.isOnline())) {
                    sender.sendMessage(sendText("&4Player not found!"));
                    return true;
                }

                int amount;
                try {
                    amount = Integer.parseInt(amountStr);
                    if (amount < 0) {
                        sender.sendMessage(sendText("&4Amount must be non-negative!"));
                        return true;
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(sendText("&4Amount must be a number!"));
                    return true;
                }

                PlayerData playerData = GetPlayerData(target);
                int current = playerData.getAccruedClaimBlocks();
                int maxAccruedLimit = GriefPrevention.instance.config_claims_maxAccruedBlocks_default;

                switch (action) {
                    case "add":
                        GPManager.ClaimLimitUtils.addLimit(playerData, amount);
                        int newAddTotal = playerData.getAccruedClaimBlocks();
                        sender.sendMessage(sendText("&aAdded &2" + amount + " &aclaim blocks to &2" + target.getName() +
                                "&a. New total: &2" + newAddTotal + "&a / &2" + maxAccruedLimit));
                        break;

                    case "set":
                        GPManager.ClaimLimitUtils.setLimit(playerData, amount);
                        int newSetTotal = playerData.getAccruedClaimBlocks();
                        sender.sendMessage(sendText("&aSet &2" + target.getName() + "&a's claim blocks to &2" + newSetTotal +
                                "&a / &2" + maxAccruedLimit));
                        break;

                    case "remove":
                        GPManager.ClaimLimitUtils.removeLimit(playerData, amount);
                        int newRemoveTotal = playerData.getAccruedClaimBlocks();
                        sender.sendMessage(sendText("&aRemoved &2" + amount + " &aclaim blocks from &2" + target.getName() +
                                "&a. New total: &2" + newRemoveTotal));
                        break;

                    default:
                        sender.sendMessage(sendText("&cUnknown action. Use add, set, or remove."));
                        break;
                }

                return true;
            }

            else if (mainSub.equals("showlimit")) {
                if (args.length < 2) {
                    sender.sendMessage(sendText("&cUsage: /griefpreventionutils showlimit <player>"));
                    return true;
                }

                String targetName = args[1];
                OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
                if ((!target.hasPlayedBefore() && !target.isOnline())) {
                    sender.sendMessage(sendText("&4Player not found!"));
                    return true;
                }

                PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(target.getUniqueId());
                int accrued = playerData.getAccruedClaimBlocks();
                int bonus = playerData.getBonusClaimBlocks();
                int remaining = playerData.getRemainingClaimBlocks();
                int maxAccruedLimit = GriefPrevention.instance.config_claims_maxAccruedBlocks_default;

                sender.sendMessage(sendText("&aClaim limit for &2" + target.getName() + "&a:"));
                sender.sendMessage(sendText("&aAccrued: &2" + accrued + "&a / &2" + maxAccruedLimit));
                sender.sendMessage(sendText("&aBonus: &2" + bonus));
                sender.sendMessage(sendText("&aRemaining: &2" + remaining));
                return true;
            }

            else if (mainSub.equals("test")) {
                if (sender instanceof Player) {
                    sender.sendMessage(sendText("&aTest successful!"));
                } else {
                    sender.sendMessage(sendText("&cYou must be a player to run this."));
                }
                return true;
            }

            else if (mainSub.equals("getitem")) {
                if (args[1].equalsIgnoreCase("claimlicense")){
                    int amount = Integer.parseInt(args[2]);

                    Player target;
                    if (args.length >= 4 && !args[3].isEmpty()) {
                        target = Bukkit.getPlayer(args[3]);
                        if (target == null) {
                            sender.sendMessage(sendText("&cPlayer not found!"));
                            return true;
                        }
                        } else {

                        target = (Player) sender;
                    }

                    target.getInventory().addItem(new ItemStack(GetClaimLicense(amount)));
                    target.sendMessage(sendText("&aObtained claim license with amount: &2"+amount));
                }
            }

            //sender.sendMessage(sendText("&cUnknown subcommand. Try /griefpreventionutils managelimit or showlimit"));
            return true;
        }

        if (command.getName().equalsIgnoreCase("claimtracker")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(sendText("&cYou must be a player to use this."));
                return true;
            }
            Tracker(player);
            return true;
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        if (command.getName().equalsIgnoreCase("griefpreventionutils")) {

            if (args.length == 1) {
                return Arrays.asList("managelimit", "showlimit", "getitem");
            }

            if (args.length == 2 && args[0].equalsIgnoreCase("managelimit")) {
                return Arrays.asList("add", "set", "remove");
            }

            if (args.length == 2 && args[0].equalsIgnoreCase("getitem")) {
                return Arrays.asList("claimlicense");
            }


            if ((args.length == 2 && args[0].equalsIgnoreCase("showlimit")) || (args.length == 3 && args[0].equalsIgnoreCase("managelimit"))) {
                List<String> playerNames = new ArrayList<>();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    playerNames.add(p.getName());
                }
                return playerNames;
            }

            if (args.length == 4 && args[0].equalsIgnoreCase("managelimit")) {
                return Collections.singletonList("<amount>");
            }
        }

        return Collections.emptyList();
    }
}
