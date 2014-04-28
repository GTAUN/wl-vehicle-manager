package net.gtaun.wl.vehicle.util;

import java.util.HashMap;
import java.util.Map;

import net.gtaun.shoebill.constant.VehicleComponentSlot;
import net.gtaun.shoebill.constant.VehicleModel.VehicleType;
import net.gtaun.shoebill.object.Player;
import net.gtaun.wl.lang.LocalizedStringSet;
import net.gtaun.wl.lang.LocalizedStringSet.PlayerStringSet;

public final class VehicleTextUtils
{
	private static final Map<VehicleComponentSlot, String> COMPONENT_SLOT_NAME_KEYS = new HashMap<>();
	static
	{
		COMPONENT_SLOT_NAME_KEYS.put(null,								"Component.Slot.Unknown");
		COMPONENT_SLOT_NAME_KEYS.put(VehicleComponentSlot.SPOILER,		"Component.Slot.Spoiler");
		COMPONENT_SLOT_NAME_KEYS.put(VehicleComponentSlot.SPOILER,		"Component.Slot.Spoiler");
		COMPONENT_SLOT_NAME_KEYS.put(VehicleComponentSlot.HOOD,			"Component.Slot.Hood");
		COMPONENT_SLOT_NAME_KEYS.put(VehicleComponentSlot.ROOF,			"Component.Slot.Roof");
		COMPONENT_SLOT_NAME_KEYS.put(VehicleComponentSlot.SIDE_SKIRT,	"Component.Slot.SideSkirt");
		COMPONENT_SLOT_NAME_KEYS.put(VehicleComponentSlot.LAMPS,		"Component.Slot.Lamps");
		COMPONENT_SLOT_NAME_KEYS.put(VehicleComponentSlot.NITRO,		"Component.Slot.Nitro");
		COMPONENT_SLOT_NAME_KEYS.put(VehicleComponentSlot.EXHAUST,		"Component.Slot.Exhaust");
		COMPONENT_SLOT_NAME_KEYS.put(VehicleComponentSlot.WHEELS,		"Component.Slot.Wheels");
		COMPONENT_SLOT_NAME_KEYS.put(VehicleComponentSlot.STEREO,		"Component.Slot.Stereo");
		COMPONENT_SLOT_NAME_KEYS.put(VehicleComponentSlot.HYDRAULICS,	"Component.Slot.Hydraulics");
		COMPONENT_SLOT_NAME_KEYS.put(VehicleComponentSlot.FRONT_BUMPER,	"Component.Slot.FrontBumper");
		COMPONENT_SLOT_NAME_KEYS.put(VehicleComponentSlot.REAR_BUMPER,	"Component.Slot.RearBumper");
		COMPONENT_SLOT_NAME_KEYS.put(VehicleComponentSlot.VENT_RIGHT,	"Component.Slot.VentRight");
		COMPONENT_SLOT_NAME_KEYS.put(VehicleComponentSlot.VENT_LEFT,	"Component.Slot.VentLeft");
	}
	
	private static final Map<VehicleType, String> VEHICLE_TYPE_NAME_KEYS = new HashMap<>();
	static
	{
		VEHICLE_TYPE_NAME_KEYS.put(null,						"Vehicle.Type.Unknown");
		VEHICLE_TYPE_NAME_KEYS.put(VehicleType.UNKNOWN, 		"Vehicle.Type.Unknown");
		VEHICLE_TYPE_NAME_KEYS.put(VehicleType.BICYCLE,			"Vehicle.Type.Bicycle");
		VEHICLE_TYPE_NAME_KEYS.put(VehicleType.MOTORBIKE,		"Vehicle.Type.Motorbike");
		VEHICLE_TYPE_NAME_KEYS.put(VehicleType.CAR,				"Vehicle.Type.Car");
		VEHICLE_TYPE_NAME_KEYS.put(VehicleType.TRAILER,			"Vehicle.Type.Trailer");
		VEHICLE_TYPE_NAME_KEYS.put(VehicleType.REMOTE_CONTROL,	"Vehicle.Type.RemoteControl");
		VEHICLE_TYPE_NAME_KEYS.put(VehicleType.TRAIN,			"Vehicle.Type.Train");
		VEHICLE_TYPE_NAME_KEYS.put(VehicleType.BOAT,			"Vehicle.Type.Boat");
		VEHICLE_TYPE_NAME_KEYS.put(VehicleType.AIRCRAFT,		"Vehicle.Type.Aircraft");
		VEHICLE_TYPE_NAME_KEYS.put(VehicleType.HELICOPTER,		"Vehicle.Type.Helicopter");
		VEHICLE_TYPE_NAME_KEYS.put(VehicleType.TANK, 			"Vehicle.Type.Tank");
	}
	

	public static String getComponentSlotName(LocalizedStringSet stringSet, Player player, VehicleComponentSlot slot)
	{
		String key = COMPONENT_SLOT_NAME_KEYS.get(slot);
		return stringSet.get(player, key);
	}
	
	public static String getComponentSlotName(PlayerStringSet stringSet, VehicleComponentSlot slot)
	{
		String key = COMPONENT_SLOT_NAME_KEYS.get(slot);
		return stringSet.get(key);
	}
	
	public static String getVehicleTypeName(LocalizedStringSet stringSet, Player player, VehicleType type)
	{
		String key = VEHICLE_TYPE_NAME_KEYS.get(type);
		return stringSet.get(player, key);
	}
	
	public static String getVehicleTypeName(PlayerStringSet stringSet, VehicleType type)
	{
		String key = VEHICLE_TYPE_NAME_KEYS.get(type);
		return stringSet.get(key);
	}
	
	
	private VehicleTextUtils()
	{
		
	}
}
