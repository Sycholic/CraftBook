package com.sk89q.craftbook.bukkit.commands;
import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;

import com.sk89q.craftbook.bukkit.CraftBookPlugin;
import com.sk89q.craftbook.bukkit.ReportWriter;
import com.sk89q.craftbook.bukkit.Updater;
import com.sk89q.craftbook.bukkit.Updater.UpdateResult;
import com.sk89q.craftbook.bukkit.util.BukkitUtil;
import com.sk89q.craftbook.mechanics.area.AreaCommands;
import com.sk89q.craftbook.mechanics.cauldron.CauldronCommands;
import com.sk89q.craftbook.mechanics.crafting.RecipeCommands;
import com.sk89q.craftbook.mechanics.ic.ICCommands;
import com.sk89q.craftbook.mechanics.items.CommandItemCommands;
import com.sk89q.craftbook.mechanics.signcopier.SignEditCommands;
import com.sk89q.craftbook.mechanics.variables.VariableCommands;
import com.sk89q.craftbook.util.PastebinPoster;
import com.sk89q.craftbook.util.PastebinPoster.PasteCallback;
import com.sk89q.craftbook.util.developer.ExternalUtilityManager;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.sk89q.minecraft.util.commands.NestedCommand;

/**
 * Author: Turtle9598
 */
public class TopLevelCommands {

    public TopLevelCommands(CraftBookPlugin plugin) {

    }

    @Command(aliases = {"craftbook", "cb"}, desc = "CraftBook Plugin commands")
    @NestedCommand(Commands.class)
    public void craftBookCmds(CommandContext context, CommandSender sender) {

    }

    @Command(aliases = {"area", "togglearea"}, desc = "Commands to manage Craftbook Areas")
    @NestedCommand(AreaCommands.class)
    public void area(CommandContext context, CommandSender sender) {
    }

    @Command(aliases = {"recp", "recps"}, desc = "Commands to manage Craftbook Custom Recipes")
    @NestedCommand(RecipeCommands.class)
    public void recipe(CommandContext context, CommandSender sender) {

    }

    @Command(aliases = {"comitems", "commanditems", "citems", "commanditem"}, desc = "Commands to manage Craftbook Command Items")
    @NestedCommand(CommandItemCommands.class)
    public void commandItems(CommandContext context, CommandSender sender) {
    }

    @Command(aliases = {"cauldron"}, desc = "Commands to manage the Craftbook Cauldron")
    @NestedCommand(CauldronCommands.class)
    public void cauldron(CommandContext context, CommandSender sender) {

    }

    @Command(aliases = {"sign", "signcopy", "signpaste", "signedit"}, desc = "Commands to manage the Sign Copier")
    @NestedCommand(SignEditCommands.class)
    public void signedit(CommandContext context, CommandSender sender) {
    }

    @Command(aliases = {"ic","circuit"}, desc = "Commands to manage Craftbook IC's")
    @NestedCommand(ICCommands.class)
    public void icCmd(CommandContext context, CommandSender sender) {
    }

    public static class Commands {

        public Commands(CraftBookPlugin plugin) {

        }

        @Command(aliases = {"var"}, desc = "Variable commands")
        @NestedCommand(VariableCommands.class)
        public void variableCmds(CommandContext context, CommandSender sender) {

        }

        @Command(aliases = "reload", desc = "Reloads the CraftBook Common config")
        @CommandPermissions("craftbook.reload")
        public void reload(CommandContext context, CommandSender sender) {

            try {
                CraftBookPlugin.inst().reloadConfiguration();
            } catch (Throwable e) {
                BukkitUtil.printStacktrace(e);
                sender.sendMessage("An error occured while reloading the CraftBook config.");
                return;
            }
            sender.sendMessage("The CraftBook config has been reloaded.");
        }

        @Command(aliases = "update", desc = "Updates CraftBook.", flags = "c", max = 0)
        @CommandPermissions("craftbook.update")
        public void update(CommandContext context, CommandSender sender) {

            if(!CraftBookPlugin.inst().getConfiguration().updateNotifier) {
                sender.sendMessage("Functionality disabled!");
                return;
            }

            if (context.hasFlag('c')) {

                CraftBookPlugin.inst().checkForUpdates();
                sender.sendMessage(CraftBookPlugin.inst().getLatestVersion() + " is the latest version available, and the updatability of it is: " + CraftBookPlugin.inst().isUpdateAvailable());
            } else {

                if(!CraftBookPlugin.inst().updateAvailable) {
                    sender.sendMessage("No updates are available!");
                    return;
                }
                Updater updater = new Updater(CraftBookPlugin.inst(), CraftBookPlugin.getUpdaterID(), CraftBookPlugin.inst().getFile(), Updater.UpdateType.DEFAULT, true);
                if(updater.getResult() == UpdateResult.NO_UPDATE)
                    sender.sendMessage("No updates are available!");
                else {
                    sender.sendMessage("Update found! Check console for download progress.");
                }
            }
        }

