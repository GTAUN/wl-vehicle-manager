package net.gtaun.wl.vehicle.dialog;

import java.util.Set;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.AbstractListDialog;
import net.gtaun.shoebill.constant.VehicleComponentModel;
import net.gtaun.shoebill.constant.VehicleComponentSlot;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.event.dialog.DialogResponseEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.vehicle.VehicleManagerService;

public class VehicleComponentAddDialog extends AbstractListDialog
{
	private final AbstractDialog parentDialog;
	private final Vehicle vehicle;
	private final VehicleComponentSlot componentSlot;
	
	
	public VehicleComponentAddDialog
	(final Player player, final Shoebill shoebill, final EventManager eventManager, final AbstractDialog parentDialog, final Vehicle vehicle, final VehicleManagerService vehicleManager, final VehicleComponentSlot slot)
	{
		super(player, shoebill, eventManager);
		this.parentDialog = parentDialog;
		this.vehicle = vehicle;
		this.componentSlot = slot;

		final int modelId = vehicle.getModelId();
		final String name = VehicleModel.getName(modelId);
		
		final Set<Integer> components = VehicleComponentModel.getSlotSupportedComponents(modelId, slot);
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
					player.playSound(1133, player.getLocation());
					player.sendMessage(Color.LIGHTBLUE, "%1$s: 您的车子 %2$s 已安装%3$s新组件: %4$s 。", "车管", name, slotName, componentName);
					
					vehicle.getComponent().add(cid);
					parentDialog.show();
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
		
		setCaption(String.format("%1$s: 改装 %2$s - 选择%3$s部件", "车管", name, VehicleComponentDialog.getVehicleComponentSlotNames().get(componentSlot)));
		super.show();
	}
	
	@Override
	protected void onDialogResponse(DialogResponseEvent event)
	{
		if (event.getDialogResponse() == 0)
		{
			player.playSound(1084, player.getLocation());
			parentDialog.show();
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
