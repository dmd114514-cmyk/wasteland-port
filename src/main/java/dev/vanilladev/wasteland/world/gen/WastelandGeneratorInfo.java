package dev.vanilladev.wasteland.world.gen;

import dev.vanilladev.wasteland.ModConfig;

public class WastelandGeneratorInfo
{
	// option ids
	public static final int OPT_CITY = 0;
	public static final int OPT_CITY_WEIGHT = 1;
	public static final int OPT_BUNKER = 2;
	public static final int OPT_TREE = 3;
	public static final int OPT_FIRE = 4;
	public static final int OPT_DAY_ZOMBIES = 5;
	public static final int OPT_COUNT = 6;

	private static int[] values;
	private static final String[] KEYS = { "option.city", "option.cityWeight",
			"option.bunker", "option.tree", "option.fire", "option.dayZombies" };

	private static void ensureLoaded()
	{
		if (values == null)
			createDefault();
	}

	// load current config values into the editable list
	public static void createDefault()
	{
		values = new int[OPT_COUNT];
		values[OPT_CITY] = ModConfig.spawnCities ? 1 : 0;
		values[OPT_CITY_WEIGHT] = ModConfig.cityWeight;
		values[OPT_BUNKER] = ModConfig.spawnBunker ? 1 : 0;
		values[OPT_TREE] = ModConfig.wastelandTreeSpawnRate;
		values[OPT_FIRE] = ModConfig.randomFirePerChunk;
		values[OPT_DAY_ZOMBIES] = ModConfig.dayZombies ? 1 : 0;
	}

	// options are session-scoped config overrides; the world seed JSON stays untouched
	public void setComplete(String str)
	{
		createDefault();
	}

	public String getFinal()
	{
		applyToConfig();
		return "";
	}

	public static int getCount()
	{
		ensureLoaded();
		return OPT_COUNT;
	}

	public static int getValue(int id)
	{
		ensureLoaded();
		return (id >= 0 && id < OPT_COUNT) ? values[id] : 0;
	}

	public static void setValue(int id, int v)
	{
		ensureLoaded();
		if (id >= 0 && id < OPT_COUNT)
			values[id] = v;
	}

	public static String getOptionKey(int id)
	{
		ensureLoaded();
		return (id >= 0 && id < OPT_COUNT) ? KEYS[id] : KEYS[0];
	}

	private static void applyToConfig()
	{
		ensureLoaded();
		ModConfig.spawnCities = values[OPT_CITY] != 0;
		if (values[OPT_CITY_WEIGHT] >= 1)
			ModConfig.cityWeight = values[OPT_CITY_WEIGHT];
		ModConfig.spawnBunker = values[OPT_BUNKER] != 0;
		ModConfig.wastelandTreeSpawnRate = values[OPT_TREE];
		ModConfig.randomFirePerChunk = values[OPT_FIRE];
		ModConfig.dayZombies = values[OPT_DAY_ZOMBIES] != 0;
	}
}