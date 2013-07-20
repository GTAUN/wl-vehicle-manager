package net.gtaun.wl.vehicle.stat;

import java.util.Date;

public interface GlobalVehicleStatistic
{
	int getModelId();
	long getSpawnCount();
	long getFavoriteCount();
	double getDamageCount();
	long getDriveCount();
	long getDriveTimeCount();
	double getDriveOdometer();
	Date getLastUpdate();
}
