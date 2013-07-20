package net.gtaun.wl.vehicle.stat;

import java.util.Date;

import net.gtaun.shoebill.object.Player;

public interface PlayerVehicleStatistic
{
	Player getPlayer();
	int getModelId();
	long getSpawnCount();
	double getDamageCount();
	long getDriveCount();
	long getDriveTimeCount();
	double getDriveOdometer();
	Date getLastUpdate();
}
