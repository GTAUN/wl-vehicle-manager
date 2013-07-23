package net.gtaun.wl.vehicle.dialog;

import java.util.Arrays;
import java.util.Comparator;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractPageListDialog;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.event.dialog.DialogResponseEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.vehicle.VehicleManagerService;
import net.gtaun.wl.vehicle.stat.GlobalVehicleStatistic;
import net.gtaun.wl.vehicle.stat.PlayerVehicleStatistic;
import net.gtaun.wl.vehicle.textdraw.VehicleCreateListTextDraw;
import net.gtaun.wl.vehicle.textdraw.VehicleCreateListTextDraw.ClickCallback;

import org.apache.commons.lang3.ArrayUtils;

public class VehicleCreateListDialog extends AbstractPageListDialog
{
	private final VehicleManagerService vehicleManager;
	private final String setName;
	private final int[] modelIds;
	
	private VehicleCreateListTextDraw previewTextDraw;
	
	
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
			
			setItemString(String.format("%1$s (型号: %2$d , 座位数: %3$d, 驾驶次数: %4$d, 人气: %5$d)", name, modelId, seats, driveCount, globalDriveCount));
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
	
	
	public VehicleCreateListDialog
	(final Player player, final Shoebill shoebill, final EventManager eventManager, final VehicleManagerService vehicleManager, final String setname, int[] modelIds)
	{
		super(player, shoebill, eventManager);
		this.vehicleManager = vehicleManager;
		this.setName = setname;
		
		Integer[] sortedModelIds = ArrayUtils.toObject(modelIds);
		Arrays.sort(sortedModelIds, new Comparator<Integer>()
		{
			@Override
			public int compare(Integer o1, Integer o2)
			{
				GlobalVehicleStatistic s1 = vehicleManager.getGlobalVehicleStatistic(o1);
				GlobalVehicleStatistic s2 = vehicleManager.getGlobalVehicleStatistic(o2);
				return (int) (s2.getDriveCount() - s1.getDriveCount());
			}
		});
		
		for (int modelId : sortedModelIds) dialogListItems.add(new DialogListItemVehicle(modelId));
		this.modelIds = ArrayUtils.toPrimitive(sortedModelIds);
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
				destroy();
			}
		};
		
		setCaption(String.format("%1$s: 刷车 - 车辆选择 - %2$s (%3$d/%4$d)", "车管", setName, getCurrentPage() + 1, getMaxPage() + 1));
		super.show();
		
		if (previewTextDraw != null) previewTextDraw.destroy();
		
		int index = getCurrentPage() * getItemsPerPage();
		int[] nowIds = ArrayUtils.subarray(modelIds, index, index+getItemsPerPage());
		previewTextDraw = new VehicleCreateListTextDraw(player, shoebill, rootEventManager, vehicleManager, nowIds, callback);
		previewTextDraw.show();
	}
	
	@Override
	protected void onDialogResponse(DialogResponseEvent event)
	{
		if (event.getDialogResponse() == 0)
		{
			player.playSound(1084, player.getLocation());
			new VehicleCreateMainDialog(player, shoebill, rootEventManager, vehicleManager).show();
		}
		
		super.onDialogResponse(event);
	}
	
	@Override
	protected void destroy()
	{
		if (previewTextDraw != null) previewTextDraw.destroy();
		super.destroy();
	}
}
