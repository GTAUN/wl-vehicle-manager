package net.gtaun.wl.vehicle.stat;

import java.util.Date;
import java.util.List;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.PostLoad;

@Entity("GlobalVehicleStatistic")
public class VehicleStatistic
{
	@Id private int modelId;					// 车辆模型ID
	
	private int spawnCount;						// 刷车总计数器
	private int favoriteCount;					// 被收藏数
	private double damageCount;					// 总伤害值计数器
	
	private int driveCount;						// 驾驶次数
	private long driveTimeCount;				// 总驾驶时间，单位秒
	private double driveOdometer;				// 总驾驶距离，单位米
	
	private List<Integer> dailySpawnCount;		// 最近 n 日刷车数，计划 n=30
	private Date lastUpdate;					// 上次更新时间
	

	public VehicleStatistic()
	{
		
	}
	
	public VehicleStatistic(int modelId)
	{
		this.modelId = modelId;
	}
	
	@PostLoad
	void PostLoad()
	{
		
	}
	
	public int getModelId()
	{
		return modelId;
	}
	
	public int getSpawnCount()
	{
		return spawnCount;
	}
	
	public int getFavoriteCount()
	{
		return favoriteCount;
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
