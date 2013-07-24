package net.gtaun.wl.vehicle.dialog;

import java.util.List;

import org.apache.commons.lang3.time.DateFormatUtils;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractPageListDialog;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.vehicle.VehicleManagerService;
import net.gtaun.wl.vehicle.stat.OncePlayerVehicleStatistic;

public class RecordedOnceStatisticDialog extends AbstractPageListDialog
{
	public RecordedOnceStatisticDialog
	(final Player player, final Shoebill shoebill, final EventManager eventManager, final VehicleManagerService vehicleManager)
	{
		super(player, shoebill, eventManager);
		setCaption("车辆管理系统 - 驾驶记录");
		
		List<OncePlayerVehicleStatistic> stats = vehicleManager.getPlayerRecordedOnceStatistics(player);
		
		for (OncePlayerVehicleStatistic stat : stats)
		{
			final int modelId = stat.getModelId();
			final String modelName = VehicleModel.getName(modelId);
			final String startTime = DateFormatUtils.ISO_TIME_NO_T_FORMAT.format(stat.getStartTime());
			final String endTime = DateFormatUtils.ISO_TIME_NO_T_FORMAT.format(stat.getEndTime());
			
			String item = String.format("[驾驶记录] %1$s (%2$d) - %3$s ~ %4$s", modelId, modelName, startTime, endTime);
			dialogListItems.add(new DialogListItem(item)
			{
				@Override
				public void onItemSelect()
				{
					
				}
			});
		}
	}
	
}
