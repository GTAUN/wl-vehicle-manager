package net.gtaun.wl.vehicle.stat;

import java.util.Date;

import net.gtaun.shoebill.object.Player;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.Transient;

@Entity("PlayerVehicleStatistic")
public class PlayerVehicleStatistic
{
	@Transient private Player player;
	
	@Id private ObjectId objectId;
	
	@Indexed private String playerUniqueId;		// 玩家唯一ID
	@Indexed private int modelId;				// 车辆模型ID
	
	private int spawnCount;						// 刷车计数器
	private double damageCount;					// 总伤害值计数器
	
	private int driveCount;						// 驾驶次数
	private long driveTimeCount;				// 总驾驶时间，单位秒
	private double driveOdometer;				// 总驾驶距离，单位米
	
	private Date lastUpdate;					// 上次更新时间
	

	public PlayerVehicleStatistic(Player player, String playerUniqueId, int modelId)
	{
		this.player = player;
		this.playerUniqueId = playerUniqueId;
		this.modelId = modelId;
	}
	
	protected PlayerVehicleStatistic()
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
