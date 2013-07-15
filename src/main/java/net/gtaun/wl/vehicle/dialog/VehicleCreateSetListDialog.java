package net.gtaun.wl.vehicle.dialog;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractPageListDialog;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.event.dialog.DialogResponseEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.vehicle.VehicleManagerService;

public class VehicleCreateSetListDialog extends AbstractPageListDialog
{
	private final VehicleManagerService vehicleManager;
	private final String setName;
	
	
	public VehicleCreateSetListDialog
	(final Player player, final Shoebill shoebill, final EventManager eventManager, final VehicleManagerService vehicleManager, final String setname, int[] modelIds)
	{
		super(player, shoebill, eventManager);
		this.vehicleManager = vehicleManager;
		this.setName = setname;
		
		for(final int modelId : modelIds)
		{
			final String name = VehicleModel.getName(modelId);
			final int seats = VehicleModel.getSeats(modelId);
			
			dialogListItems.add(new DialogListItem(String.format("%1$s (型号: %2$d , 座位数: %3$d)", name, modelId, seats))
			{
				@Override
				public void onItemSelect()
				{
					player.playSound(1057, player.getLocation());
					
					Vehicle vehicle = shoebill.getSampObjectFactory().createVehicle(modelId, player.getLocation(), 0.0f, 0, 0, 3600);
					vehicleManager.ownVehicle(player, vehicle);
					vehicle.putPlayer(player, 0);
					player.sendMessage(Color.LIGHTBLUE, "%1$s: 您的专属座驾 %2$s 已创建！", "车管", VehicleModel.getName(vehicle.getModelId()));
					destroy();
				}
			});
		}
	}
	
	@Override
	public void show()
	{
		setCaption(String.format("%1$s: 刷车 - 车辆类型选择 - 集合：%2$s (%3$d/%4$d)", "车管", setName, getCurrentPage() + 1, getMaxPage() + 1));
		super.show();
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
}
