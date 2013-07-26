/**
 * Copyright (C) 2013 MK124
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package net.gtaun.wl.vehicle.textdraw;

import net.gtaun.shoebill.SampObjectFactory;
import net.gtaun.shoebill.object.Player;
import net.gtaun.shoebill.object.PlayerTextdraw;

public final class TextDrawUtils
{
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
	
	
	private TextDrawUtils()
	{
		
	}
}
