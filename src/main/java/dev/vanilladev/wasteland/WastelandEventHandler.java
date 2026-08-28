package dev.vanilladev.wasteland;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import dev.vanilladev.wasteland.city.CityGenerator;
import dev.vanilladev.wasteland.ruin.RuinVillageGenerator;
import dev.vanilladev.wasteland.utils.Vector;
import dev.vanilladev.wasteland.world.WastelandWorldData;
import dev.vanilladev.wasteland.world.WorldChunkManagerWasteland;

public class WastelandEventHandler
{
	RuinVillageGenerator villageGeneratorHook;
	CityGenerator cityGeneratorHook;
	WastelandWorldData worldSaveData;
	boolean newSpawn;
	boolean bunkerScheduled;
	boolean bunkerDone;
	int spawnHeight;
	Vector spawnLoc;
	
	private boolean isWastelandWorld(World world)
	{
		return !world.isRemote && world.getBiomeProvider().getClass().getName().equals(WorldChunkManagerWasteland.class.getName());
	}
	
	@SubscribeEvent
	public void loadData(WorldEvent.Load event)
	{
		World world = event.getWorld();
		
		if (!world.isRemote && world.getBiomeProvider().getClass().getName().equals(WorldChunkManagerWasteland.class.getName()))
		{
			this.worldSaveData.setFile(world.getSaveHandler().getWorldDirectory() + "/data/WastelandMod.dat");
			Vector spawn = new Vector(world.getWorldInfo().getSpawnX(), world.getWorldInfo().getSpawnY() + 10, world.getWorldInfo().getSpawnZ());
			
			if (!this.worldSaveData.checkIfExists())
			{
				this.worldSaveData.createFile();
				this.villageGeneratorHook.resetData();
				this.cityGeneratorHook.resetData();
				newSpawn = true;
			}
			else
			{
				List<Vector> villageLocation = this.worldSaveData.loadVillageData();
				List<Vector> cityLocation = this.worldSaveData.loadCityData();
				this.villageGeneratorHook.loadData(villageLocation, villageLocation.size());
				this.cityGeneratorHook.loadData(cityLocation, cityLocation.size());
				this.spawnLoc = this.worldSaveData.loadSpawnLoc();
				if (this.spawnLoc != null)
					world.setSpawnPoint(new BlockPos(this.spawnLoc.X, this.spawnLoc.Y, this.spawnLoc.Z));
				else if (ModConfig.spawnBunker)
					this.bunkerScheduled = true; // old save without a bunker -> rebuild one
			}
			
			// defer to the first world tick: at WorldEvent.Load the spawn area is
			// not generated yet, getMinWorldHeight() returns 0 there and the bunker
			// would be built high in the air (player then spawns on the surface)
			if (newSpawn && ModConfig.spawnBunker)
				this.bunkerScheduled = true;
		}
	}
	
	@SubscribeEvent
	public void worldTick(TickEvent.WorldTickEvent event)
	{
		World world = event.world;
		if (world.isRemote || !this.bunkerScheduled || this.bunkerDone)
			return;
		if (!isWastelandWorld(world))
			return;
		Vector spawn = new Vector(world.getWorldInfo().getSpawnX(), world.getWorldInfo().getSpawnY() + 10, world.getWorldInfo().getSpawnZ());
		int min = getMinWorldHeight(spawn, 3, world);
		if (min <= 0)
			return; // spawn area still generating; retry next tick
		this.spawnHeight = min - 7;
		spawn.Y = this.spawnHeight;
		RuinVillageGenerator.spawnBunker(spawn, world);
		this.spawnLoc = new Vector(spawn.X, spawn.Y + 1, spawn.Z);
		this.worldSaveData.saveSpawnLoc(this.spawnLoc);
		world.setSpawnPoint(new BlockPos(this.spawnLoc.X, this.spawnLoc.Y, this.spawnLoc.Z));
		this.bunkerDone = true;
		this.bunkerScheduled = false;
		// pull in a player that joined before the bunker was ready
		for (EntityPlayer player : world.playerEntities)
		{
			if (isNewPlayer(player))
			{
				player.setPosition(this.spawnLoc.X, this.spawnLoc.Y, this.spawnLoc.Z);
				BlockPos bedPos = new BlockPos(this.spawnLoc.X - 2, this.spawnLoc.Y, this.spawnLoc.Z + 1);
				player.setSpawnChunk(bedPos, false, 0);
				this.worldSaveData.savePlayerName(player.getName());
			}
		}
	}
	
