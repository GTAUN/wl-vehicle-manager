package net.gtaun.wl.vehicle.dialog;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractListDialog;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.data.Color;
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

		final int modelId = vehicle.getModelId();
		final String name = VehicleModel.getName(modelId);
		
		for (int i=0; i<3; i++)
		{
			final int paintjobId = i;
			final String item = String.format("%1$s: %2$s %3$d", "喷漆", "喷漆", paintjobId);
			
			dialogListItems.add(new DialogListItem(item)
			{
				@Override
				public void onItemSelect()
				{
					player.playSound(1134, player.getLocation());
					player.sendMessage(Color.BLUE, "%1$s: 您的车子 %2$s 已喷漆: %3$s %4$d 。", "车管", name, "喷漆", paintjobId);
					
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
		
		setCaption(String.format("%1$s: 改装 %2$s - 选择%3$s部件", "车管", name, "喷漆"));
		super.show();
	}
	
	@Override
	protected void onDialogResponse(DialogResponseEvent event)
	{
		if (event.getDialogResponse() == 0)
		{
			player.playSound(1084, player.getLocation());
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
