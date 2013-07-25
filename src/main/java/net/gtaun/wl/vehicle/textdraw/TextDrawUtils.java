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
