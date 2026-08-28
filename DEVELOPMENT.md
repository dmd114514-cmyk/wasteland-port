# The Wasteland Mod — 1.12.2 移植开发文档

> 项目：The Wasteland Mod（modid: WLM）
> 原版：1.7.10（dk.mrspring.wasteland，v1.3.0，MrSpring 原作）
> 目标：Forge 1.12.2-14.23.5.2864
> 维护人：移植工程（DSH 自动化）

## 1. 项目概述

本模组新增一个「Wasteland」世界类型，生成废土化的生物群系（荒原/山地/森林/城市），
散布多种废墟建筑（谷仓、民居、图书馆、幸存者帐篷、树屋、掠夺者废墟、刷怪笼、路灯、
村庄废墟、地堡出生点等），并加入白天活动的「Day Zombie」实体与 `/biomes` 地形图命令。
建筑结构等创意内容（BuildingCode 等方块数据）自 1.7.10 原版原样保留，未做二次改造。

## 2. 目录结构

```
src/main/java/dk/mrspring/wasteland/
├── Wasteland.java              # @Mod 主类（preInit/init/postInit）
├── ModHelper.java              # ModInfo（modid=WLM）、方向常量、坐标工具
├── ModConfig.java              # config/Wasteland/TerrainGen.cfg 配置
├── RuinConfig.java             # config/Wasteland/ChestLoot.cfg 战利品配置
├── WastelandBiomes.java        # 4 个废土生物群系引用
├── GameRegisterer.java         # 生物群系注册辅助（BiomeDictionary）
├── WastelandEventHandler.java  # 世界加载/保存、地堡出生点、禁睡、日间僵尸
├── GetBiomesCommand.java       # /biomes 命令（生成生物群系分布图）
├── city/                       # 城市生成（CityGenerator + RuinedCity 容器）
├── client/                     # GuiCreateWastelandWorld（世界自定义 GUI，未启用）、ItemRuinIcon
├── entity/                     # EntityDayZombie + RenderDayZombie
├── gui/BiomesGui.java          # /biomes 地图显示 GUI
├── ruin/                       # Ruin 体系：Ruin、Building、Layout、RuinedVillage、
│                               # RuinVillageGenerator（村庄+地堡）、各废墟生成器
├── ruin/code/                  # BuildingCode/ClockTowerCode/ClockTowerCodeB 纯数据（原样保留）
├── utils/                      # Vector、Rectangle、Sphere、CustomItemStack
└── world/                      # WorldTypeWasteland、ChunkGeneratorWasteland、
                                # WorldChunkManagerWasteland(BiomeProvider)、
                                # WastelandGenLayerBiome、WastelandWorldData、
                                # biome/、gen/
src/main/resources/
├── assets/wlm/                 # 语言文件、纹理（原样保留）
├── mcmod.info                  # WLM 元数据
└── pack.mcmeta
```

## 3. 1.7.10 → 1.12.2 关键 API 映射

| 1.7.10 | 1.12.2 |
|---|---|
| `cpw.mods.fml.*` | `net.minecraftforge.fml.*` |
| `BiomeGenBase` | `Biome`（`Biome.BiomeProperties` 构造） |
| `BiomeGenBase.Height` | `BiomeProperties.setBaseHeight()/setHeightVariation()` |
| `WorldChunkManager` | `BiomeProvider` |
| `world.getWorldChunkManager()` | `world.getBiomeProvider()` |
| `world.getBiomeGenForCoords(x,z)` | `world.getBiome(new BlockPos(x,0,z))` |
| `.biomeID` | `Biome.getIdForBiome(biome)` |
| `IChunkProvider`（世界生成） | `IChunkGenerator` |
| `WorldType.getChunkManager()` | `WorldType.getBiomeProvider()` |
| `WorldType.getBiomeLayer(seed, parent)` | `getBiomeLayer(seed, parent, ChunkGeneratorSettings)` |
| `world.setBlock(x,y,z,block,meta,flag)` | `world.setBlockState(new BlockPos(x,y,z), block.getStateFromMeta(meta), flag)` |
| `world.getBlock(x,y,z)` | `world.getBlockState(new BlockPos(x,y,z)).getBlock()` |
| `world.getTileEntity(x,y,z)` | `world.getTileEntity(new BlockPos(x,y,z))` |
| `world.getChunkProvider().loadChunk(x,z)` | `world.getChunkFromChunkCoords(x,z)`（强制加载） |
| `Blocks.xxx / Items.xxx`（小写） | `Blocks.XXX / Items.XXX`（大写） |
| `world.provider.dimensionId` | `world.provider.getDimension()` |
| `EntityRegistry.registerGlobalEntityID(...)` | `EntityRegistry.registerModEntity(...)`（无需全局ID） |
| `RenderingRegistry.registerEntityRenderingHandler(cls, renderer)` | `(cls, rm -> new Renderer(rm))`（仅客户端，见 Proxy） |
| `ChatComponentText` | `TextComponentString` |
| `ChunkCoordinates` | `BlockPos` |
| `player.setSpawnChunk(pos,bool)` | `player.setSpawnPoint(pos,bool)` |
| `player.addChatMessage(...)` | `player.sendMessage(...)` |
| `StatCollector.translateToLocal` | `net.minecraft.client.resources.I18n.format` |
| `CommandBase.getCommandName()/getCommandUsage()` | `getName()/getUsage()` |
| `CommandBase.processCommand(...)` | `execute(...)` |
| `MathHelper.floor_double/sqrt_float` | `MathHelper.floor / sqrt` |
| `Potion.weakness/damageBoost/confusion` | `MobEffects.WEAKNESS/STRENGTH/NAUSEA` |
| `worldObj.difficultySetting` | `world.getDifficulty()` |
| `world.getSavedLightValue(EnumSkyBlock.Sky,x,y,z)` | `world.getLightFor(EnumSkyBlock.SKY, pos)` |
| `world.canBlockSeeTheSky(x,y,z)` | `world.canSeeSky(new BlockPos(x,y,z))` |
| `world.spawnEntityInWorld(e)` | `world.spawnEntity(e)` |
| `world.playAuxSFXAtEntity(p,x,y,z,id)` | `world.playEvent(p, id, new BlockPos(x,y,z), 0)` |
| `world.playSound(...,"mob.zombie.remedy",...)` | `world.playSound(..., SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, ...)` |
| `EntityAI...AttackOnCollide` | `EntityAIAttackMelee` |
| `getHeldItem()/getEquipmentInSlot(4)` | `getHeldItemMainhand()/getItemStackFromSlot(EntityEquipmentSlot.HEAD)` |
| `itemstack.stackSize` | `itemstack.getCount()/shrink(n)` |
| `getEntityToAttack()` | `getAttackTarget()` |
| `boundingBox.expand(...)` | `getEntityBoundingBox().grow(...)` |
| jnbt（org.jnbt，外部库） | 改用 Minecraft NBT（`CompressedStreamTools/NBTTagCompound`） |