	private int getMinWorldHeight(Vector spawn, int rad, World world)
	{
		int min = RuinVillageGenerator.getWorldHeight(world, spawn.X, spawn.Z);
		min = (min == 0) ? 100 : min;
		int height;
		for (int j = 0; j < (2 * rad) + 1; j++)
		{
			for (int i = 0; i < (2 * rad) + 1; i++)
			{
				height = RuinVillageGenerator.getWorldHeight(world, spawn.X - rad + i, spawn.Z - rad + i);
				if (height != 0)
				{
					min = (height < min) ? height : min;
				}
			}
		}
		return min;
	}
	
	@SubscribeEvent
	public void saveData(WorldEvent.Save event)
	{
		if (!event.getWorld().isRemote && isWastelandWorld(event.getWorld()))
		{
			this.villageGeneratorHook.saveData(this.worldSaveData);
			this.cityGeneratorHook.saveData(this.worldSaveData);
			this.newSpawn = false;
		}
	}
	
	@SubscribeEvent
	public void changeStartSpawn(EntityJoinWorldEvent event)
	{
		if (ModConfig.spawnBunker && (event.getWorld().getBiomeProvider().getClass().getName().equals(WorldChunkManagerWasteland.class.getName())))
		{
			if (event.getEntity() instanceof EntityPlayer)
			{
				Vector pos = new Vector((int) event.getEntity().posX, (int) event.getEntity().posY, (int) event.getEntity().posZ);
				
				EntityPlayer player = (EntityPlayer) event.getEntity();
				if (this.spawnLoc == null) return;
				if (isNewPlayer(player) && Vector.VtoVlengthXZ(pos, this.spawnLoc) < 16)
				{
					player.setPosition(this.spawnLoc.X, this.spawnLoc.Y, this.spawnLoc.Z);
					BlockPos spawnPos = new BlockPos(this.spawnLoc.X - 2, this.spawnLoc.Y, this.spawnLoc.Z + 1);
					player.setSpawnChunk(spawnPos, false, 0);
					this.worldSaveData.savePlayerName(player.getName());
				}
			}
		}
	}
	
	@SubscribeEvent
	public void disableSleep(PlayerInteractEvent event)
	{
		if (event instanceof PlayerInteractEvent.RightClickBlock && ModConfig.disableSleep)
		{
			Block block = event.getWorld().getBlockState(event.getPos()).getBlock();
			if (block instanceof BlockBed)
			{
				if (!event.getWorld().isRemote)
				{
					BlockPos spawnPos = event.getPos();
					event.getEntityPlayer().setSpawnChunk(spawnPos, false, 0);
					event.getEntityPlayer().sendMessage(new TextComponentString("Spawn point set..."));
					event.setCanceled(true);
				}
			}
		}
	}
	
	private boolean isNewPlayer(EntityPlayer player)
	{
		List<String> loadedPlayers = this.worldSaveData.getPlayerNames();
		if (loadedPlayers != null)
		{
			return !loadedPlayers.contains(player.getName());
		}
		else
		{
			return true;
		}
	}
	
	public void initialize(RuinVillageGenerator villageGen, CityGenerator cityGen, WastelandWorldData data)
	{
		this.villageGeneratorHook = villageGen;
		this.cityGeneratorHook = cityGen;
		this.worldSaveData = data;
		this.newSpawn = false;
		this.bunkerScheduled = false;
		this.bunkerDone = false;
	}
}