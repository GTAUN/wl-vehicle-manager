/**
 * WL Vehicle Manager Plugin
 * Copyright (C) 2013 MK124
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.gtaun.wl.vehicle.dialog;

import java.util.List;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.vehicle.VehicleUtils;
import net.gtaun.shoebill.constant.VehicleComponentModel;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.shoebill.object.VehicleDamage;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractListDialog;
import net.gtaun.wl.lang.LocalizedStringSet;
import net.gtaun.wl.vehicle.VehicleManagerServiceImpl;

public class VehicleDialog extends AbstractListDialog
{
	private final Vehicle vehicle;
	private final VehicleManagerServiceImpl vehicleManagerService;
	
	
	public VehicleDialog
	(final Player player, final Shoebill shoebill, final EventManager eventManager, AbstractDialog parentDialog, final Vehicle vehicle, final VehicleManagerServiceImpl vehicleManager)
	{
		super(player, shoebill, eventManager, parentDialog);
		this.vehicle = vehicle;
		this.vehicleManagerService = vehicleManager;
		final LocalizedStringSet stringSet = vehicleManagerService.getLocalizedStringSet();
		
		if (vehicle == null)
		{
			destroy();
			return;
		}
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.VehicleDialog.GetOn"))
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
				player.sendMessage(Color.LIGHTBLUE, stringSet.format(player, "Dialog.VehicleDialog.GetOnMessage", VehicleModel.getName(vehicle.getModelId())));
				vehicle.putPlayer(player, 0);
				destroy();
			}
		});
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.VehicleDialog.OwnThisVehicle"))
		{
			@Override
			public boolean isEnabled()
			{
				if (vehicleManager.isOwned(vehicle)) return false;
				if (VehicleUtils.getVehicleDriver(vehicle) != player) return false;
				return true;
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1057, player.getLocation());
				player.sendMessage(Color.LIGHTBLUE, stringSet.format(player, "Dialog.VehicleDialog.OwnMessage", VehicleModel.getName(vehicle.getModelId())));
				vehicleManager.ownVehicle(player, vehicle);
				show();
			}
		});
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.VehicleDialog.FetchAndGetOn"))
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

				player.sendMessage(Color.LIGHTBLUE, stringSet.format(player, "Dialog.VehicleDialog.FetchMessage", VehicleModel.getName(vehicle.getModelId())));
				destroy();
			}
		});
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.VehicleDialog.Repair"))
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
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.VehicleDialog.Flip"))
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
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.VehicleDialog.ChangeColor"))
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
				new VehicleResprayGroupDialog(player, shoebill, eventManager, VehicleDialog.this, vehicle, vehicleManager).show();
			}
		});
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.VehicleDialog.KickPassengers"))
		{
			@Override
			public boolean isEnabled()
			{
				if (VehicleUtils.getVehiclePassengers(vehicle).isEmpty()) return false;
				return VehicleUtils.isVehicleDriver(vehicle, player);
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				List<Player> passengers = VehicleUtils.getVehiclePassengers(vehicle);
				for (Player passenger : passengers)
				{
					passenger.sendMessage(Color.LIGHTBLUE, stringSet.format(player, "Dialog.VehicleDialog.KickPassengerMessage", player.getName()));
					passenger.removeFromVehicle();
				}
				
				player.sendMessage(Color.LIGHTBLUE, stringSet.format(player, "Dialog.VehicleDialog.KickCompleteMessage", passengers.size()));
				destroy();
			}
		});
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.VehicleDialog.Modifications"))
		{
			@Override
			public boolean isEnabled()
			{
				if (VehicleUtils.isVehicleDriver(vehicle, player) == false) return false;
				return VehicleComponentModel.isVehicleSupportAnyComponment(vehicle.getModelId());
			}
			
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				new VehicleComponentDialog(player, shoebill, eventManager, VehicleDialog.this, vehicle, vehicleManager).show();
			}
		});
		
		dialogListItems.add(new DialogListItemSwitch(stringSet.get(player, "Dialog.VehicleDialog.LockDoors"))
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
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.VehicleDialog.PersonalStatistics"))
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				new PlayerVehicleStatisticDialog(player, shoebill, eventManager, VehicleDialog.this, vehicle, vehicleManager).show();
			}
		});
		
		dialogListItems.add(new DialogListItem(stringSet.get(player, "Dialog.VehicleDialog.GlobalStatistics"))
		{
			@Override
			public void onItemSelect()
			{
				player.playSound(1083, player.getLocation());
				new GlobalVehicleStatisticDialog(player, shoebill, eventManager, VehicleDialog.this, vehicle, vehicleManager).show();
			}
		});
	}
	
	@Override
	public void show()
	{
		final LocalizedStringSet stringSet = vehicleManagerService.getLocalizedStringSet();
		
		int modelId = vehicle.getModelId();
		String name = VehicleModel.getName(modelId);
		
		boolean owned = vehicleManagerService.getOwnedVehicle(player) == vehicle;
		String ownMessage = owned ? stringSet.get(player, "Vehicle.Owned") : stringSet.get(player, "Vehicle.Normal");
		
		if (player.getVehicle() != vehicle)
		{
			Location loc = vehicle.getLocation();
			player.setCameraLookAt(loc);
			loc.setZ(loc.getZ() + 10.0f);
			player.setCameraPosition(loc);
		}
		
		this.caption = stringSet.format(player, "Dialog.VehicleDialog.Caption", ownMessage, name, modelId, vehicle.getHealth()/10);
		super.show();
	}
	
	@Override
	protected void destroy()
	{
		if (player.getVehicle() != vehicle) player.setCameraBehind();
		super.destroy();
	}
}
