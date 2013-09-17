package net.gtaun.wl.vehicle.util;

import net.gtaun.shoebill.constant.VehicleComponentSlot;
import net.gtaun.shoebill.constant.VehicleModel.VehicleType;
import net.gtaun.shoebill.object.Player;
import net.gtaun.wl.lang.LocalizedStringSet;

public final class VehicleTextUtils
{
	public static String getComponentSlotName(LocalizedStringSet stringSet, Player player, VehicleComponentSlot slot)
	{
		switch (slot)
		{
		case SPOILER:		return stringSet.get(player, "Component.Slot.Spoiler");
		case HOOD:			return stringSet.get(player, "Component.Slot.Hood");
		case ROOF:			return stringSet.get(player, "Component.Slot.Roof");
		case SIDE_SKIRT:	return stringSet.get(player, "Component.Slot.SideSkirt");
		case LAMPS:			return stringSet.get(player, "Component.Slot.Lamps");
		case NITRO:			return stringSet.get(player, "Component.Slot.Nitro");
		case EXHAUST:		return stringSet.get(player, "Component.Slot.Exhaust");
		case WHEELS:		return stringSet.get(player, "Component.Slot.Wheels");
		case STEREO:		return stringSet.get(player, "Component.Slot.Stereo");
		case HYDRAULICS:	return stringSet.get(player, "Component.Slot.Hydraulics");
		case FRONT_BUMPER:	return stringSet.get(player, "Component.Slot.FrontBumper");
		case REAR_BUMPER:	return stringSet.get(player, "Component.Slot.RearBumper");
		case VENT_RIGHT:	return stringSet.get(player, "Component.Slot.VentRight");
		case VENT_LEFT:		return stringSet.get(player, "Component.Slot.VentLeft");
		default:			return stringSet.get(player, "Component.Slot.Unknown");
		}
	}
	
	public static String getVehicleTypeName(LocalizedStringSet stringSet, Player player, VehicleType type)
	{
		switch (type)
		{
		case BICYCLE:			return stringSet.get(player, "Vehicle.Type.Bicycle");
		case MOTORBIKE:			return stringSet.get(player, "Vehicle.Type.Motorbike");
		case CAR:				return stringSet.get(player, "Vehicle.Type.Car");
		case TRAILER:			return stringSet.get(player, "Vehicle.Type.Trailer");
		case REMOTE_CONTROL:	return stringSet.get(player, "Vehicle.Type.RemoteControl");
		case TRAIN:				return stringSet.get(player, "Vehicle.Type.Train");
		case BOAT:				return stringSet.get(player, "Vehicle.Type.Boat");
		case AIRCRAFT:			return stringSet.get(player, "Vehicle.Type.Aircraft");
		case HELICOPTER:		return stringSet.get(player, "Vehicle.Type.Helicopter");
		case TANK:				return stringSet.get(player, "Vehicle.Type.Tank");
		default:				return stringSet.get(player, "Vehicle.Type.Unknown");
		}
	}
	
	private VehicleTextUtils()
	{
		
	}
}
