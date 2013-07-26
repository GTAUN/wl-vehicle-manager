package net.gtaun.wl.vehicle;

import net.gtaun.shoebill.constant.VehicleComponentModel;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.Transient;

@Entity("VehicleManagerPlayerPreferences")
public final class PlayerPreferencesImpl implements PlayerPreferences
{
	interface SpeedometerWidgetSwitchCallback
	{
		void onSwitch();
	}
	
	
	@Transient private Player player;
	@Transient private SpeedometerWidgetSwitchCallback speedometerWidgetSwitchCallback;
	
	@Id private ObjectId objectId;
	
	@Indexed private String playerUniqueId;		// 玩家唯一ID
	
	private boolean unlimitedNOS;
	private boolean autoRepair;
	private boolean autoFlip;
	private boolean autoCarryPassengers;
	private boolean speedometerWidgetEnabled;
	
	
	protected PlayerPreferencesImpl()
	{
		autoCarryPassengers = true;
		speedometerWidgetEnabled = true;
	}
	
	PlayerPreferencesImpl(Player player, String playerUniqueId)
	{
		this();
		this.player = player;
		this.playerUniqueId = playerUniqueId;
	}
	
	public void setPlayer(Player player)
	{
		this.player = player;
	}
	
	public void setSpeedometerWidgetSwitchCallback(SpeedometerWidgetSwitchCallback speedometerWidgetSwitchCallback)
	{
		this.speedometerWidgetSwitchCallback = speedometerWidgetSwitchCallback;
	}
	
	@Override
	public Player getPlayer()
	{
		return player;
	}

	@Override
	public boolean isUnlimitedNOS()
	{
		return unlimitedNOS;
	}

	@Override
	public void setUnlimitedNOS(boolean enabled)
	{
		this.unlimitedNOS = enabled;
		if (unlimitedNOS)
		{
			Vehicle vehicle = player.getVehicle();
			if (vehicle == null) return;
			
			if (VehicleComponentModel.isVehicleSupported(vehicle.getModelId(), VehicleComponentModel.NITRO_10_TIMES))
			{
				vehicle.getComponent().add(VehicleComponentModel.NITRO_10_TIMES);
			}
		}
	}

	@Override
	public boolean isAutoRepair()
	{
		return autoRepair;
	}

	@Override
	public void setAutoRepair(boolean enabled)
	{
		this.autoRepair = enabled;
		if (autoRepair)
		{
			Vehicle vehicle = player.getVehicle();
			if (vehicle == null) return;
			
			if (vehicle.getHealth() < 1000.0f) vehicle.repair();
		}
	}

	@Override
	public boolean isAutoFlip()
	{
		return autoFlip;
	}

	@Override
	public void setAutoFlip(boolean enabled)
	{
		this.autoFlip = enabled;
	}

	@Override
	public boolean isAutoCarryPassengers()
	{
		return autoCarryPassengers;
	}

	@Override
	public void setAutoCarryPassengers(boolean enabled)
	{
		this.autoCarryPassengers = enabled;
	}

	@Override
	public boolean isSpeedometerWidgetEnabled()
	{
		return speedometerWidgetEnabled;
	}
	
	@Override
	public void setSpeedometerWidgetEnabled(boolean enabled)
	{
		this.speedometerWidgetEnabled = enabled;
		speedometerWidgetSwitchCallback.onSwitch();
	}
}
