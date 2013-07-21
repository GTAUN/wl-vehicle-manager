package net.gtaun.wl.vehicle.dialog;

import java.util.Collection;
import java.util.Date;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.constant.DialogStyle;
import net.gtaun.shoebill.event.dialog.DialogResponseEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.vehicle.VehicleManagerService;
import net.gtaun.wl.vehicle.stat.PlayerVehicleStatistic;

import org.apache.commons.lang3.time.DateFormatUtils;

public class PlayerStatisticDialog extends AbstractDialog
{
	private final VehicleManagerService vehicleManager;
	
	
	public PlayerStatisticDialog(Player player, Shoebill shoebill, EventManager rootEventManager, final VehicleManagerService vehicleManager)
	{
		super(DialogStyle.MSGBOX, player, shoebill, rootEventManager);
		this.vehicleManager = vehicleManager;
	}
	
	@Override
	public void show()
	{
		long spawnCount = 0, driveCount = 0, driveSecondCount = 0;
		double damageCount = 0.0f, driveOdometer = 0.0f;
		Date lastUpdate = new Date(0);
		
		Collection<PlayerVehicleStatistic> stats = vehicleManager.getPlayerVehicleStatistics(player);
		for (PlayerVehicleStatistic stat : stats)
		{
			spawnCount += stat.getSpawnCount();
			driveCount += stat.getDriveCount();
			driveSecondCount += stat.getDriveTimeCount();
			damageCount += stat.getDamageCount();
			driveOdometer += stat.getDriveOdometer();
			
			Date update = stat.getLastUpdate();
			if (update != null && lastUpdate.before(update)) lastUpdate = update;
		}
		
		String caption = String.format("%1$s: 所有车辆的个人统计信息", "车管");
		setCaption(caption);
		
		String textFormat = caption + "\n" +
						"累计刷车次数: %1$d\n" +
						"累计损伤花费: %2$1.1f辆\n" +
						"累计驾驶次数: %3$d次\n" +
						"累计驾驶时间: %4$s\n" +
						"累计驾驶里程: %5$1.3f公里\n" +
						"平均驾驶速度: %6$1.2fKM/H\n" +
						"平均爆车率: %7$1.1f辆 / 10分钟\n" +
						"最后更新时间: %8$s";

		double avgSpeed = driveOdometer / 1000.0f / driveSecondCount * 60 * 60;
		double avgDamagePer10Minutes = damageCount / 1000.0f / driveSecondCount * 60 * 10;
		
		long seconds = driveSecondCount % 60;
		long minutes = driveSecondCount / 60;
		long hours = driveSecondCount / 60 / 60;
		String formatedTime = String.format("%1$d小时 %2$d分 %3$d秒", hours, minutes, seconds);
		
		String text = String.format
		(
			textFormat, spawnCount, damageCount/1000.0f, driveCount, formatedTime,
			driveOdometer/1000.0f, avgSpeed, avgDamagePer10Minutes,
			DateFormatUtils.ISO_DATETIME_FORMAT.format(lastUpdate)
		);
		
		show(text);
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
