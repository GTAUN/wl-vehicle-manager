package net.gtaun.wl.vehicle.dialog;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractListDialog;
import net.gtaun.shoebill.constant.VehicleComponentModel;
import net.gtaun.shoebill.constant.VehicleComponentSlot;
import net.gtaun.shoebill.constant.VehicleModel;
import net.gtaun.shoebill.data.Location;
import net.gtaun.shoebill.event.dialog.DialogResponseEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;
import net.gtaun.wl.vehicle.VehicleManagerService;

public class VehicleComponentDialog extends AbstractListDialog
{
	private static Map<VehicleComponentSlot, String> VEHICLE_COMPONENT_SLOT_NAMES = new TreeMap<>();
	static
	{
		VEHICLE_COMPONENT_SLOT_NAMES.put(VehicleComponentSlot.SPOILER, "扰流板");
		VEHICLE_COMPONENT_SLOT_NAMES.put(VehicleComponentSlot.HOOD, "发动机罩");
		VEHICLE_COMPONENT_SLOT_NAMES.put(VehicleComponentSlot.ROOF, "车顶");
		VEHICLE_COMPONENT_SLOT_NAMES.put(VehicleComponentSlot.SIDE_SKIRT, "侧裙");
		VEHICLE_COMPONENT_SLOT_NAMES.put(VehicleComponentSlot.LAMPS, "车灯");
		VEHICLE_COMPONENT_SLOT_NAMES.put(VehicleComponentSlot.NITRO, "氮气加速装置");
		VEHICLE_COMPONENT_SLOT_NAMES.put(VehicleComponentSlot.EXHAUST, "排气管");
		VEHICLE_COMPONENT_SLOT_NAMES.put(VehicleComponentSlot.WHEELS, "车轮轮圈");
		VEHICLE_COMPONENT_SLOT_NAMES.put(VehicleComponentSlot.STEREO, "立体声音响");
		VEHICLE_COMPONENT_SLOT_NAMES.put(VehicleComponentSlot.HYDRAULICS, "液压系统");
		VEHICLE_COMPONENT_SLOT_NAMES.put(VehicleComponentSlot.FRONT_BUMPER, "前保险杠");
		VEHICLE_COMPONENT_SLOT_NAMES.put(VehicleComponentSlot.REAR_BUMPER, "后保险杠");
		VEHICLE_COMPONENT_SLOT_NAMES.put(VehicleComponentSlot.VENT_RIGHT, "右进气口");
		VEHICLE_COMPONENT_SLOT_NAMES.put(VehicleComponentSlot.VENT_LEFT, "左进气口");
		
		VEHICLE_COMPONENT_SLOT_NAMES = Collections.unmodifiableMap(VEHICLE_COMPONENT_SLOT_NAMES);
	}
	
	public static Map<VehicleComponentSlot, String> getVehicleComponentSlotNames()
	{
		return VEHICLE_COMPONENT_SLOT_NAMES;
	}

	
	private final Vehicle vehicle;
	private final VehicleManagerService vehicleManager;
	
	
	public VehicleComponentDialog
	(final Player player, final Shoebill shoebill, final EventManager eventManager, final Vehicle vehicle, final VehicleManagerService vehicleManager)
	{
		super(player, shoebill, eventManager);
		this.vehicle = vehicle;
		this.vehicleManager = vehicleManager;
		
		final String paintjobItem = String.format("%1$s", "喷漆");
		displayedItems.add(new DialogListItem(paintjobItem)
		{
			@Override
			public void onItemSelect()
			{
				new VehicleComponentPaintjobDialog(player, shoebill, eventManager, vehicle, vehicleManager).show();
				destroy();
			}
		});
		
		final int vehcileModelId = vehicle.getModelId();
		for (final VehicleComponentSlot slot : VehicleComponentModel.getVehicleSupportedSlots(vehcileModelId))
		{
			final Set<Integer> components = VehicleComponentModel.getSlotSupportedComponents(vehcileModelId, slot);
			final String slotName = VEHICLE_COMPONENT_SLOT_NAMES.get(slot);
			final String curName = VehicleComponentModel.getName(vehicle.getComponent().get(slot));
			
			final String item = String.format("%1$s -	当前部件: %2$s (%3$d 个可用部件)", slotName, curName, components.size());
			dialogListItems.add(new DialogListItem(item)
			{
				@Override
				public void onItemSelect()
				{
					new VehicleComponentAddDialog(player, shoebill, eventManager, vehicle, vehicleManager, slot).show();
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
		
		setCaption(String.format("改装 %1$s - 模型：%2$d, HP：%3$1.0f％", name, modelId, vehicle.getHealth()/10));
		super.show();
	}
	
	@Override
	protected void onDialogResponse(DialogResponseEvent event)
	{
		if (event.getDialogResponse() == 0)
		{
			new VehicleDialog(player, shoebill, rootEventManager, vehicle, vehicleManager).show();
		}
		
		super.onDialogResponse(event);
	}
}