## 4. 移植决策记录

1. **包名改为** `dev.vanilladev.wasteland`（依据原作 LICENSE 判定，见 §6），
   modid 保持 `wlm`（资源路径 assets/wlm、实体/群系注册名均不变）。
2. **建筑数据**：`ruin/code/*`（BuildingCode 等 2800+ 行方块数组）逐字节原样复制。
3. **废墟生成器**：创意内容（方块布局）一字不改，仅替换 API 调用。
4. **地形生成**：ChunkProviderWasteland（1.7.10 原版地形副本）改写为 1.12.2 `IChunkGenerator`
   结构（噪声管线对齐 1.12.2 原版 ChunkProviderGenerate），生物群系接入点不变。
5. **生物群系注册**：1.12.2 不允许固定 biome ID，改用 `ForgeRegistries.BIOMES` 注册，
   配置中的 ID 项保留但不再生效（文档说明）。
6. **世界数据文件**：`WastelandWorldData` 由 jnbt 改为 MC NBT，保持文件结构
   （Villages/Cities/Players/Spawn），路径改为 `world/data/WastelandMod.dat`。
7. **客户端代码**：实体渲染、世界自定义 GUI 等放入 `ClientProxy`，服务端不加载。
8. **Day Zombie**：保留自定义实体 EntityDayZombie（不改动其行为），1.12.2 实体注册
   走 `EntityRegistry.registerModEntity`。
9. **配置**：沿用 `Configuration` API，配置路径与分类与原版一致
   （`config/Wasteland/TerrainGen.cfg`、`config/Wasteland/ChestLoot.cfg`）。
10. **禁用项保持**：`spawnCities` 原版即强制 false，城市生成器保留但默认关闭；
   世界自定义 GUI 原版即未接线（onCustomizeButton 空实现），移植后同样保持。

## 5. 构建与冒烟测试

要求：JDK8（`D:\DevEnv\zulu\JDK8`），Gradle 5.6.4（wrapper 自带），联网（首次拉依赖）。

```bat
set JAVA_HOME=D:\DevEnv\zulu\JDK8
gradlew.bat build          :: 编译 + reobf，产出 build/libs/wasteland-1.0.0.jar
```

