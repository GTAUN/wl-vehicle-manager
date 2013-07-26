package net.gtaun.wl.vehicle.dialog;

import net.gtaun.shoebill.Shoebill;
import net.gtaun.shoebill.common.dialog.AbstractDialog;
import net.gtaun.shoebill.common.dialog.AbstractMsgboxDialog;
import net.gtaun.shoebill.event.dialog.DialogResponseEvent;
import net.gtaun.shoebill.object.Player;
import net.gtaun.util.event.EventManager;

public class MsgboxDialog extends AbstractMsgboxDialog
{
	private final String message;
	
	
	public MsgboxDialog(Player player, Shoebill shoebill, EventManager rootEventManager, String caption, String message)
	{
		this(player, shoebill, rootEventManager, null, caption, message);
	}
	
	public MsgboxDialog(Player player, Shoebill shoebill, EventManager rootEventManager, AbstractDialog parentDialog, String caption, String message)
	{
		super(player, shoebill, rootEventManager, parentDialog);
		this.message = message;
		
		setCaption(caption);
	}
	
	@Override
	public void show()
	{
		show(message);
	}
	
	@Override
	protected void onDialogResponse(DialogResponseEvent event)
	{
		if (event.getDialogResponse() == 0)
		{
			player.playSound(1084, player.getLocation());
			showParentDialog();
		}
		
		super.onDialogResponse(event);
	}
}
