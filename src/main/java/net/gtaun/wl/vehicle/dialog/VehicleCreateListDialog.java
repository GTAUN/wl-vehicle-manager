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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.ListDialogItem;
import net.gtaun.shoebill.common.dialog.ListDialogItemRadio;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.WlPageListDialog;
import net.gtaun.wl.lang.LocalizedStringSet.PlayerStringSet;
import net.gtaun.wl.vehicle.VehicleManagerServiceImpl;
import net.gtaun.wl.vehicle.stat.GlobalVehicleStatistic;
import net.gtaun.wl.vehicle.stat.PlayerVehicleStatistic;
import net.gtaun.wl.vehicle.textdraw.VehicleCreateListTextDraw;

import org.apache.commons.lang3.ArrayUtils;

public class VehicleCreateListDialog extends WlPageListDialog
{
	public class ListDialogItemVehicle extends ListDialogItem
	{
		private final int modelId;
		private final long driveCount;
		private final long globalDriveCount;
		
		public ListDialogItemVehicle(int modelId)
		{
			this.modelId = modelId;
			
			String name = VehicleModel.getName(modelId);
			int seats = VehicleModel.getSeats(modelId);

			PlayerVehicleStatistic stat = vehicleManagerService.getPlayerVehicleStatistic(player, modelId);
			GlobalVehicleStatistic globalStat = vehicleManagerService.getGlobalVehicleStatistic(modelId);
			
			driveCount = stat.getDriveCount();
			globalDriveCount = globalStat.getDriveCount();
			
			setItemText(() -> stringSet.format("Dialog.VehicleCreateListDialog.Item", name, modelId, seats, driveCount, globalDriveCount));
		}

		@Override
		public void onItemSelect()
		{
			player.playSound(1057, player.getLocation());
			
			Vehicle vehicle = vehicleManagerService.createOwnVehicle(player, modelId);
			vehicle.putPlayer(player, 0);
			player.sendMessage(Color.LIGHTBLUE, stringSet.format("Dialog.VehicleCreateListDialog.CreateMessage", VehicleModel.getName(vehicle.getModelId())));
			destroy();
		}
	}
	

	private final VehicleManagerServiceImpl vehicleManagerService;
	private final PlayerStringSet stringSet;
	
	private final Integer[] modelIds;
	
	private VehicleCreateListTextDraw previewTextdraw;
	
	private List<Comparator<Integer>> modelIdComparators;
	private Comparator<Integer> modelIdComparator;
	
	
	public VehicleCreateListDialog
	(Player player, EventManager eventManager, AbstractDialog parent, VehicleManagerServiceImpl service, String setName, int[] modelIds)
	{
		super(player, eventManager);
		setParentDialog(parent);
		
		this.vehicleManagerService = service;
		this.modelIds = ArrayUtils.toObject(modelIds);
		this.stringSet = vehicleManagerService.getLocalizedStringSet().getStringSet(player);

		setCaption((d) -> stringSet.format("Dialog.VehicleCreateListDialog.Caption", setName, getCurrentPage() + 1, getMaxPage() + 1));
		
		
		modelIdComparators = new ArrayList<>();
		
		modelIdComparators.add((o1, o2) ->
			(int) (service.getGlobalVehicleStatistic(o2).getDriveCount() - service.getGlobalVehicleStatistic(o1).getDriveCount()));
		
		modelIdComparators.add((o1, o2) ->
			(int) (service.getPlayerVehicleStatistic(player, o2).getDriveCount() - service.getPlayerVehicleStatistic(player, o1).getDriveCount()));
		
		modelIdComparators.add((o1, o2) ->
			(int) (VehicleModel.getSeats(o2) - VehicleModel.getSeats(o1)));
		
		modelIdComparators.add((o1, o2) ->
			(int) (VehicleModel.getType(o1).ordinal() - VehicleModel.getType(o2).ordinal()));
		
		modelIdComparator = modelIdComparators.get(0);
		changeSortMode(0);
		
		
		setCloseHandler((d, t) -> destroyPreviewTextdraw());
	}
	
	private void changeSortMode(int mode)
	{
		items.clear();
		items.add(ListDialogItemRadio.create()
			.itemText(stringSet.get("Dialog.VehicleCreateListDialog.ItemSortMode"))
			.item(stringSet.get("Vehicle.SortMode.Popular"),		Color.LIGHTPINK)
			.item(stringSet.get("Vehicle.SortMode.SpawnedTimes"),	Color.LIGHTBLUE)
			.item(stringSet.get("Vehicle.SortMode.Seats"),			Color.LIGHTGREEN)
			.item(stringSet.get("Vehicle.SortMode.Type"),			Color.LIGHTYELLOW)
			.selectedIndex(() -> modelIdComparators.indexOf(modelIdComparator))
			.onRadioItemSelect((item, index) ->
			{
				player.playSound(1083, player.getLocation());
				modelIdComparator = modelIdComparators.get(index);
				changeSortMode(index);
				show();
			})
			.build());
		
		Arrays.sort(this.modelIds, modelIdComparator);
		for (int modelId : modelIds) items.add(new ListDialogItemVehicle(modelId));
	}
	
	@Override
	public void show()
	{
		super.show();
		showPreviewTextdraw();
	}
	
	private void showPreviewTextdraw()
	{
		destroyPreviewTextdraw();
		
		int index = getCurrentPage() * getItemsPerPage();
		Integer[] nowIds = ArrayUtils.subarray(modelIds, index, index+getItemsPerPage());
		previewTextdraw = new VehicleCreateListTextDraw(player, eventManagerNode.getParent(), vehicleManagerService, nowIds, (modelId) ->
		{
			player.cancelDialog();
			player.playSound(1057, player.getLocation());
			
			Vehicle vehicle = vehicleManagerService.createOwnVehicle(player, modelId);
			vehicle.putPlayer(player, 0);
			stringSet.sendMessage(Color.LIGHTBLUE, "Dialog.VehicleCreateListDialog.CreateMessage", VehicleModel.getName(vehicle.getModelId()));
		});
		previewTextdraw.show();
	}
	
	private void destroyPreviewTextdraw()
	{
		if (previewTextdraw == null) return;
		
		previewTextdraw.destroy();
		previewTextdraw = null;
	}
}
