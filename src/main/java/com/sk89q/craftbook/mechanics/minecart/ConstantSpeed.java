package com.sk89q.craftbook.mechanics.minecart;

import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.util.Vector;

import com.sk89q.craftbook.AbstractCraftBookMechanic;
import com.sk89q.craftbook.util.EventUtil;
import com.sk89q.craftbook.util.RailUtil;
import com.sk89q.util.yaml.YAMLProcessor;

public class ConstantSpeed extends AbstractCraftBookMechanic {

    @EventHandler(priority = EventPriority.HIGH)
    public void onVehicleMove(VehicleMoveEvent event) {

        if(!EventUtil.passesFilter(event)) return;

        if (!(event.getVehicle() instanceof Minecart)) return;

        if (RailUtil.isTrack(event.getTo().getBlock().getType()) && event.getVehicle().getVelocity().lengthSquared() > 0) {
            Vector vel = event.getVehicle().getVelocity();
            event.getVehicle().setVelocity(vel.normalize().multiply(speed));
        }
    }

    double speed;

    @Override
    public void loadConfiguration (YAMLProcessor config, String path) {

        config.setComment(path + "speed", "Sets the speed to move at constantly.");
        speed = config.getDouble(path + "speed", 0.5);
    }
}