package net.gtaun.wl.vehicle.dialog;

import java.util.Date;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.constant.DialogStyle;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.event.dialog.DialogResponseEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.vehicle.VehicleManagerService;
import net.gtaun.wl.vehicle.stat.OncePlayerVehicleStatistic;

import org.apache.commons.lang3.time.DateFormatUtils;

public class OnceStatisticDialog extends AbstractDialog
{
	private final VehicleManagerService vehicleManager;
	private final OncePlayerVehicleStatistic stat;
	
	
	public OnceStatisticDialog(Player player, Shoebill shoebill, EventManager rootEventManager, VehicleManagerService vehicleManager, OncePlayerVehicleStatistic stat)
	{
		super(DialogStyle.MSGBOX, player, shoebill, rootEventManager);
		this.vehicleManager = vehicleManager;
		this.stat = stat;
	}
	
	@Override
	public void show()
	{
		int modelId = stat.getModelId();
		String name = VehicleModel.getName(modelId);

		String startTimeStr = "未知";
		Date startTime = stat.getStartTime();
		if (startTime != null) startTimeStr = DateFormatUtils.ISO_TIME_NO_T_FORMAT.format(startTime);
		
		String endTimeStr = "未知";
		Date endTime = stat.getEndTime();
		if (endTime != null) endTimeStr = DateFormatUtils.ISO_TIME_NO_T_FORMAT.format(endTime);
		
		String caption = String.format("%1$s: %2$s (%3$d) 的驾驶记录信息 (%4$s~%5$s)", "车管", name, modelId, startTimeStr, endTimeStr);
		setCaption(caption);
		
		String textFormat = 
						"累计损伤花费: %1$1.1f辆\n" +
						"累计驾驶时间: %2$s\n" +
						"累计驾驶里程: %3$1.3f公里\n" +
						"平均驾驶速度: %4$1.2fKM/H\n" +
						"最高驾驶速度: %5$1.2fKM/H\n" +
						"平均爆车率: %6$1.1f辆 / 10分钟\n" +
						"开始驾驶时间: %7$s\n" + 
						"停止驾驶时间: %8$s";

		double avgSpeed = stat.getDriveOdometer() / stat.getDriveSecondCount() * 60 * 60 / 1000.0f;
		double maxSpeed = stat.getMaxSpeed() * 60 * 60 / 1000.0f;
		double avgDamagePer10Minutes = stat.getDamageCount() / 1000.0f / stat.getDriveSecondCount() * 60 * 10;
		
		long seconds = stat.getDriveSecondCount() % 60;
		long minutes = (stat.getDriveSecondCount() / 60) % 60;
		long hours = stat.getDriveSecondCount() / 60 / 60;
		String formatedTime = String.format("%1$d小时 %2$d分 %3$d秒", hours, minutes, seconds);
		
		String text = String.format
		(
			textFormat, stat.getDamageCount()/1000.0f, formatedTime, stat.getDriveOdometer()/1000.0f,
			avgSpeed, maxSpeed, avgDamagePer10Minutes, startTimeStr, endTimeStr
		);
		
		show(text);
	}
	
	@Override
	protected void onDialogResponse(DialogResponseEvent event)
	{
		if (event.getDialogResponse() == 0)
		{
			player.playSound(1084, player.getLocation());
			new RecordedOnceStatisticDialog(player, shoebill, rootEventManager, vehicleManager).show();
		}
		
		super.onDialogResponse(event);
	}
}
