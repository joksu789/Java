package imu.iAPI.Other;

import java.util.HashMap;

public class Cooldowns 
{

	HashMap<String, Long> cooldowns = new HashMap<String, Long>();
	
	public void addCooldownInSeconds(String cd_name, double seconds)
	{
		if(cooldowns.containsKey(cd_name.toLowerCase()))
			return;
		
		cooldowns.put(cd_name.toLowerCase(), System.currentTimeMillis()+(long)(seconds*1000));
	}
	
	public void setCooldownInSeconds(String cd_name, double seconds)
	{

		cooldowns.put(cd_name.toLowerCase(), System.currentTimeMillis()+(long)(seconds*1000));
	}
	
	public boolean isCooldownReady(String cd_name)
	{
		
		if(!cooldowns.containsKey(cd_name.toLowerCase()) || System.currentTimeMillis() > cooldowns.get(cd_name.toLowerCase()))
		{
			//cooldown is finnished
			cooldowns.remove(cd_name.toLowerCase());
			return true;
		}
		return false;
	}
	
	public int GetCdInSeconds(String cd_name)
	{
		int left = 0;
		if(cooldowns.containsKey(cd_name))
		{
			left = (int) ((cooldowns.get(cd_name.toLowerCase()) - System.currentTimeMillis())*0.001);
		}
		
		return left;
	}
	
	public void removeCooldown(String cd_name)
	{
		cooldowns.remove(cd_name.toLowerCase());
	}
	
	public String GetCdInReadableTime(String cd_name)
	{
		return Metods.FormatTime((long)(GetCdInSeconds(cd_name) * 1000));
	}
}
