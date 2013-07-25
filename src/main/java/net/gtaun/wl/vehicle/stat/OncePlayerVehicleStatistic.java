package net.gtaun.wl.vehicle.stat;

import java.util.Date;

import net.gtaun.shoebill.constant.PlayerState;
import net.gtaun.shoebill.object.Player;

public interface OncePlayerVehicleStatistic
{
	Player getPlayer();
	boolean isActive();
	PlayerState getType();
	
	int getModelId();
	double getDamageCount();
	long getDriveSecondCount();
	double getDriveOdometer();
	float getCurrentSpeed();
	float getMaxSpeed();
	
	Date getStartTime();
	Date getEndTime();
}
