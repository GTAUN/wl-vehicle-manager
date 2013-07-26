package net.gtaun.wl.vehicle.textdraw;

import net.gtaun.shoebill.SampObjectFactory;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.player.AbstractPlayerContext;
import net.gtaun.shoebill.constant.TextDrawAlign;
import net.gtaun.shoebill.constant.TextDrawFont;
import net.gtaun.shoebill.event.TimerEventHandler;
import net.gtaun.shoebill.event.timer.TimerTickEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.PlayerTextdraw;
import net.gtaun.shoebill.object.Timer;
import net.gtaun.shoebill.object.Vehicle;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManager.HandlerPriority;
import net.gtaun.wl.vehicle.VehicleManagerService;
import net.gtaun.wl.vehicle.stat.OncePlayerVehicleStatistic;

public class VehicleSpeedometerWidget extends AbstractPlayerContext
{
	private final VehicleManagerService vehicleManager;
	
	private Timer timer;

	private PlayerTextdraw speedDisplay;
	private PlayerTextdraw unitDisplay;
	private PlayerTextdraw otherInfo;
	
	
	public VehicleSpeedometerWidget(Shoebill shoebill, EventManager rootEventManager, Player player, VehicleManagerService vehicleManager)
	{
		super(shoebill, rootEventManager, player);
		this.vehicleManager = vehicleManager;
	}

	@Override
	protected void onInit()
	{
		SampObjectFactory factory = shoebill.getSampObjectFactory();
		
		speedDisplay = TextDrawUtils.createPlayerText(factory, player, 625, 410, "0");
		speedDisplay.setAlignment(TextDrawAlign.RIGHT);
		speedDisplay.setFont(TextDrawFont.PRICEDOWN);
		speedDisplay.setLetterSize(1.2f, 3.75f);
		speedDisplay.setShadowSize(2);
		speedDisplay.show();
		
		unitDisplay = TextDrawUtils.createPlayerText(factory, player, 635, 445, "KPH");
		unitDisplay.setAlignment(TextDrawAlign.RIGHT);
		unitDisplay.setFont(TextDrawFont.BANK_GOTHIC);
		unitDisplay.setLetterSize(0.3f, 1.2f);
		unitDisplay.setShadowSize(1);
		unitDisplay.show();
		
		otherInfo = TextDrawUtils.createPlayerText(factory, player, 638, 460, "-");
		otherInfo.setAlignment(TextDrawAlign.RIGHT);
		otherInfo.setFont(TextDrawFont.FONT2);
		otherInfo.setLetterSize(0.25f, 0.8f);
		otherInfo.setShadowSize(1);
		otherInfo.show();
		
		timer = factory.createTimer(100);
		eventManager.registerHandler(TimerTickEvent.class, timer, timerEventHandler, HandlerPriority.NORMAL);
		timer.start();

		addDestroyable(otherInfo);
		addDestroyable(unitDisplay);
		addDestroyable(speedDisplay);
		addDestroyable(timer);
		
		update();
	}

	@Override
	protected void onDestroy()
	{
		
	}
	
	private void update()
	{
		OncePlayerVehicleStatistic stat = vehicleManager.getPlayerCurrentOnceStatistic(player);
		if (stat == null) return;
		
		Vehicle vehicle = player.getVehicle();
		if (vehicle == null) return;
		
		final float spd = stat.getCurrentSpeed() * 60 * 60 / 1000.0f;
		final float avgSpd = (float) (stat.getDriveOdometer() / stat.getDriveSecondCount()) * 60 * 60 / 1000.0f;
		final float maxSpd = stat.getMaxSpeed() * 60 * 60 / 1000.0f;
		final float dmg = (float) (stat.getDamageCount() / 10.0f);
		final float metres = (float) (stat.getDriveOdometer() / 1000.0f);
		
		long seconds = stat.getDriveSecondCount() % 60;
		long minutes = (stat.getDriveSecondCount() / 60) % 60;
		long hours = stat.getDriveSecondCount() / 60 / 60;
		String formatedTime = String.format("%1$02d:%2$02d:%3$02d", hours, minutes, seconds);

		String autoRepair = vehicleManager.getPlayerPreferences(player).isAutoRepair() ? "~g~R" : "~w~-";
		String unlimitedNOS = vehicleManager.getPlayerPreferences(player).isUnlimitedNOS() ? "~r~N" : "~w~-";
		String autoFlip = vehicleManager.getPlayerPreferences(player).isAutoFlip() ? "~b~F" : "~w~-";
		String lockDoor = vehicle.getState().getDoors()!=0 ? "~y~D" : "~w~-";

		speedDisplay.setText(String.format("%1$1.0f", spd));
		
		if (timer.getCount() % 5 == 0)
		{
			final String format = "%1$s [%7$s%8$s%9$s%10$s~w~]~n~Dmg: ~p~~h~%2$1.0f%%~w~ - Odo: ~g~~h~%3$1.2fKM~w~ - Avg/Max: ~y~~h~%5$1.1f~w~/~r~~h~%6$1.1f~w~ KPH";
			String text = String.format(format, formatedTime, dmg, metres, spd, avgSpd, maxSpd, autoRepair, unlimitedNOS, autoFlip, lockDoor);
			otherInfo.setText(text);
		}
	}
	
	private TimerEventHandler timerEventHandler = new TimerEventHandler()
	{
		protected void onTimerTick(TimerTickEvent event)
		{
			update();
		}
	};
}

