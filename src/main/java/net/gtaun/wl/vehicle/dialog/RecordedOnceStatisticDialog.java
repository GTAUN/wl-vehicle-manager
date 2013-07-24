package net.gtaun.wl.vehicle.dialog;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateFormatUtils;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractPageListDialog;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.event.dialog.DialogResponseEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.vehicle.VehicleManagerService;
import net.gtaun.wl.vehicle.stat.OncePlayerVehicleStatistic;

public class RecordedOnceStatisticDialog extends AbstractPageListDialog
{
	private final VehicleManagerService vehicleManager;
	
	public RecordedOnceStatisticDialog
	(final Player player, final Shoebill shoebill, final EventManager eventManager, final VehicleManagerService vehicleManager)
	{
		super(player, shoebill, eventManager);
		this.vehicleManager = vehicleManager;
		
		setCaption("车辆管理系统 - 驾驶记录");
		
		List<OncePlayerVehicleStatistic> stats = vehicleManager.getPlayerRecordedOnceStatistics(player);
		
		for (final OncePlayerVehicleStatistic stat : stats)
		{
			final int modelId = stat.getModelId();
			final String modelName = VehicleModel.getName(modelId);
			String startTimeStr = DateFormatUtils.ISO_TIME_NO_T_FORMAT.format(stat.getStartTime());
			String endTimeStr = "未知";
			
			Date endTime = stat.getEndTime();
			if (endTime != null) endTimeStr = DateFormatUtils.ISO_TIME_NO_T_FORMAT.format(stat.getEndTime());
			
			String item = String.format("[驾驶记录] %1$s (%2$d) - %3$s ~ %4$s", modelName, modelId, startTimeStr, endTimeStr);
			dialogListItems.add(new DialogListItem(item)
			{
				@Override
				public void onItemSelect()
				{
					new OnceStatisticDialog(player, shoebill, eventManager, vehicleManager, stat).show();
					destroy();
				}
			});
		}
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
}
