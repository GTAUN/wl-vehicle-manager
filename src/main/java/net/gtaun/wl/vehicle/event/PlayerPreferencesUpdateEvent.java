package net.gtaun.wl.vehicle.event;

import net.gtaun.util.event.Event;
import net.gtaun.wl.vehicle.PlayerPreferencesBase;

public class PlayerPreferencesUpdateEvent extends Event
{
	private final PlayerPreferencesBase playerPreferences;
	
	
	public PlayerPreferencesUpdateEvent(PlayerPreferencesBase playerPreferences)
	{
		this.playerPreferences = playerPreferences;
	}
	
	public PlayerPreferencesBase getPlayerPreferences()
	{
		return playerPreferences;
	}
}
