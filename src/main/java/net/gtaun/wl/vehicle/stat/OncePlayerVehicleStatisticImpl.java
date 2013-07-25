package net.gtaun.wl.vehicle.stat;

import java.util.Date;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;

public class OncePlayerVehicleStatisticImpl extends AbstractPlayerVehicleProbe implements OncePlayerVehicleStatistic
{
	private int modelId;
	private double damageCount;
	private long driveSecondCount;
	private double driveOdometer;
	private float currentSpeed;
	private float maxSpeed;
	
	private Date startTime;
	private Date endTime;
	
	
	public OncePlayerVehicleStatisticImpl(Shoebill shoebill, EventManager rootEventManager, Player player)
	{
		super(shoebill, rootEventManager, player);
		modelId = player.getVehicle().getModelId();
		damageCount = 0.0;
		driveOdometer = 0.0;
		maxSpeed = 0.0f;
	}
	
	@Override
	protected void onVehicleUpdate(Vehicle vehicle)
	{
		currentSpeed = vehicle.getVelocity().speed3d() * 50;
		if (currentSpeed > maxSpeed) maxSpeed = currentSpeed;
	}

	@Override
	protected void onVehicleMove(Vehicle vehicle, float distance)
	{
		driveOdometer += distance;
	}

	@Override
	protected void onVehicleTick(Vehicle vehicle)
	{
		driveSecondCount++;
	}

	@Override
	protected void onVehicleDamage(Vehicle vehicle, float damage)
	{
		damageCount += damage;
	}
	
	@Override
	public boolean isActive()
	{
		return isDestroyed() == false;
	}
	
	public void start()
	{
		startTime = new Date();
		init();
	}
	
	public void end()
	{
		if (isActive() == false) return;

		endTime = new Date();
		super.destroy();
	}
	
	@Override
	public int getModelId()
	{
		return modelId;
	}
	
	@Override
	public double getDamageCount()
	{
		return damageCount;
	}
	
	@Override
	public long getDriveSecondCount()
	{
		return driveSecondCount;
	}
	
	@Override
	public double getDriveOdometer()
	{
		return driveOdometer;
	}
	
	@Override
	public float getCurrentSpeed()
	{
		return currentSpeed;
	}

	@Override
	public float getMaxSpeed()
	{
		return maxSpeed;
	}
	
	@Override
	public Date getStartTime()
	{
		return startTime;
	}
	
	@Override
	public Date getEndTime()
	{
		return endTime;
	}
}
