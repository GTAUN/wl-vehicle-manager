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

import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItemSwitch;
import net.gtaun.shoebill.common.vehicle.VehicleUtils;
import net.gtaun.shoebill.constant.VehicleComponentModel;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.shoebill.object.VehicleDamage;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.WlListDialog;
import net.gtaun.wl.lang.LocalizedStringSet.PlayerStringSet;
import net.gtaun.wl.vehicle.VehicleManagerServiceImpl;

public class VehicleDialog
{
	public static WlListDialog create
	(Player player, EventManager eventManager, AbstractDialog parent, Vehicle vehicle, VehicleManagerServiceImpl service)
	{
		if (vehicle == null) throw new NullPointerException();
		
		PlayerStringSet stringSet = service.getLocalizedStringSet().getStringSet(player);
		
		return WlListDialog.create(player, eventManager)
			.parentDialog(parent)
			.caption((d) ->
			{
				int modelId = vehicle.getModelId();
				String name = VehicleModel.getName(modelId);
						
				boolean owned = service.getOwnedVehicle(player) == vehicle;
				String ownMessage = stringSet.get(owned ? "Vehicle.Owned" : "Vehicle.Normal");
						
				if (player.getVehicle() != vehicle)
				{
					Location loc = vehicle.getLocation();
					player.setCameraLookAt(loc);
					loc.setZ(loc.getZ() + 10.0f);
					player.setCameraPosition(loc);
				}
						
				return stringSet.format("Dialog.VehicleDialog.Caption", ownMessage, name, modelId, vehicle.getHealth()/10);
			})
			
			.item(stringSet.get("Dialog.VehicleDialog.GetOn"), () ->
			{
				if (player.getVehicle() == vehicle) return false;
				if (service.getOwnedVehicle(player) == vehicle) return true;
				if (VehicleUtils.getVehicleDriver(vehicle) != null) return false;
				return true;
			}, (i) ->
			{
				player.playSound(1083);
				stringSet.sendMessage(Color.LIGHTBLUE, "Dialog.VehicleDialog.GetOnMessage", VehicleModel.getName(vehicle.getModelId()));
				vehicle.putPlayer(player, 0);
			})
			
			.item(stringSet.get("Dialog.VehicleDialog.OwnThisVehicle"), () ->
			{
				if (service.isOwned(vehicle)) return false;
				if (VehicleUtils.getVehicleDriver(vehicle) != player) return false;
				return true;
			}, (i) ->
			{
				player.playSound(1057);
				stringSet.sendMessage(Color.LIGHTBLUE, "Dialog.VehicleDialog.OwnMessage", VehicleModel.getName(vehicle.getModelId()));
				service.ownVehicle(player, vehicle);
				i.getCurrentDialog().show();
			})
			
			.item(stringSet.get("Dialog.VehicleDialog.FetchAndGetOn"), () ->
			{
				if (service.isOwned(vehicle) == false) return false;
				if (player.getVehicle() == vehicle) return false;
				return true;
			}, (i) ->
			{
				player.playSound(1083);
						
				vehicle.setLocation(player.getLocation());
				vehicle.putPlayer(player, 0);
	
				stringSet.sendMessage(Color.LIGHTBLUE, "Dialog.VehicleDialog.FetchMessage", VehicleModel.getName(vehicle.getModelId()));
			})
			
			.item(stringSet.get("Dialog.VehicleDialog.Repair"), () ->
			{
				if (VehicleUtils.isVehicleDriver(vehicle, player) == false) return false;
					
				VehicleDamage damage = vehicle.getDamage();
				if (damage.getDoors() != 0) return true;
				if (damage.getLights() != 0) return true;
				if (damage.getPanels() != 0) return true;
				if (damage.getTires() != 0) return true;
				if (vehicle.getHealth() < 1000.0f) return true;
				return false;
			}, (i) ->
			{
				player.playSound(1133);
				vehicle.repair();
				i.getCurrentDialog().show();
			})
			
			.item(stringSet.get("Dialog.VehicleDialog.Flip"), () -> VehicleUtils.isVehicleDriver(vehicle, player), (i) ->
			{
				player.playSound(1083);
				vehicle.setLocation(vehicle.getLocation());
				i.getCurrentDialog().show();
			})
			
			.item(stringSet.get("Dialog.VehicleDialog.ChangeColor"), () -> VehicleUtils.isVehicleDriver(vehicle, player), (i) ->
			{
				player.playSound(1083);
				VehicleResprayGroupDialog.create(player, eventManager, i.getCurrentDialog(), vehicle, service).show();
			})
			
			.item(stringSet.get("Dialog.VehicleDialog.KickPassengers"), () ->
			{
				if (VehicleUtils.getVehiclePassengers(vehicle).isEmpty()) return false;
				return VehicleUtils.isVehicleDriver(vehicle, player);
			}, (i) ->
			{
				player.playSound(1083);
				List<Player> passengers = VehicleUtils.getVehiclePassengers(vehicle);
				for (Player passenger : passengers)
				{
					stringSet.forOthers(passenger).sendMessage(Color.LIGHTBLUE, "Dialog.VehicleDialog.KickPassengerMessage", player.getName());
					passenger.removeFromVehicle();
				}
				
				stringSet.sendMessage(Color.LIGHTBLUE, "Dialog.VehicleDialog.KickCompleteMessage", passengers.size());
			})
			
			.item(stringSet.get("Dialog.VehicleDialog.Modifications"), () ->
			{
				if (VehicleUtils.isVehicleDriver(vehicle, player) == false) return false;
				return VehicleComponentModel.isVehicleSupportAnyComponment(vehicle.getModelId());
			}, (i) ->
			{
				player.playSound(1083);
				VehicleComponentDialog.create(player, eventManager, i.getCurrentDialog(), vehicle, service).show();
			})
				
			.item(ListDialogItemSwitch.create()
				.enabled(() -> VehicleUtils.isVehicleDriver(vehicle, player))
				.statusSupplier(() -> vehicle.getState().getDoors() != 0)
				.onSelect((i) ->
				{
					player.playSound(1083);
					vehicle.getState().setDoors(vehicle.getState().getDoors() ^ 1);
					i.getCurrentDialog().show();
				})
				.build())
				
			.item(stringSet.get("Dialog.VehicleDialog.PersonalStatistics"), (i) ->
			{
				player.playSound(1083);
				PlayerVehicleStatisticDialog.create(player, eventManager, i.getCurrentDialog(), vehicle, service).show();
			})
			
			.item(stringSet.get("Dialog.VehicleDialog.GlobalStatistics"), (i) ->
			{
				player.playSound(1083);
				GlobalVehicleStatisticDialog.create(player, eventManager, i.getCurrentDialog(), vehicle, service).show();
			})
			
			.onShow((d) ->
			{
				if (player.getVehicle() == vehicle) return;
				
				Location loc = vehicle.getLocation();
				player.setCameraLookAt(loc);
				loc.setZ(loc.getZ() + 10.0f);
				player.setCameraPosition(loc);
			})
			
			.onClose((d, t) ->
			{
				if (player.getVehicle() != vehicle) player.setCameraBehind();
			})
			
			.build();
	}
}
