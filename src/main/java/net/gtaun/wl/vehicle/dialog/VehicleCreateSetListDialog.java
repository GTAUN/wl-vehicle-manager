package net.gtaun.wl.vehicle.dialog;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractPageListDialog;
import net.gtaun.shoebill.constant.VehicleModel;
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
			
			dialogListItems.add(new DialogListItem(String.format("%1$d - %2$s (座位数: %3$d)", modelId, name, seats))
			{
				@Override
				public void onItemSelect()
				{
					Vehicle vehicle = shoebill.getSampObjectFactory().createVehicle(modelId, player.getLocation(), 0.0f, 0, 0, 3600);
					vehicleManager.ownVehicle(player, vehicle);
					vehicle.putPlayer(player, 0);
					destroy();
				}
			});
		}
	}
	
	@Override
	public void show()
	{
		setCaption(String.format("刷车 - 车辆类型选择 - 集合：%1$s (%2$d/%3$d)", setName, getCurrentPage(), getMaxPage()));
		super.show();
	}
	
	@Override
	protected void onDialogResponse(DialogResponseEvent event)
	{
		if (event.getDialogResponse() == 0)
		{
			new VehicleCreateMainDialog(player, shoebill, rootEventManager, vehicleManager).show();
		}
		
		super.onDialogResponse(event);
	}
}
