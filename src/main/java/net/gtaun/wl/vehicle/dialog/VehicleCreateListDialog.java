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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.event.dialog.DialogCancelEvent.DialogCancelType;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.common.dialog.AbstractPageListDialog;
import net.gtaun.wl.vehicle.VehicleManagerService;
import net.gtaun.wl.vehicle.stat.GlobalVehicleStatistic;
import net.gtaun.wl.vehicle.stat.PlayerVehicleStatistic;
import net.gtaun.wl.vehicle.textdraw.VehicleCreateListTextDraw;
import net.gtaun.wl.vehicle.textdraw.VehicleCreateListTextDraw.ClickCallback;

import org.apache.commons.lang3.ArrayUtils;

public class VehicleCreateListDialog extends AbstractPageListDialog
{
	public class DialogListItemVehicle extends DialogListItem
	{
		private final int modelId;
		private final long driveCount;
		private final long globalDriveCount;
		
		public DialogListItemVehicle(int modelId)
		{
			this.modelId = modelId;
			
			final String name = VehicleModel.getName(modelId);
			final int seats = VehicleModel.getSeats(modelId);

			PlayerVehicleStatistic stat = vehicleManager.getPlayerVehicleStatistic(player, modelId);
			GlobalVehicleStatistic globalStat = vehicleManager.getGlobalVehicleStatistic(modelId);
			
			driveCount = stat.getDriveCount();
			globalDriveCount = globalStat.getDriveCount();
			
			this.itemString = (String.format("%1$s (型号: %2$d , 座位数: %3$d, 驾驶次数: %4$d, 人气: %5$d)", name, modelId, seats, driveCount, globalDriveCount));
		}

		@Override
		public void onItemSelect()
		{
			player.playSound(1057, player.getLocation());
			
			Vehicle vehicle = vehicleManager.createOwnVehicle(player, modelId);
			vehicle.putPlayer(player, 0);
			player.sendMessage(Color.LIGHTBLUE, "%1$s: 您的专属座驾 %2$s 已创建！", "车管", VehicleModel.getName(vehicle.getModelId()));
			destroy();
		}
	}
	
	
	private final VehicleManagerService vehicleManager;
	private final String setName;
	private final Integer[] modelIds;
	
	private VehicleCreateListTextDraw previewTextdraw;
	
	private List<Comparator<Integer>> modelIdComparators;
	private Comparator<Integer> modelIdComparator;
	
	
	public VehicleCreateListDialog
	(final Player player, final Shoebill shoebill, final EventManager eventManager, AbstractDialog parentDialog, final VehicleManagerService vehicleManager, final String setname, int[] modelIds)
	{
		super(player, shoebill, eventManager, parentDialog);
		this.vehicleManager = vehicleManager;
		this.setName = setname;
		
		modelIdComparators = new ArrayList<>();
		modelIdComparators.add(new Comparator<Integer>()
		{
			@Override
			public int compare(Integer o1, Integer o2)
			{
				GlobalVehicleStatistic s1 = vehicleManager.getGlobalVehicleStatistic(o1);
				GlobalVehicleStatistic s2 = vehicleManager.getGlobalVehicleStatistic(o2);
				return (int) (s2.getDriveCount() - s1.getDriveCount());
			}
		});
		modelIdComparators.add(new Comparator<Integer>()
		{
			@Override
			public int compare(Integer o1, Integer o2)
			{
				PlayerVehicleStatistic s1 = vehicleManager.getPlayerVehicleStatistic(player, o1);
				PlayerVehicleStatistic s2 = vehicleManager.getPlayerVehicleStatistic(player, o2);
				return (int) (s2.getDriveCount() - s1.getDriveCount());
			}
		});
		modelIdComparators.add(new Comparator<Integer>()
		{
			@Override
			public int compare(Integer o1, Integer o2)
			{
				return (int) (VehicleModel.getSeats(o2) - VehicleModel.getSeats(o1));
			}
		});
		modelIdComparators.add(new Comparator<Integer>()
		{
			@Override
			public int compare(Integer o1, Integer o2)
			{
				return (int) (VehicleModel.getType(o1).ordinal() - VehicleModel.getType(o2).ordinal());
			}
		});
		
		this.modelIds = ArrayUtils.toObject(modelIds);
		modelIdComparator = modelIdComparators.get(0);
		
		changeSortMode(0);
	}
	
	private void changeSortMode(int mode)
	{
		dialogListItems.clear();
		dialogListItems.add(new DialogListItemRadio("排序方式: ")
		{
			{
				addItem(new RadioItem("人气", Color.LIGHTPINK));
				addItem(new RadioItem("使用次数", Color.LIGHTBLUE));
				addItem(new RadioItem("座位数", Color.LIGHTGREEN));
				addItem(new RadioItem("类型", Color.LIGHTYELLOW));
			}
			
			@Override
			public int getSelected()
			{
				return modelIdComparators.indexOf(modelIdComparator);
			}
			
			@Override
			public void onItemSelect(RadioItem item, int itemIndex)
			{
				player.playSound(1083, player.getLocation());
				modelIdComparator = modelIdComparators.get(itemIndex);
				changeSortMode(itemIndex);
				show();
			}
		});
		
		Arrays.sort(this.modelIds, modelIdComparator);
		for (int modelId : modelIds) dialogListItems.add(new DialogListItemVehicle(modelId));
	}
	
	@Override
	public void onPageUpdate()
	{
		player.playSound(1083, player.getLocation());
	}
	
	@Override
	public void show()
	{
		ClickCallback callback = new ClickCallback()
		{
			@Override
			public void onClick(int modelId)
			{
				player.cancelDialog();
				player.playSound(1057, player.getLocation());
				
				Vehicle vehicle = vehicleManager.createOwnVehicle(player, modelId);
				vehicle.putPlayer(player, 0);
				player.sendMessage(Color.LIGHTBLUE, "%1$s: 您的专属座驾 %2$s 已创建！", "车管", VehicleModel.getName(vehicle.getModelId()));
				destroyPreviewTextdraw();
			}
		};
		
		this.caption = String.format("%1$s: 刷车 - 车辆选择 - %2$s (%3$d/%4$d)", "车管", setName, getCurrentPage() + 1, getMaxPage() + 1);
		super.show();
		
		destroyPreviewTextdraw();
		
		int index = getCurrentPage() * getItemsPerPage();
		Integer[] nowIds = ArrayUtils.subarray(modelIds, index, index+getItemsPerPage());
		previewTextdraw = new VehicleCreateListTextDraw(player, shoebill, rootEventManager, vehicleManager, nowIds, callback);
		previewTextdraw.show();
	}
	
	@Override
	protected void onClickOk(DialogListItem item)
	{
		destroyPreviewTextdraw();
	}
	
	@Override
	protected void onClickCancel()
	{
		destroyPreviewTextdraw();
		super.onClickCancel();
	}
	
	@Override
	protected void onCancel(DialogCancelType type)
	{
		destroyPreviewTextdraw();
	}
	
	private void destroyPreviewTextdraw()
	{
		if (previewTextdraw == null) return;
		
		previewTextdraw.destroy();
		previewTextdraw = null;
	}
}
