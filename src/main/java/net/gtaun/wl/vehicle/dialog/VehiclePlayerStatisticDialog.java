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

public class VehiclePlayerStatisticDialog extends AbstractDialog
{
	private final Vehicle vehicle;
	private final VehicleManagerService vehicleManager;
	
	
	public VehiclePlayerStatisticDialog(Player player, Shoebill shoebill, EventManager rootEventManager, final Vehicle vehicle, final VehicleManagerService vehicleManager)
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
		
		setCaption(String.format("%1$s: %2$s (模型: %3$d) 的个人统计信息", "车管", name, modelId));
		
		String textFormat =
						"累计刷车次数: %1$d\n" +
						"累计损伤花费: %2$1.1f辆\n" +
						"累计驾驶次数: %3$d次\n" +
						"累计驾驶时间: %4$d秒\n" +
						"累计里程次数: %5$1.1f米\n" +
						"最后更新时间: %6$s";
		
		String text = String.format
		(
			textFormat, stat.getSpawnCount(), stat.getDamageCount()/1000.0f, stat.getDriveCount(),
			stat.getDriveTimeCount(), stat.getDriveOdometer(), stat.getLastUpdate()
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
