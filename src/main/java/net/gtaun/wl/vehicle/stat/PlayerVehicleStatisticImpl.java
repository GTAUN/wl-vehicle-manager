package net.gtaun.wl.vehicle.stat;

import java.util.Date;

import net.gtaun.shoebill.object.Player;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.Transient;

@Entity("PlayerVehicleStatistic")
public class PlayerVehicleStatisticImpl implements PlayerVehicleStatistic
{
	@Transient private Player player;
	
	@Id private ObjectId objectId;
	
	@Indexed private String playerUniqueId;		// 玩家唯一ID
	@Indexed private int modelId;				// 车辆模型ID
	
	private long spawnCount;					// 刷车计数器
	private double damageCount;					// 总伤害值计数器
	
	private long driveCount;					// 驾驶次数
	private long driveTimeCount;				// 总驾驶时间，单位秒
	private double driveOdometer;				// 总驾驶距离，单位米
	
	private Date lastUpdate;					// 上次更新时间
	

	public PlayerVehicleStatisticImpl(Player player, String playerUniqueId, int modelId)
	{
		this.player = player;
		this.playerUniqueId = playerUniqueId;
		this.modelId = modelId;
	}
	
	protected PlayerVehicleStatisticImpl()
	{
		
	}
	
	public String getPlayerUniqueId()
	{
		return playerUniqueId;
	}
	
	public void setPlayer(Player player)
	{
		this.player = player;
	}

	@Override
	public Player getPlayer()
	{
		return player;
	}

	@Override
	public int getModelId()
	{
		return modelId;
	}

	@Override
	public long getSpawnCount()
	{
		return spawnCount;
	}

	@Override
	public double getDamageCount()
	{
		return damageCount;
	}

	@Override
	public long getDriveCount()
	{
		return driveCount;
	}

	@Override
	public long getDriveTimeCount()
	{
		return driveTimeCount;
	}

	@Override
	public double getDriveOdometer()
	{
		return driveOdometer;
	}

	@Override
	public Date getLastUpdate()
	{
		return lastUpdate;
	}
	
	public void onSpawn()
	{
		spawnCount++;
		lastUpdate = new Date();
	}

	public void onDrive()
	{
		driveCount++;
		lastUpdate = new Date();
	}
	
	public void onDamage(float val)
	{
		damageCount += val;
		lastUpdate = new Date();
	}
	
	public void onDriveTick()
	{
		driveTimeCount++;
		lastUpdate = new Date();
	}
	
	public void onDriveMove(float distance)
	{
		driveOdometer += distance;
		lastUpdate = new Date();
	}
}
