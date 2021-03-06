// $Id$
/*
 * CraftBook Copyright (C) 2010 sk89q <http://www.sk89q.com>
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/>.
 */

package com.sk89q.craftbook.mechanics;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.sk89q.craftbook.AbstractCraftBookMechanic;
import com.sk89q.craftbook.LocalPlayer;
import com.sk89q.craftbook.bukkit.CraftBookPlugin;
import com.sk89q.craftbook.util.EventUtil;
import com.sk89q.craftbook.util.ItemInfo;
import com.sk89q.craftbook.util.ProtectionUtil;
import com.sk89q.util.yaml.YAMLProcessor;

/**
 * This allows users to Right-click to check the light level.
 */
public class LightStone extends AbstractCraftBookMechanic {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onRightClick(PlayerInteractEvent event) {

        if (!EventUtil.passesFilter(event))
            return;

        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        LocalPlayer player = CraftBookPlugin.inst().wrapPlayer(event.getPlayer());

        if(!item.equals(player.getHeldItemInfo())) return;
        if(!player.hasPermission("craftbook.mech.lightstone.use")) {
            if(CraftBookPlugin.inst().getConfiguration().showPermissionMessages)
                player.printError("mech.use-permission");
            return;
        }

        if(!ProtectionUtil.canUse(event.getPlayer(), event.getClickedBlock().getLocation(), event.getBlockFace(), event.getAction())) {
            if(CraftBookPlugin.inst().getConfiguration().showPermissionMessages)
                player.printError("area.use-permissions");
            return;
        }

        Block block = event.getClickedBlock().getRelative(event.getBlockFace());
        player.printRaw(ChatColor.YELLOW + player.translate("mech.lightstone.lightstone") + " [" + getLightLine(block.getLightLevel()) + ChatColor.YELLOW + "] " + block.getLightLevel() + " L");
    }

    private String getLightLine(int data) {

        StringBuilder line = new StringBuilder(25);
        if (data >= 9)
            line.append(ChatColor.GREEN);
        else
            line.append(ChatColor.DARK_RED);
        for (int i = 0; i < data; i++)
            line.append("|");
        line.append(ChatColor.BLACK);
        for (int i = data; i < 15; i++)
            line.append("|");
        return line.toString();
    }

    public ItemInfo item;

    @Override
    public void loadConfiguration (YAMLProcessor config, String path) {

        config.setComment(path + "item", "The item that the lightstone mechanic uses.");
        item = new ItemInfo(config.getString(path + "item", "GLOWSTONE_DUST"));
    }
}