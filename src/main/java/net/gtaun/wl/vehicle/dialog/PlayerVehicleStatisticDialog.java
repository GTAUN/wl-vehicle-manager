package net.gtaun.wl.vehicle.dialog;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.constant.DialogStyle;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.event.dialog.DialogResponseEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.vehicle.VehicleManagerService;
import net.gtaun.wl.vehicle.stat.PlayerVehicleStatistic;

import org.apache.commons.lang3.time.DateFormatUtils;

public class PlayerVehicleStatisticDialog extends AbstractDialog
{
	private final Vehicle vehicle;
	private final VehicleManagerService vehicleManager;
	
	
	public PlayerVehicleStatisticDialog(Player player, Shoebill shoebill, EventManager rootEventManager, final Vehicle vehicle, final VehicleManagerService vehicleManager)
	{
		super(DialogStyle.MSGBOX, player, shoebill, rootEventManager);
		this.vehicle = vehicle;
		this.vehicleManager = vehicleManager;
	}
	
	@Override
	public void show()
	{
		int modelId = vehicle.getModelId();
		String name = VehicleModel.getName(modelId);
		PlayerVehicleStatistic stat = vehicleManager.getPlayerVehicleStatistic(player, modelId);
		
		String caption = String.format("%1$s: %2$s (模型: %3$d) 的个人统计信息", "车管", name, modelId);
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

		double avgSpeed = stat.getDriveOdometer() / stat.getDriveTimeCount() * 60 * 60 / 1000.0f;
		double avgDamagePer10Minutes = stat.getDamageCount() / 1000.0f / stat.getDriveTimeCount() * 60 * 10;
		
		long seconds = stat.getDriveTimeCount() % 60;
		long minutes = stat.getDriveTimeCount() / 60;
		long hours = stat.getDriveTimeCount() / 60 / 60;
		String formatedTime = String.format("%1$d小时 %2$d分 %3$d秒", hours, minutes, seconds);
		
		String text = String.format
		(
			textFormat, stat.getSpawnCount(), stat.getDamageCount()/1000.0f, stat.getDriveCount(), formatedTime,
			stat.getDriveOdometer()/1000.0f, avgSpeed, avgDamagePer10Minutes,
			DateFormatUtils.ISO_DATETIME_FORMAT.format(stat.getLastUpdate())
		);
		
		show(text);
	}
	
	@Override
	protected void onDialogResponse(DialogResponseEvent event)
	{
		if (event.getDialogResponse() == 0)
		{
			player.playSound(1084, player.getLocation());
			new VehicleDialog(player, shoebill, rootEventManager, vehicle, vehicleManager).show();
		}
		
		super.onDialogResponse(event);
	}
}
