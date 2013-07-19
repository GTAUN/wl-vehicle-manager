package net.gtaun.wl.vehicle.stat;

import java.util.Date;

import com.google.code.morphia.annotations.Id;

public class PlayerVehicleStatistic
{
	@Id private String playerUniqueId;		// 玩家唯一ID
	private int modelId;					// 车辆模型ID
	
	private int spawnCount;					// 刷车计数器
	private double damageCount;				// 总伤害值计数器
	
	private int driveCount;					// 驾驶次数
	private long driveTimeCount;			// 总驾驶时间，单位秒
	private double driveOdometer;			// 总驾驶距离，单位米
	
	private Date lastUpdate;				// 上次更新时间
	
	
	public PlayerVehicleStatistic()
	{

	}
	
	public String getPlayerUniqueId()
	{
		return playerUniqueId;
	}
	
	public int getModelId()
	{
		return modelId;
	}
	
	public int getSpawnCount()
	{
		return spawnCount;
	}
	
	public double getDamageCount()
	{
		return damageCount;
	}
	
	public int getDriveCount()
	{
		return driveCount;
	}
	
	public long getDriveTimeCount()
	{
		return driveTimeCount;
	}
	
	public double getDriveOdometer()
	{
		return driveOdometer;
	}
	
	public Date getLastUpdate()
	{
		return lastUpdate;
	}
}
