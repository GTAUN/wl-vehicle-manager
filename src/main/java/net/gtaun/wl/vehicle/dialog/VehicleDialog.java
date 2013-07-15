/**
 * Copyright (C) 2013 MK124
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package net.gtaun.wl.vehicle.dialog;

import java.util.List;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.VehicleUtils;
import net.gtaun.shoebill.common.dialog.AbstractListDialog;
import net.gtaun.shoebill.constant.VehicleComponentModel;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.event.dialog.DialogResponseEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.shoebill.object.VehicleDamage;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.vehicle.VehicleManagerService;

public class VehicleDialog extends AbstractListDialog
{
	private final Vehicle vehicle;
	private final VehicleManagerService vehicleManager;
	
	
	public VehicleDialog
	(final Player player, final Shoebill shoebill, final EventManager eventManager, final Vehicle vehicle, final VehicleManagerService vehicleManager)
	{
		super(player, shoebill, eventManager);
		this.vehicle = vehicle;
		this.vehicleManager = vehicleManager;
		
		if (vehicle == null)
		{
			destroy();
			return;
		}
		
		dialogListItems.add(new DialogListItem("上车")
		{
			@Override
			public boolean isEnabled()
			{
				if (player.getVehicle() == vehicle) return false;
				if (vehicleManager.getOwnedVehicle(player) == vehicle) return true;
				if (VehicleUtils.getVehicleDriver(vehicle) != null) return false;
				return true;
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				player.sendMessage(Color.LIGHTBLUE, "%1$s: 你已传送到车子 %2$s 里了。", "车管", VehicleModel.getName(vehicle.getModelId()));
				vehicle.putPlayer(player, 0);
				destroy();
			}
		});
		
		dialogListItems.add(new DialogListItem("成为我的车子")
		{
			@Override
			public boolean isEnabled()
			{
				if (vehicleManager.isOwned(vehicle)) return false;
				return true;
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1057, player.getLocation());
				player.sendMessage(Color.LIGHTBLUE, "%1$s: 车子 %2$s 已成为您的专属座驾！", "车管", VehicleModel.getName(vehicle.getModelId()));
				vehicleManager.ownVehicle(player, vehicle);
				show();
			}
		});
		
		dialogListItems.add(new DialogListItem("传送到身边并上车")
		{
			@Override
			public boolean isEnabled()
			{
				if (vehicleManager.isOwned(vehicle) == false) return false;
				if (player.getVehicle() == vehicle) return false;
				return true;
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				
				vehicle.setLocation(player.getLocation());
				vehicle.putPlayer(player, 0);

				player.sendMessage(Color.LIGHTBLUE, "%1$s: 车子 %2$s 已传送到你身边！", "车管", VehicleModel.getName(vehicle.getModelId()));
				destroy();
			}
		});
		
		dialogListItems.add(new DialogListItem("修复车子")
		{
			@Override
			public boolean isEnabled()
			{
				if (VehicleUtils.isVehicleDriver(vehicle, player) == false) return false;
				
				VehicleDamage damage = vehicle.getDamage();
				if (damage.getDoors() != 0) return true;
				if (damage.getLights() != 0) return true;
				if (damage.getPanels() != 0) return true;
				if (damage.getTires() != 0) return true;
				if (vehicle.getHealth() < 1000.0f) return true;
				return false;
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1133, player.getLocation());
				vehicle.repair();
				show();
			}
		});
		
		dialogListItems.add(new DialogListItem("翻转车子")
		{
			@Override
			public boolean isEnabled()
			{
				return VehicleUtils.isVehicleDriver(vehicle, player);
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				vehicle.setLocation(vehicle.getLocation());
				show();
			}
		});
		
		dialogListItems.add(new DialogListItem("改变颜色")
		{
			@Override
			public boolean isEnabled()
			{
				return VehicleUtils.isVehicleDriver(vehicle, player);
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				show();
			}
		});
		
		dialogListItems.add(new DialogListItem("踢掉乘客")
		{
			@Override
			public boolean isEnabled()
			{
				return VehicleUtils.getVehiclePassengers(vehicle).isEmpty() == false;
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				List<Player> passengers = VehicleUtils.getVehiclePassengers(vehicle);
				for (Player passenger : passengers)
				{
					passenger.sendMessage(Color.LIGHTBLUE, "%1$s: 你被司机 %2$s 给踢下车了！", "车管", player.getName());
					passenger.removeFromVehicle();
				}
				
				player.sendMessage(Color.LIGHTBLUE, "%1$s: 已踢出 %2$d 个乘客！", "车管", passengers.size());
				destroy();
			}
		});
		
		dialogListItems.add(new DialogListItem("改装车子")
		{
			@Override
			public boolean isEnabled()
			{
				if (player.getVehicle() != vehicle) return false;
				return VehicleComponentModel.isVehicleSupportAnyComponment(vehicle.getModelId());
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				new VehicleComponentDialog(player, shoebill, eventManager, vehicle, vehicleManager).show();
				destroy();
			}
		});
		
		dialogListItems.add(new DialogListItemSwitch("锁车门")
		{
			@Override
			public boolean isEnabled()
			{
				return VehicleUtils.isVehicleDriver(vehicle, player);
			}
			
			@Override
			public boolean isSwitched()
			{
				return vehicle.getState().getDoors() != 0;
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				vehicle.getState().setDoors(vehicle.getState().getDoors() ^ 1);
				show();
			}
		});
	}
	
	@Override
	public void show()
	{
		int modelId = vehicle.getModelId();
		String name = VehicleModel.getName(modelId);
		
		boolean owned = vehicleManager.getOwnedVehicle(player) == vehicle;
		String ownMessage = owned ? "我的车子" : "车辆";
		
		if (player.getVehicle() != vehicle)
		{
			Location loc = vehicle.getLocation();
			player.setCameraLookAt(loc);
			loc.setZ(loc.getZ() + 10.0f);
			player.setCameraPosition(loc);
		}
		
		setCaption(String.format("%1$s: %2$s %3$s - 模型：%4$d, HP：%5$1.0f％", "车管", ownMessage, name, modelId, vehicle.getHealth()/10));
		super.show();
	}
	
	@Override
	protected void onDialogResponse(DialogResponseEvent event)
	{
		if (event.getDialogResponse() == 0)
		{
			player.playSound(1084, player.getLocation());
			new VehicleManagerDialog(player, shoebill, rootEventManager, vehicleManager).show();
		}
		
		super.onDialogResponse(event);
	}
	
	@Override
	protected void destroy()
	{
		player.setCameraBehind();
		super.destroy();
	}
}
