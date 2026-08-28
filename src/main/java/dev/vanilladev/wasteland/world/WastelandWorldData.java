package dev.vanilladev.wasteland.world;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import dev.vanilladev.wasteland.utils.Vector;

public class WastelandWorldData 
{
	private File file;

	public WastelandWorldData(String filename)
	{
		this.file = new File(filename);
	}
	
	public WastelandWorldData()
	{
		this.file = null;
	}
	
	public void setFile(String filename)
	{
		this.file = new File(filename);
	}
	
	public boolean checkIfExists()
	{
		return this.file.exists();
	}
	
	public void createFile()
	{
		NBTTagCompound global = new NBTTagCompound();
		NBTTagCompound villages = new NBTTagCompound();
		villages.setInteger("Total", 0);
		NBTTagCompound cities = new NBTTagCompound();
		cities.setInteger("Total", 0);
		global.setTag("Villages", villages);
		global.setTag("Cities", cities);
		global.setTag("Players", new NBTTagCompound());
		
		try 
		{
			this.file.getParentFile().mkdirs();
			this.file.createNewFile();
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(this.file));
			CompressedStreamTools.write(global, dos);
			dos.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	private NBTTagCompound readFile()
	{
		try
		{
			DataInputStream dis = new DataInputStream(new FileInputStream(this.file));
			NBTTagCompound result = CompressedStreamTools.read(dis, NBTSizeTracker.INFINITE);
			dis.close();
			return result;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	private void writeFile(NBTTagCompound tag)
	{
		try
		{
			this.file.getParentFile().mkdirs();
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(this.file));
			CompressedStreamTools.write(tag, dos);
			dos.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public List<Vector> loadVillageData()
	{
		List<Vector> villagePos = new ArrayList<Vector>();
		NBTTagCompound global = readFile();
		if (global != null && global.hasKey("Villages"))
		{
			NBTTagCompound villages = global.getCompoundTag("Villages");
			for (String key : villages.getKeySet())
			{
				if (!key.equals("Total"))
				{
					NBTTagCompound village = villages.getCompoundTag(key);
					villagePos.add(new Vector(village.getInteger("X"), village.getInteger("Y"), village.getInteger("Z")));
				}
			}
		}
		return villagePos;
	}
	
	public void saveVillageData(List<Vector> villagePosition)
	{
		NBTTagCompound global = readFile();
		if (global == null) global = new NBTTagCompound();
		NBTTagCompound villages = new NBTTagCompound();
		for (int i = 0; i < villagePosition.size(); i++)
		{
			NBTTagCompound location = new NBTTagCompound();
			location.setInteger("X", villagePosition.get(i).X);
			location.setInteger("Y", villagePosition.get(i).Y);
			location.setInteger("Z", villagePosition.get(i).Z);
			villages.setTag(String.valueOf(i+1), location);
		}
		villages.setInteger("Total", villagePosition.size());
		global.setTag("Villages", villages);
		writeFile(global);
	}
	
	public List<Vector> loadCityData()
	{
		List<Vector> cityPos = new ArrayList<Vector>();
		NBTTagCompound global = readFile();
		if (global != null && global.hasKey("Cities"))
		{
			NBTTagCompound cities = global.getCompoundTag("Cities");
			for (String key : cities.getKeySet())
			{
				if (!key.equals("Total"))
				{
					NBTTagCompound city = cities.getCompoundTag(key);
					cityPos.add(new Vector(city.getInteger("X"), city.getInteger("Y"), city.getInteger("Z")));
				}
			}
		}
		return cityPos;
	}
	
	public void saveCityData(List<Vector> cityPosition)
	{
		NBTTagCompound global = readFile();
		if (global == null) global = new NBTTagCompound();
		NBTTagCompound cities = new NBTTagCompound();
		for (int i = 0; i < cityPosition.size(); i++)
		{
			NBTTagCompound location = new NBTTagCompound();
			location.setInteger("X", cityPosition.get(i).X);
			location.setInteger("Y", cityPosition.get(i).Y);
			location.setInteger("Z", cityPosition.get(i).Z);
			cities.setTag(String.valueOf(i+1), location);
		}
		cities.setInteger("Total", cityPosition.size());
		global.setTag("Cities", cities);
		writeFile(global);
	}
	
	public List<String> getPlayerNames()
	{
		List<String> names  = new ArrayList<String>();
		NBTTagCompound global = readFile();
		if (global != null && global.hasKey("Players"))
		{
			NBTTagCompound players = global.getCompoundTag("Players");
			for (String playerName : players.getKeySet())
			{
				names.add(playerName);
			}
		}
		return (names.isEmpty()) ? null : names;
	}
	
	public void savePlayerNames(List<String> names)
	{
		NBTTagCompound global = readFile();
		if (global == null) global = new NBTTagCompound();
		NBTTagCompound players = new NBTTagCompound();
		for (int i = 0; i < names.size(); i++)
		{
			players.setString(names.get(i), names.get(i));
		}
		global.setTag("Players", players);
		writeFile(global);
	}
	
	public void savePlayerName(String name)
	{
		List<String> names = new ArrayList<String>();
		names.add(name);
		savePlayerNames(names);
	}

	public void saveSpawnLoc(Vector spawn) 
	{
		NBTTagCompound global = readFile();
		if (global == null) global = new NBTTagCompound();
		NBTTagCompound spawnTag = new NBTTagCompound();
		spawnTag.setInteger("spawnX", spawn.X);
		spawnTag.setInteger("spawnY", spawn.Y);
		spawnTag.setInteger("spawnZ", spawn.Z);
		global.setTag("Spawn", spawnTag);
		writeFile(global);
	}

	public Vector loadSpawnLoc()
	{
		Vector spawn = null;
		NBTTagCompound global = readFile();
		if (global != null && global.hasKey("Spawn"))
		{
			NBTTagCompound spawnLoc = global.getCompoundTag("Spawn");
			spawn = new Vector(spawnLoc.getInteger("spawnX"), spawnLoc.getInteger("spawnY"), spawnLoc.getInteger("spawnZ"));
		}
		return spawn;
	}

}