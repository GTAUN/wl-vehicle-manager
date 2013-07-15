package net.gtaun.wl.vehicle.dialog;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractListDialog;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.event.dialog.DialogResponseEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.vehicle.VehicleManagerService;

public class VehicleComponentPaintjobDialog extends AbstractListDialog
{
	private final Vehicle vehicle;
	private final VehicleManagerService vehicleManager;
	
	
	public VehicleComponentPaintjobDialog
	(final Player player, final Shoebill shoebill, final EventManager eventManager, final Vehicle vehicle, final VehicleManagerService vehicleManager)
	{
		super(player, shoebill, eventManager);
		this.vehicle = vehicle;
		this.vehicleManager = vehicleManager;
		
		// final int vehcileModelId = vehicle.getModelId();
		
		for (int i=0; i<3; i++)
		{
			final int paintjobId = i;
			final String item = String.format("%1$s: %2$s %3$d", "喷漆", "喷漆", paintjobId);
			
			dialogListItems.add(new DialogListItem(item)
			{
				@Override
				public void onItemSelect()
				{
					vehicle.setPaintjob(paintjobId);
					new VehicleComponentDialog(player, shoebill, rootEventManager, vehicle, vehicleManager).show();
					destroy();
				}
			});
		}
	}
	
	@Override
	public void show()
	{
		int modelId = vehicle.getModelId();
		String name = VehicleModel.getName(modelId);
		
		if (player.getVehicle() != vehicle)
		{
			Location loc = vehicle.getLocation();
			player.setCameraLookAt(loc);
			loc.setZ(loc.getZ() + 10.0f);
			player.setCameraPosition(loc);
		}
		
		setCaption(String.format("改装 %1$s - 选择%2$s部件", name, "喷漆"));
		super.show();
	}
	
	@Override
	protected void onDialogResponse(DialogResponseEvent event)
	{
		if (event.getDialogResponse() == 0)
		{
			new VehicleComponentDialog(player, shoebill, rootEventManager, vehicle, vehicleManager).show();
		}
		
		super.onDialogResponse(event);
	}
	
	@Override
	protected void destroy()
	{
		if (player.getVehicle() != vehicle) player.setCameraBehind();
		super.destroy();
	}
}
