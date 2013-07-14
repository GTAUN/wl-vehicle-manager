package net.gtaun.wl.vehicle.dialog;

import java.util.Set;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractPageListDialog;
import net.gtaun.shoebill.constant.VehicleComponentModel;
import net.gtaun.shoebill.constant.VehicleComponentSlot;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.event.dialog.DialogResponseEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.vehicle.VehicleManagerService;

public class VehicleComponentAddDialog extends AbstractPageListDialog
{
	private final Vehicle vehicle;
	private final VehicleManagerService vehicleManager;
	private final VehicleComponentSlot componentSlot;
	
	
	public VehicleComponentAddDialog
	(final Player player, final Shoebill shoebill, final EventManager eventManager, final Vehicle vehicle, final VehicleManagerService vehicleManager, final VehicleComponentSlot slot)
	{
		super(player, shoebill, eventManager);
		this.vehicle = vehicle;
		this.vehicleManager = vehicleManager;
		this.componentSlot = slot;
		
		final int vehcileModelId = vehicle.getModelId();
		final Set<Integer> components = VehicleComponentModel.getSlotSupportedComponents(vehcileModelId, slot);
		final String slotName = VehicleComponentDialog.getVehicleComponentSlotNames().get(slot);
		
		for (final int cid : components)
		{
			final String componentName = VehicleComponentModel.getName(cid);
			final String item = String.format("%1$s: %2$s", slotName, componentName);
			
			dialogListItems.add(new DialogListItem(item)
			{
				@Override
				public void onItemSelect()
				{
					vehicle.getComponent().add(cid);
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
			loc.setZ(loc.getZ() + 15.0f);
			player.setCameraPosition(loc);
		}
		
		setCaption(String.format("改装 %1$s - 选择%2$s部件", name, VehicleComponentDialog.getVehicleComponentSlotNames().get(componentSlot)));
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
}
