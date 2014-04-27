/**
 * WL Vehicle Manager Plugin
 * Copyright (C) 2013 MK124
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.gtaun.wl.vehicle.textdraw;

import java.util.ArrayList;
import java.util.List;

import net.gtaun.shoebill.constant.TextDrawFont;
import net.gtaun.shoebill.data.Color;
import net.gtaun.shoebill.event.player.PlayerClickPlayerTextDrawEvent;
import net.gtaun.shoebill.object.Destroyable;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.PlayerTextdraw;
import net.gtaun.util.event.Attentions;
import net.gtaun.util.event.EventManager;
import net.gtaun.util.event.EventManagerNode;
import net.gtaun.util.event.HandlerPriority;
import net.gtaun.wl.common.textdraw.TextDrawUtils;
import net.gtaun.wl.vehicle.VehicleManagerService;

public class VehicleCreateListTextDraw implements Destroyable
{
	public interface ClickCallback
	{
		void onClick(int modelId);
	}
	
	
	protected final Player player;
	protected final EventManager rootEventManager;
	protected final EventManagerNode eventManagerNode;
	protected final VehicleManagerService vehicleManager;
	protected final Integer[] modelIds;
	protected final ClickCallback clickCallback;
	
	private List<PlayerTextdraw> items;
	
	
	public VehicleCreateListTextDraw
	(final Player player, final EventManager rootEventManager, final VehicleManagerService vehicleManager, Integer[] modelIds, ClickCallback callback)
	{
		this.player = player;
		this.rootEventManager = rootEventManager;
		this.eventManagerNode = rootEventManager.createChildNode();
		this.vehicleManager = vehicleManager;
		this.modelIds = modelIds;
		this.clickCallback = callback;
		
		int baseX = 2 + (10 - modelIds.length) * 64 / 2;
		
		items = new ArrayList<>(modelIds.length);
		for (int i=0; i<modelIds.length; i++)
		{
			PlayerTextdraw textdraw = TextDrawUtils.createPlayerText(player, baseX+64*i, 400, "_");
			textdraw.setFont(TextDrawFont.MODEL_PREVIEW);
			textdraw.setPreviewModel(modelIds[i]);
			textdraw.setPreviewModelRotation(-10.0f, 0.0f, -20.0f, 0.8f);
			textdraw.setSelectable(true);
			textdraw.setUseBox(true);
			textdraw.setBoxColor(new Color(100, 120, 140, 0));
			textdraw.setBackgroundColor(new Color(100, 120, 140, 0));
			textdraw.setTextSize(60, 60);
			items.add(textdraw);
		}
		
		this.eventManagerNode.registerHandler(PlayerClickPlayerTextDrawEvent.class, HandlerPriority.NORMAL, Attentions.create().object(player), (e) ->
		{
			PlayerTextdraw textdraw = e.getPlayerTextdraw();
			if (textdraw != null)
			{
				int index = items.indexOf(textdraw);
				if (index != -1)
				{
					e.setProcessed();
					clickCallback.onClick(modelIds[index]);
				}
				
				destroy();
			}
		});
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

		eventManagerNode.cancelAll();
		player.cancelSelectTextDraw();
		
		for (PlayerTextdraw textdraw : items) textdraw.destroy();
		items = null;
	}
	
	public void show()
	{
		for (PlayerTextdraw textdraw : items) textdraw.show();
		
		player.selectTextDraw(new Color(255, 255, 255, 128));
	}
}