        @Command(aliases = "about", desc = "Gives info about craftbook.")
        public void about(CommandContext context, CommandSender sender) {

            String ver = CraftBookPlugin.inst().getDescription().getVersion();
            if(ver.split("-")[0].equalsIgnoreCase(CraftBookPlugin.getStableBuild()))
                ver = CraftBookPlugin.getVersion();
            sender.sendMessage(ChatColor.YELLOW + "CraftBook version " + ver);
            sender.sendMessage(ChatColor.YELLOW + "Founded by sk89q, and currently developed by me4502 & Dark_Arc");
        }

        @Command(aliases = {"report"}, desc = "Writes a report on CraftBook", flags = "pi", max = 0)
        @CommandPermissions({"craftbook.report"})
        public void report(CommandContext args, final CommandSender sender) throws CommandPermissionsException {

            File dest = new File(CraftBookPlugin.inst().getDataFolder(), "report.txt");
            ReportWriter report = new ReportWriter(CraftBookPlugin.inst());

            if(args.hasFlag('i'))
                report.appendFlags("i");

            report.generate();

            try {
                report.write(dest);
                sender.sendMessage(ChatColor.YELLOW + "CraftBook report written to "
                        + dest.getAbsolutePath());
            } catch (IOException e) {
                throw new CommandException("Failed to write report: " + e.getMessage());
            }

            if (args.hasFlag('p')) {
                CraftBookPlugin.inst().checkPermission(sender, "craftbook.report.pastebin");

                sender.sendMessage(ChatColor.YELLOW + "Now uploading to Pastebin...");
                PastebinPoster.paste(report.toString(), new PasteCallback() {

                    @Override
                    public void handleSuccess(String url) {
                        // Hope we don't have a thread safety issue here
                        sender.sendMessage(ChatColor.YELLOW + "CraftBook report (1 hour): " + url);
                    }

                    @Override
                    public void handleError(String err) {
                        // Hope we don't have a thread safety issue here
                        sender.sendMessage(ChatColor.YELLOW + "CraftBook report pastebin error: " + err);
                    }
                });
            }
        }

        @Command(aliases = {"dev"}, desc = "Advanced developer commands")
        @CommandPermissions({"craftbook.developer"})
        public void dev(CommandContext args, final CommandSender sender) throws CommandPermissionsException {

            if(args.argsLength() > 1 && args.getString(0).equalsIgnoreCase("util")) {
                try {
                    ExternalUtilityManager.performExternalUtility(args.getString(1), args.getSlice(1));
                    sender.sendMessage(ChatColor.YELLOW + "Performed utility successfully!");
                } catch (Exception e) {
                    e.printStackTrace();
                    sender.sendMessage(ChatColor.RED + "Failed to perform utility: See console for details!");
                }
            }
        }

        @Command(aliases = {"enable"}, desc = "Enable a mechanic")
        @CommandPermissions({"craftbook.enable-mechanic"})
        public void enable(CommandContext args, final CommandSender sender) throws CommandPermissionsException {

            if(args.argsLength() > 0) {
                if(CraftBookPlugin.inst().enableMechanic(args.getString(0)))
                    sender.sendMessage(ChatColor.YELLOW + "Sucessfully enabled " + args.getString(0));
                else
                    sender.sendMessage(ChatColor.RED + "Failed to load " + args.getString(0));
            }
        }

        @Command(aliases = {"disable"}, desc = "Disable a mechanic")
        @CommandPermissions({"craftbook.disable-mechanic"})
        public void disable(CommandContext args, final CommandSender sender) throws CommandPermissionsException {

            if(args.argsLength() > 0) {
                if(CraftBookPlugin.inst().disableMechanic(args.getString(0)))
                    sender.sendMessage(ChatColor.YELLOW + "Sucessfully disabled " + args.getString(0));
                else
                    sender.sendMessage(ChatColor.RED + "Failed to remove " + args.getString(0));
            }
        }
    }
}
