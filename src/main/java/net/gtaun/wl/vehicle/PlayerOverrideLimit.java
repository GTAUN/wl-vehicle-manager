package net.gtaun.wl.vehicle;

public interface PlayerOverrideLimit
{
	boolean isUnlimitedNOS(boolean previous);
	boolean isAutoRepair(boolean previous);
	boolean isAutoFlip(boolean previous);
	boolean isAutoCarryPassengers(boolean previous);
}