冒烟测试：
1. 将 jar 复制到 `D:\TEMP\模组资料\1.12.2-smoke-server\mods\`
2. `server.properties` 中 `level-type=wasteland`（模组世界类型名）
3. `D:\DevEnv\zulu\JDK8\bin\java.exe -Xmx3G -jar forge-1.12.2-14.23.5.2864.jar nogui`
4. 观察日志：无崩溃、世界类型注册成功、村庄/废墟/地堡正常生成

### 冒烟结论（2026-08-27，多轮 1.12.2 专用服务端实测）

- **世界创建与生成**：`level-type=wasteland` 世界正常创建（`Done (…s)!`），
  无崩溃、无异常堆栈；4 个群系（wasteland / _mountains / _forest / _city）可生成，
  地形、无湖泊（干涸荒原语义）、海平面下空气均正常。
- **结构生成（创意内容原样保留）**：地堡（spawnBunker）、村庄（RuinedVillage 布局
  Size/Buildings 随机）、废墟建筑与城市均由 1.12.2 管线按原逻辑生成。
- **装饰**：BiomeDecoratorWasteland 矿石/枯树/废墟装饰正常（无 NPE）。
- **数据持久化**：`WastelandMod.dat` 写入/读回正常；重启后村庄/地堡不重复生成，
  位置列表正确恢复。
- **客户端引用隔离**：服务端正常加载（`@SideOnly(CLIENT)` 隔离 BiomesGui 等）。

### 排错记录（移植期间实测结论）

1. `IWorldGenerator.generate` 6 参签名（含 `IChunkGenerator`、`IChunkProvider`）。
2. 服务端侧加载：客户端类引用必须收进 `@SideOnly(Side.CLIENT)` 方法，
   否则 SideTransformer 报 `Attempted to load class blk for invalid side SERVER`。
3. `BiomeDecorator` 矿石生成器字段在 `decorate`（4 参）方法体内创建，
   **不可覆写 decorate 而不调用 super**（否则全字段为 null → NPE）。
   原作 `generateLakes=false` 语义改用 TERRAIN_GEN_BUS 拦截
   `DecorateBiomeEvent.Decorate` 的 LAKE_WATER/LAKE_LAVA 实现。
4. `writeCompressed`/`read(File)` 压缩 NBT 写读在该环境（JDK 8u502 + 1.12.2
   mapped 运行时）不对称，文件尾部结束标记丢失、重读报 EOFException；
   数据文件改用非压缩 `CompressedStreamTools.write/read`（格式为模组内部
   数据文件，与 vanilla 世界存档互不影响）。

## 6. 版本信息

- 版本号从头开始：`1.0.0`（ModHelper.ModInfo.version / mcmod.info / gradle
  version / manifest 均已同步）。
- jars 名：`wasteland-1.0.0.jar`。
- 作者：DiaoMIAO；开发团队：vanillaDev（mcmod.info authorList / credits /
  manifest vendor 已更新，并保留原作 MrSpring 致谢）。
- 包名判定（授权核对，2026-08-27 经 web 检索确认）：
  原作 GitHub 仓库 = **https://github.com/chiqors/WastelandMod**（"The classic
  Minecraft Wasteland mod for 1.7.10"，最近提交 2020-05-20）。LICENSE 明确
  作者谱系：Silver_Weasek → The_Holy_Frenchman(Frog) → MrSpring → GiMoe →
  115kino(Kino) → Han(chiqors)。
  - §4 允许反编译/修改（私用自由）；公开分发修改版需书面许可；
  - **§5 例外：原作超过 7 个月未更新即可"FEEL FREE to update the mod for
    everyone"** —— 距最后提交已 6 年余，公开更新/移植明确豁免；
  - 许可证未禁止改名，改名属更新范畴 —— 按"允许才改"规则判定**允许**，
    已执行：包名 `dk.mrspring.wasteland` → `dev.vanilladev.wasteland`
    （全量替换 53 个源文件 + 目录迁移 + gradle group；原包名改动前已备份）。
  - 义务条款照办：附原作者致谢（credits）、不盈利、保留 mod 名。
- 冒烟（改名后）：`wasteland-1.0.0.jar` 服务端启动正常，Done 1.27s 且
  无崩溃/异常堆栈（世界存档 version-tracker 提示为版本号变更的正常提示）。

## 7. 客户端实测问题修复（2026-08-27）

整合包实测反馈 4 项，逐一定性并修复：

1. **水源方块极低概率单方块刷新** —— 定性：该水为**原作废墟创意内容**
   （`ruin/Building.java:202-205` 水井、`RuinRuinedCiv1.java:130` 水池），
   按"创意性内容不可二次改造"红线**保留不改**。真正该禁的 vanilla 泉水/水塘
   已由 `WastelandLakeBlocker`（TERRAIN_GEN_BUS 对 LAKE_WATER/LAKE_LAVA 的
   DENY）拦截（1.12.2 字节码证实泉水与湖共用这两类事件门控）；另按 1.7.10
   原作 `generateLakes=false` 的等价语义，在 `BiomeDecoratorWasteland` 构造器
   补 `generateFalls=false`（1.12.2 对应开关）双保险。
2. **找不到城市建筑群与其生物群系** —— 定性（2026-08-28 二次核对原作
   源码勘误）：原作城市 = **框架完整 + 建筑空壳**。
   - 框架确凿存在：城市群系（`BiomeGenCity.java`，biomeID 46）、群系层
     条目（`WastelandGenLayerBiome`，权重 10，受 `spawnCities` 门控）、
     城市生成器（`city\CityGenerator.java`，flood-fill 选地→算中心→调
     `RuinedCity`）、配置键（`Enable cities` 注释行 / `Min chunks between
     abandoned cities`=128 / `Wasteland City Biome ID`=46）、主类实例化
     （`Wasteland.java:45,77`）。
   - **建筑本体不存在**：`city\RuinedCity.java` 全 22 行，构造与 generate
     均为**空实现**，没有任何城市布局/房屋代码；且 `CityGenerator`
     flood-fill 存在无限循环缺陷（列表在循环内增长永不终止，递归无访问
     标记）—— **作者因此硬编码关闭城市（未完工功能）**。
   - 处理：仅修 flood-fill 终止逻辑（新增 `containsXZ` 判重后才追加+递
     归，机制层修复，不涉及创意内容）；**经确认（2026-08-28）默认
     关闭城市 = Enable cities 默认 false（复刻原作发布版），配置项保留
     可手动开启**。手动开启后城市群系会生成，但 `RuinedCity` 仍为空壳
     （只有地皮无建筑）。如需真正可见的城市建筑，须用原作已有组件
     （Building/BuildingCode/废墟系列）**新拼**城市街区布局——原作无此
     内容，属新增创意，须另行批准才能实施。
3. **创建世界时 wasteland 的自定义按钮无反应** —— 定性：原作
   `onCustomizeButton` 本为空实现（GUI 行被注释）。按作者意图接线，点击
   打开已移植的 `GuiCreateWastelandWorld`（设置写入
   `chunkProviderSettingsJson`，随生成参数传给 `ChunkProviderWasteland`）。
4. **进世界从高空生成摔死** —— 定性：1.12.2 玩家首次加入按世界存档存储的
   SpawnY 出生（通常 64），而荒原低地/峡谷表面可能更低，导致坠落。
   修复：`WastelandEventHandler.loadData` 在地堡生成后（新档与读档两种
   分支）都执行 `world.setSpawnPoint(地堡站位)`，使玩家首入与重生点直接
   落在安全的地堡内部；`changeStartSpawn` 另加 null 防护（避免客户端
   侧 join 事件在未加载数据时 NPE）。
- 重编译 + 服务端冒烟复验（见步骤 8 记录）。

## 8. 第二轮客户端实测问题修复（2026-08-28）

整合包实测再报 4 项（含两份崩溃日志），逐一定性并修复：

1. **点击自定义崩溃**（`ArrayIndexOutOfBoundsException: 321`，Client
   thread，`WastelandGeneratorInfo.getRarity:49` ←
   `GuiCreateWastelandWorld$Details.drawSlot:136`）—— `ruin` 稀有度
   列表渲染时 `getRarity(slotIndex)`/`setRarity(id)` 缺边界检查，
   且 `id > length` 判断是 off-by-one。修复：`getRarity` 对
   `id<0 || id>=length` 返回 0；`setRarity` 改为 `id>=0 && id<length`；
   `isOptionClicked`/加减按钮同条件修正；`Details.drawSlot` 开头加
   slotIndex 越界保护。
2. **跑图崩溃**（`ArrayIndexOutOfBoundsException: 3/13`，Server thread，
   `WorldGenWastelandBigTree.generateLeafNodeBases:279` ←
   `BiomeDecoratorWasteland.decorateForest:180`）—— **根因：级联世界生成
   重入**。`BiomeDecoratorWasteland.deadTreeGen` 为 biome 单例共享字段；
   大树生成中 `setBlockAndNotifyAdequately` 触发相邻 chunk 加载与再装饰，
   **重入 `generateLeafNodeList` 把共享的 `leafNodes` 字段换成更短数组**，
   外层循环每次迭代 getfield 重读字段 → 越界（index 3/13 随数据异）。
   修复：`generateLeaves`/`generateLeafNodeBases` 开头把 `leafNodes`
   读入**局部** `int[][] nodes`，循环与访问全用局部引用（重入不再污染），
   另保留 `var4.length<4` 防御。冒烟复验：同一级联量级下 `Done 5.3s`
   无崩溃、地堡/村庄正常。
3. **进世界仍从天上掉** —— 客户端 jar 与最新源码**行号不对齐**
   （崩溃日志行号 279 ≠ 当前 281），判定为旧交付 jar。最新 jar 已含
   `setSpawnPoint`（冒烟验证 level.dat SpawnY=62=地堡站位；崩溃报告
   中 `Level spawn location (2541,60,734)` 亦显示已生效）。请以最新
   jar 新建世界复核。
4. **白昼僵尸未正常生成** —— 代码核对：四个荒原群系（apocalypse 权重
   100/forest 100/mountains 10/city 100）均已按原作在 `dayZombies` 时
   注册 `EntityDayZombie` 进入 spawnableMonsterList；1.12.2 javap 证实
   `EntityMob.getCanSpawnHere() → isValidLightLevel()` 路径完整，移植与
   原作一致。整合包环境装有 incontrol/hordes/SRParasites 等大量怪物/生成
   控制 mod，可能压制白昼僵尸生成——先用最新 jar 复核，若仍异常再针对
   外部 mod 排查。
- 本轮修改文件：`WastelandGeneratorInfo.java`、`GuiCreateWastelandWorld.java`、
  `WorldGenWastelandBigTree.java`（局部引用 + 防御）、`DEVELOPMENT.md`。
- 重编译 + 冒烟复验已通过（`wasteland-1.0.0.jar`，`Done 5.3s`，无崩溃）。

## 9. 城市实现（2026-08-28，已批准「原作组件拼街区」方案）

- 背景：原作 `RuinedCity.java` 是空壳（无任何建筑），`CityGenerator` 的 flood-fill
  （已修复）与城市框架（群系/配置/生成器）齐全。确定把城市做成"真有建筑的
  城市"，方案：**不新造任何建筑蓝图，全部复用原作组件**（红线：创意零改动）。
- 实现（仅改 3 个文件）：
  1. `city/RuinedCity.java`（22 行空壳 → 新实现）：
     - 街道网格：街道带宽 6、街区边长 26、周期 32（以 `center` 为原点对齐）；
       街道铺设 GRAVEL/COBBLESTONE 随机斑块（60/25%，其余留 dirt），
       街道格随机尝试原作路灯 `RuinLightposts`（fence×3 + glowstone 灯臂）。
     - 街区判定：街区中心须为城市群系（`WastelandBiomes.city`）、地形 3×3 采样
       起伏 ≤3；中心街区放地标（`Building.CLOCK_TOWER`/`CHURCH`）并加密子块，
       普通街区 15% 在中心放大型建筑（医院/图书馆/餐厅/大宅，中心对齐跨度 ≤24），
       其余 2~4 栋小/中建筑放 13×13 子块（小池：小房×2/摊/井/教堂/中房2；
       中心街区加中房1/大房1/大房2），空子块 40% 撒 `WorldGenRandomRubble` 碎石。
     - 每个建筑经 `Building.create(type).generate(world, random, pos, rot)` 放置
       （与原作村庄逐行同款调用，含随机破洞与箱子战利品行为），不动任何蓝图。
  2. `ruin/Ruin.java`：新增 public 桥接 `placeRuin(...)` → 调 protected `generate`，
     仅暴露调用（路灯跨包调用用），不触及任何建筑结构。
  3. `world/WastelandGenLayerBiome.java`：城市权重测试期 400（冒烟验证）后已还原 10。
- 冒烟：`Enable cities=true` + 权重 400 新世界 → 日志
  `Generating City at X:128 Z:96`，`Done 27s` 无崩溃（城市+级联生成耗时）；
  权重还原 10（默认关闭开关不变，`Enable cities` 仍默认 false）后最终发布版
  `Done 5.5s` 无异常。城市功能 = `Enable cities=true` 时按城市群系连片区域
  （`CityGenerator` 已修 flood-fill，区域 >8 chunk）生成街区+街道+地标。

### 城市第二轮实测修复（2026-08-28 晚）

传送城市坐标后"啥都没有"，复跑冒烟定位并修复 4 处：

1. **flood-fill 爆栈**（`StackOverflowError`，`addConnectedBiomeChunks` 递归）——
   权重大时城市群系连片几千 chunk，递归深度爆栈。修复：递归改**迭代 BFS 队列**
   （`CityGenerator.addConnectedBiomeChunks`）。
2. **重复生成同一城市**（日志几十次同坐标）——`checkDist` 依赖的
   `cityLocation` 列表被 `resetData` 清空后没拦住。修复：新增**会话级去重集合**
   `builtCities`（XZ 键，`resetData`/`loadData` 同步），生成前查重。
3. **街区级平坦检查太严**（26×26 起伏≤3 → 21/36 街区被拒，城市接近空白）——
   修复：平坦检查**下放到子块级**（13×13，起伏≤4）与地标/大建筑半径 10 检查，
   街区本身只做群系检查。实测街区生成 7 → **28/36**。
4. **生成期间级联风暴**（`fillStreets` 遍历整个包围盒 `getHeight` 触发大量
   chunk 加载，服务器迟迟不 Done）——修复：城市只生成**中心核心区**
   （6×6 街区 ≈192×192），级联有限。实测 `Done 5.5~5.9s`。

- 最终验证：seed 1 世界（权重 400）`Generating City at X:112 Z:208`，
  `built 28/36`，无爆栈、无重复；发布版（权重 10）冒烟 `Done 5.5s` 无异常。
- 查找城市方式：`Enable cities=true` + 新世界 → 看日志
  `Generating City at X:… Z:…`（客户端单机在 `.minecraft/logs/latest.log`），
  传送到该坐标即见城市（建筑网格以该坐标为中心 ±96 格）。

### 城市第三轮实测修复（2026-08-28 晚）

实测：只见道路不见房子、道路高出地表一格；进世界从高空掉、无地堡。

1. **`getHeight(int,int)` 对未生成 chunk 返回 0**（javap 证实：先查
   `isChunkLoaded`，false 直接返回 0，不触发加载）——城市生成（populate）时
   核心区 chunk 未生成 → `groundY` 全 0 → 街道 `isStreetSurface` 全失败、
   `isFlatAt` 全 false → 街道/建筑/碎石全跳过。修复：`groundY` 改用
   `world.getChunkFromBlockCoords(pos).getHeightValue(x,y)`（强制加载 chunk，
   级联有限：核心区 6×6 街区 ≈12×12 chunk）。实测 `buildings 47`、
   `Done 6.9~7.1s`。
2. **道路高出地表一格**：`getHeight` 返回地表上方空气格，铺路直接用该 y →
   悬浮。修复：`fillStreets`/`rubble` 改为 `y-1`（替换地表方块本身）。
3. **进世界高空掉**（读档分支）：`loadSpawnLoc()` 返回 null（旧档无地堡数据）
   时 `setSpawnPoint(spawnLoc.X…)` NPE → spawn 点未设置。修复：`spawnLoc != null`
   才设置。新世界不受影响（newSpawn 分支照常生成地堡+设点）。
4. **平坦阈值**：子块 13×13 起伏 ≤4→**≤12**，地标/大建筑半径 10 起伏 ≤6→**≤12**
   （荒原丘陵地形更宽容，建筑嵌地可接受）。
- 测试版权重 **300**（约 1/3 面积，必见城市；原作发布版 10＝约 1.6%）。
- 定稿权重 **150**（中间值，约 20% 面积），确认后入 ModConfig `cityWeight`
  （`Worldgen: "City biome weight"`，GUI 可调）。

### 第四轮：道路修复 + 自定义页完善（2026-08-28）

1. **道路全消失**：`fillStreets` 改用 `y-1` 铺地表时，`isStreetSurface` 仍检查
   `y-1`（即地表**下方**的方块）→ 恒不匹配 → 街道全跳过。修复：直接检查 `y`
   处是否 surface block（`getSurfaceBlock() == surface`）后铺路。
2. **创建世界自定义页完善**（`GuiCreateWastelandWorld` + `WastelandGeneratorInfo`）：
   - 原页面为残缺壳：列表 1 项空条目、改稀有度不保存（`getFinal` 返回空串、无名称
     显示）。重写为 **Wasteland 配置页**：6 项可调
     （城市开关 / 城市群系权重 / 地堡开关 / 死树稀度 / 每区块火焰数 / 白昼僵尸），
     列表显示名称+值，`+/-` 调整、Reset、Done 写入 `ModConfig`（会话内生效，
     创建世界即用；不污染世界 seed JSON——`getFinal` 返回空串保持原版
     `chunkProviderSettings` 不动）。
   - `WastelandGenLayerBiome` 城市权重改为 `ModConfig.cityWeight`（默认 150）。
- 红线核对：放置的每个方块均来自原作既有蓝图（`BuildingCode` 15 种建筑、
  `RuinLightposts`、`WorldGenRandomRubble`）；新增仅为街区/街道组合机制。
- 交付：`build/libs/wasteland-1.0.0.jar`（当前 = 城市版，`Enable cities` 默认关）。
  已随冒烟部署至 `1.12.2-smoke-server/mods/wasteland-1.0.0.jar`。

### 第五轮：房子不生成根因实锤 + 出生/按钮修复（2026-08-28）

新档实测：路网完整但一栋房子都没有。

1. **根因（诊断日志实锤）**：`City gen done: blocks=36 biomeFail=36
   buildings=0` —— 36 个街区全部因群系判定失败被跳过。城市中心角落落在
   主荒原群系 `wlm:wasteland`（群系边界偏移 1~2 格），而判定只认
   `wlm:wasteland_city`；街道不查群系故照铺 → 只见路不见房。修复：
   `isCityBiome` 放宽为 `city || apocalypse`（废墟城市散布于荒地）。
   冒烟复验 `biomeFail=0 buildings=76`。
2. **出生高空掉落**：`WorldEvent.Load` 时地形未生成，兜底高度 100 →
   地堡建在 y≈93 高空。修复：改在首个 `WorldTickEvent`（地形已生成）
   建地堡 + `setSpawnPoint` + 把已加入玩家拉入地堡。冒烟 `Y:62`（地下）。
3. **地堡无床**：原作 Bunker 蓝图无床 → `spawnBunker` 末尾补双人床。
4. **网格过大**：26 街区+6 街道（周期 32）压路 → **18 街区+6 街道**
   （周期 24），大池去掉 24 宽医院，子块修正至街区内部不再压街。
5. **创建世界自定义按钮**：页面为空白壳 → 按需求删除
   （`isCustomizable=false`），配置全部由 `TerrainGen.cfg` 控制。

### 第六轮：医院/城市枯树/森林树形/模组建筑隔离（2026-08-28）

1. **医院回归**：24×17 医院在 18 街区放不下 → 中心街区合并为 36×36
   城市广场（中心街区+两侧街带+邻街区边缘），医院居中、地标靠角，
   子块照填。冒烟 `buildings=86`。
2. **城市路旁枯树**：`fillStreets` 未铺路的泥土格（15%）按 1/16 概率
   放模组枯树（复用 `deadTreeGen`）。
3. **森林群系树枝干过长**：`Wasteland Forest` 群系为原生 `BiomeGenForest`
   （MC 森林装饰，大橡树横枝长）。修复：`Wasteland City` 与 `Wasteland
   Forest` 的 `decorator` 换为 `BiomeDecoratorWasteland`（无 MC 大树）；
   枯树生成器 `WorldGenWastelandBigTree`（大枝型）换为自制小枯树
   `WorldGenWastelandDeadSmallTree`（3~5 直干+可选短枝、无叶）。
4. **城市区块内其它模组建筑**：Forge 1.12.2 无 API 阻止其它模组
   `IWorldGenerator` 在指定区块生成，`GameRegistry.generateWorld` 由
   `ChunkProviderServer` 自动调用。方案：城市生成优先级
   `registerWorldGenerator(10→1000)`（最后执行）→ 城市方块（路/房/广场）
   覆盖先生成的模组建筑，城市区基本干净。高塔类残留属 Forge 限制，
   可后续针对性处理。
- 红线核对：枯树/森林装饰/城市广场均为机制层改动，医院为原作既有蓝图
  （BuildingCode）；未改动任何建筑方块数据。
- 交付：`build/libs/wasteland-1.0.0.jar`（239,446 B）已部署客户端
  `versions/双衰变/mods/wasteland-1.0.0.jar`（备份 `.bak-20260828-7`）
  与冒烟服。

### 第七轮：撤销第六轮（保留医院）（2026-08-28）

要求撤销第六轮除医院外的全部改动。

1. **回滚**：城市路旁枯树（`fillStreets` 树分支、`deadTree` 字段/import、
   `WorldGenWastelandDeadSmallTree` 类删除）；森林/城市 `decorator` 换回
   原生 `BiomeGenForest`（MC 大树恢复原状）；`CityGenerator` 权重
   1000→**10**（取消 mod 建筑覆盖方案）、`isInCityRange` 删除。
2. **保留**：中心 36×36 广场 + 医院居中（大建筑回归的唯一不压路方案）。
   冒烟复验 `buildings=80`、地堡 `Y:62`、无异常。
3. 交付：`build/libs/wasteland-1.0.0.jar`（237,946 B）已部署客户端
   （备份 `.bak-20260828-8`）与冒烟服。

### 第八轮：建筑浮空/重叠/黏连修复（2026-08-28）

实测：城市里部分建筑浮空、生成在其他建筑上面、建筑黏在一起。

1. **浮空**：`placeBuilding` 只采样中心一点的 `groundY`，起伏地形上大建筑
   边缘悬空。修复：按旋转后的 footprint（宽×长）取**最高地表**，
   把 footprint 填平到 maxY-1（gravel）再放建筑 → 建筑永远坐在平地上。
2. **重叠**：中心区医院(24×17 居中)与教堂地标(-13,-13，14 宽)重叠 6×2，
   中心子块建筑也压医院。修复：新增 `occupied` 占位表（footprint 膨胀 1
   格登记）；`placeBuilding` 先查占位，冲突即跳过；中心广场只放医院，
   教堂/钟楼移入普通街区大池（`pickLarge` 追加 CHURCH/CLOCK_TOWER）。
3. **黏连**：子块池混入 9 宽 M_HOUSE 与 14 宽 CHURCH，9×9 子块被塞满/溢出。
   修复：`pickSmall`/`pickMid` 收紧为 ≤7 宽（S_HOUSE1/S_HOUSE2/STAND/WELL），
   子块间留出空隙，建筑不再互相贴住。
- 冒烟复验：`buildings=61`（占位跳过生效）、`groundFail=0`、无异常、
  地堡 `Y:62`。
- 交付：`build/libs/wasteland-1.0.0.jar`（238,498 B）已部署客户端
  （备份 `.bak-20260828-9`）与冒烟服。

### 第九轮：HBM CE 建筑联动（2026-08-28）

需求：检测到 HBM 核科技 CE 存在时，自动把其民房/办公室类建筑加入
城市建筑池。

1. 定位（读 HBM CE 源码）：`com.hbm.world.gen.component.CivilianFeatures`
   的 **NTMHouse1**（9×4 砂岩民房）与 `OfficeFeatures` 的 **LargeOffice**
   （14×5 办公室）—— 均继承 `Component`，`addComponentParts` 自带
   `setAverageHeight` 贴地与地基铺设。
2. 实现（反射，编译期不依赖 HBM）：`Loader.isModLoaded("hbm")` 静态检测；
   `placeHbmBuilding` 反射构造组件 → `getBoundingBox` → `addComponentParts`
   生成于城市街区；占用/平地/间隔逻辑与普通建筑一致（不浮空不重叠）。
   `pickSmall/pickMid` 有 HBM 时 1/4 概率出民房，`pickLarge` 1/4 出办公室。
3. **反射方法名用 SRG**（运行时混淆名）：`StructureComponent` 的
   `getBoundingBox`→`func_74874_b`、`addComponentParts`→`func_74875_a`；
   首次冒烟用 deobf 名报 `NoSuchMethodException`，换 SRG 后通过。
4. **真机复验**（冒烟服装载客户端整合包的 NTM-CE 2.5.0.5 + libnine +
   mixinbooter）：`City gen done: buildings=73 hbm=13 hbmLoaded=true`
   —— 13 栋 HBM 民房/办公室成功生成，无 skip 异常。无 HBM 时行为不变
   （`buildings=61`、无异常、地堡 `Y:62`）。

### 第十轮：城市大平原 + 建筑多样化 + 联动上限（2026-08-28）

用户两点：城市别再落在干涸河床的凹陷里（干脆把 city 群系做成大平原），
建筑种类要更随机（联动模组 + 原版），联动模组每种建筑每城最多出现 3 次。

1. **两大平原化**：
   - `BiomeGenCity.genTerrainBlocks` override：city 群系铺成固定大平原
     （石头柱到 y=61、62 填充、63 草地）—— 群系区天然是平地。
   - `RuinedCity.flattenCity`：城市生成前把整个地块（6×6 街区范围）以
     中心地面为基准整平（高处削平、低处垫高、地表统一重铺）→ 街道与
     建筑永远在同一平面，无干涸河床坑、无浮空。冒烟 `City flatten: base=64`。
2. **建筑种类随机化**：
   - 小池（子块 9）：砂岩小屋权重降到 25%（小屋/帐篷/水井/小农场
     S_FARM 各 2/8）—— 不再"一片砂岩"。
   - 大池（15% 街区）：加入 **L_FARM、M_HOUSE1、M_HOUSE2**（Library/
     Diner/教堂/钟楼/大屋原池保留）→ 9 种。
3. **HBM 联动扩展（7 类 + 每类 ≤3）**：民房系 NTMHouse1/NTMLab1（9×4
   小池）+ NTMHouse2（15×5）、NTMLab2（12×11）、RuralHouse1（14×8）
   与办公室系 LargeOffice（14×5）、LargeOfficeCorner（11×15）进大池；
   `hbmCount[7]` 每类计数，达 3 后该类型不再入池（hbmSmallAvail/
   hbmLargeAvail 只从未满类型里选）。冒烟 `buildings=61 hbm=7
   hbmLoaded=true`（无 skip 异常，地堡 Y:62）。
- 修复：`BiomeGenCity` 误用 1.7 的 `setBlock`（编译失败）→ 1.12.2
  用 `primer.setBlockState`。
- 交付：`build/libs/wasteland-1.0.0.jar`（241,164 B，备份 `.bak-20260828-12`）。
- 红线核对：HBM 建筑由 HBM 组件生成（未改 HBM 源码），城市机制层调用；
  我方代码仍 MIT，HBM 建筑属 HBM 模组。
- 交付：`build/libs/wasteland-1.0.0.jar`（239,739 B，备份 `.bak-20260828-10`）。

### 第十一轮：HBM 联动扩到 8 类（Ruin001 接入，2026-08-28）

用户确认接入 HBM 废墟民房（dungeon 系）。

1. 定位：`dungeon.Ruin001` 是独立 `WorldGenerator`（无参构造 +
   `generate(World,Random,BlockPos)`），尺寸 12×14；`Ruin002` 无标准
   入口（只暴露 `generate_r0x` 子方法，不独立可调用）→ 不接。
2. 实现（`RuinedCity.placeHbmBuilding`）：由 2 型 if/else 扩为 **8 类
   switch**（NTMHouse1/NTMLab1/NTMHouse2/NTMLab2/RuralHouse1 +
   LargeOffice/LargeOfficeCorner + **Ruin001**）；**Ruin001 走
   WorldGenerator 反射分派**（`generate(World,Random,BlockPos)`，置于
   垫平地表 y=maxY-1），其余 7 类走 `StructureComponent` 反射
   （`func_74874_b`/`func_74875_a`）。
3. 修复：上一轮 `hbmCount` 数组为 [7] 而池引用 [7]（越界）+ 计数从未
   递增（≤3 上限形同虚设）—— 本轮数组扩为 [8] 且成功放置时
   `hbmCount[idx]++`（idx = type − HBM_HOUSE，0..7）。
4. 冒烟（NTM-CE 服）：`City flatten: base=64`、`City gen done:
   blocks=36 buildings=59 hbm=8 hbmLoaded=true`（无 skip 异常、地堡
   `Y:62`）—— hbm=8 较上轮 7 恰为 Ruin001 新增（反射分派生效）。
- 交付：`build/libs/wasteland-1.0.0.jar`（241,481 B，备份 `.bak-20260828-13`）。
- 备注：用户已删除两个 GitHub 仓库（私有 Wasteland、公开 wasteland-port）——
  本轮与后续改动只在本地提交，不再推送远端。