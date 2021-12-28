package imu.iWaystones.Other;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

import imu.iAPI.Other.Metods;
import imu.iWaystone.Interfaces.IModDataInv;
import imu.iWaystone.Interfaces.IModDataValues;

public class ConvUpgrade extends StringPrompt
{
	private String _question;
	private IModDataValues _value;
	private IModDataInv _inv;
	public ConvUpgrade(IModDataValues value, IModDataInv inv, String quetion) 
	{
		_value= value;
		_question = quetion;
		_inv = inv;
	}	
	
	@Override
	public Prompt acceptInput(ConversationContext con, String anwser) 
	{
		_inv.SetModData(_value, anwser);
		_inv.openThis();
		return null;
	}

	@Override
	public String getPromptText(ConversationContext arg0) {
		return Metods.msgC(_question);
	}

}
