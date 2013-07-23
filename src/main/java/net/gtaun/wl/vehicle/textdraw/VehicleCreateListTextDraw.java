package net.gtaun.wl.vehicle.textdraw;

import java.util.ArrayList;
import java.util.List;

import net.gtaun.shoebill.SampObjectFactory;
import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.constant.TextDrawFont;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.event.PlayerEventHandler;
import net.gtaun.shoebill.event.player.PlayerClickPlayerTextDrawEvent;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.PlayerTextdraw;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManager.HandlerPriority;
import net.gtaun.util.event.ManagedEventManager;
import net.gtaun.wl.vehicle.VehicleManagerService;

public class VehicleCreateListTextDraw implements Destroyable
{
	public interface ClickCallback
	{
		void onClick(int modelId);
	}
	
	
	protected final Player player;
	protected final Shoebill shoebill;
	protected final EventManager rootEventManager;
	protected final ManagedEventManager eventManager;
	protected final VehicleManagerService vehicleManager;
	protected final int[] modelIds;
	protected final ClickCallback clickCallback;
	
	private List<PlayerTextdraw> items;
	
	
	public VehicleCreateListTextDraw
	(final Player player, final Shoebill shoebill, final EventManager eventManager, final VehicleManagerService vehicleManager, int[] modelIds, ClickCallback callback)
	{
		this.player = player;
		this.shoebill = shoebill;
		this.rootEventManager = eventManager;
		this.eventManager = new ManagedEventManager(eventManager);
		this.vehicleManager = vehicleManager;
		this.modelIds = modelIds;
		this.clickCallback = callback;
		
		SampObjectFactory factory = shoebill.getSampObjectFactory();
		
		items = new ArrayList<>(modelIds.length);
		for (int i=0; i<modelIds.length; i++)
		{
			PlayerTextdraw textdraw = createPlayerText(factory, player, 2+64*i, 400, "_");
			textdraw.setFont(TextDrawFont.MODEL_PREVIEW);
			textdraw.setPreviewModel(modelIds[i]);
			textdraw.setSelectable(true);
			textdraw.setUseBox(true);
			textdraw.setBoxColor(new Color(100, 120, 140, 192));
			textdraw.setBackgroundColor(new Color(100, 120, 140, 192));
			textdraw.setTextSize(60, 60);
			items.add(textdraw);
		}
		
		eventManager.registerHandler(PlayerClickPlayerTextDrawEvent.class, player, playerEventHandler, HandlerPriority.NORMAL);
	}
	
	@Override
	protected void finalize() throws Throwable
	{
		destroy();
		super.finalize();
	}
	
	@Override
	public boolean isDestroyed()
	{
		return items == null;
	}
	
	@Override
	public void destroy()
	{
		if (isDestroyed()) return;

		eventManager.cancelAll();
		player.cancelSelectTextDraw();
		
		for (PlayerTextdraw textdraw : items) textdraw.destroy();
		
		items = null;
	}
	
	public void show()
	{
		for (PlayerTextdraw textdraw : items) textdraw.show();
		
		player.selectTextDraw(new Color(255, 255, 255, 128));
	}
	
	private PlayerEventHandler playerEventHandler = new PlayerEventHandler()
	{
		protected void onPlayerClickPlayerTextDraw(PlayerClickPlayerTextDrawEvent event)
		{
			PlayerTextdraw textdraw = event.getPlayerTextdraw();
			if (textdraw != null)
			{
				int index = items.indexOf(textdraw);
				if (index == -1) return;
				
				event.setProcessed();
				clickCallback.onClick(modelIds[index]);
			}
			
			destroy();
		}
	};
	
	public static PlayerTextdraw createPlayerText(SampObjectFactory f, Player player, float x, float y, String text)
	{
		PlayerTextdraw textdraw = f.createPlayerTextdraw(player, x, (y-50)/1.075f+50, text);
		return textdraw;
	}
	
//	public static PlayerTextdraw createPlayerTextBG(SampObjectFactory f, Player player, float x, float y, float w, float h)
//	{
//		int lines = Math.round((h - 5) / 5.0f);
//		PlayerTextdraw textdraw = f.createPlayerTextdraw(player, x+4, (y-50)/1.075f+50, StringUtils.repeat("~n~", lines));
//		textdraw.setUseBox(true);
//		textdraw.setLetterSize(1.0f, 0.5f);
//		textdraw.setTextSize(x+w-4, h-7);
//		return textdraw;
//	}
}
