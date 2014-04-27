package net.gtaun.wl.vehicle;

public interface PlayerOverrideLimit
{
	boolean isInfiniteNitrous(boolean previous);
	boolean isAutoRepair(boolean previous);
	boolean isAutoFlip(boolean previous);
	boolean isAutoCarryPassengers(boolean previous);
}
