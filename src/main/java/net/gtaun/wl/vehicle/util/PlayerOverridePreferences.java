package net.gtaun.wl.vehicle.util;

import java.util.Deque;
import java.util.LinkedList;

import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.Attentions;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.HandlerPriority;
import net.gtaun.wl.vehicle.PlayerOverrideLimit;
import net.gtaun.wl.vehicle.PlayerPreferences;
import net.gtaun.wl.vehicle.PlayerPreferencesBase;
import net.gtaun.wl.vehicle.event.PlayerPreferencesUpdateEvent;

public class PlayerOverridePreferences implements PlayerPreferencesBase
{
	private final EventManager eventManager;
	private final PlayerPreferences preferences;
	private final Deque<PlayerOverrideLimit> limits;
	
	
	public PlayerOverridePreferences(PlayerPreferences preferences, EventManager eventManager)
	{
		this.eventManager = eventManager;
		this.preferences = preferences;
		this.limits = new LinkedList<>();
		
		eventManager.registerHandler(PlayerPreferencesUpdateEvent.class, HandlerPriority.NORMAL, Attentions.create().object(preferences), (e) -> dispatchUpdateEvent());
	}
	
	private void dispatchUpdateEvent()
	{
		PlayerPreferencesUpdateEvent event = new PlayerPreferencesUpdateEvent(this);
		eventManager.dispatchEvent(event, preferences.getPlayer(), this);
	}
	
	public void addLimit(PlayerOverrideLimit limit)
	{
		if (limits.contains(limit)) limits.remove(limit);
		limits.offerLast(limit);
		dispatchUpdateEvent();
	}
	
	public void removeLimit(PlayerOverrideLimit limit)
	{
		limits.remove(limit);
		dispatchUpdateEvent();
	}
	
	public boolean hasLimit(PlayerOverrideLimit limit)
	{
		return limits.contains(limit);
	}
	
	public void clearLimits()
	{
		limits.clear();
		dispatchUpdateEvent();
	}

	@Override
	public Player getPlayer()
	{
		return preferences.getPlayer();
	}

	@Override
	public boolean isInfiniteNitrous()
	{
		boolean enabled = preferences.isInfiniteNitrous();
		for (PlayerOverrideLimit limit : limits) enabled = limit.isInfiniteNitrous(enabled);
		return enabled;
	}

	@Override
	public boolean isAutoRepair()
	{
		boolean enabled = preferences.isAutoRepair();
		for (PlayerOverrideLimit limit : limits) enabled = limit.isAutoRepair(enabled);
		return enabled;
	}

	@Override
	public boolean isAutoFlip()
	{
		boolean enabled = preferences.isAutoFlip();
		for (PlayerOverrideLimit limit : limits) enabled = limit.isAutoFlip(enabled);
		return enabled;
	}

	@Override
	public boolean isAutoCarryPassengers()
	{
		boolean enabled = preferences.isAutoCarryPassengers();
		for (PlayerOverrideLimit limit : limits) enabled = limit.isAutoCarryPassengers(enabled);
		return enabled;
	}

	@Override
	public boolean isVehicleWidgetEnabled()
	{
		return preferences.isVehicleWidgetEnabled();
	}
}
