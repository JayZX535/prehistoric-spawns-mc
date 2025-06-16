package com.jayzx535.prehistoricspawns;

import com.corvicraft.corvicraftspawns.spawnconfig.CorvicraftSpawnEntry;
import com.corvicraft.corvicraftspawns.spawnconfig.CorvicraftSpawnHandler;
import com.corvicraft.corvicraftspawns.spawnconfig.CorvicraftSpawnSet;
import com.corvicraft.corvicraftspawns.spawnconfig.RarityModifiers;
import com.github.teamfossilsarcheology.fossil.entity.ModEntities;
import com.mojang.logging.LogUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.slf4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Mod(PrehistoricSpawns.MODID)
public class PrehistoricSpawns {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final String MODID = "prehistoricspawns";
	private static CorvicraftSpawnHandler SPAWNS;

	// These are shorthands for legibility
	private static final SpawnPlacements.Type ON_GROUND = SpawnPlacements.Type.ON_GROUND;
	private static final SpawnPlacements.Type IN_WATER = SpawnPlacements.Type.IN_WATER;
	private static final Heightmap.Types MOTION_BLOCKING_NO_LEAVES = Heightmap.Types.MOTION_BLOCKING_NO_LEAVES;
	
	public PrehistoricSpawns() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		eventBus.addListener(this::setup);
    	eventBus.addListener(this::loadConfig);
    	eventBus.addListener(this::reloadConfig);
    	MinecraftForge.EVENT_BUS.addListener(this::biomeLoadingEvent);
    	
		// Configs
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC, MODID + "-common.toml");
	}

	public void loadConfig(ModConfigEvent.Loading eventIn) { if (SPAWNS == null) initSpawns(); }
	
	public void reloadConfig(ModConfigEvent.Reloading eventIn) { if (SPAWNS == null) initSpawns(); }
	
	private void setup(final FMLCommonSetupEvent event) {
		
		/* Because dinosaurs don't spawn naturally, none of them had placements registered, causing a lot of improper spawn positions.
		 * To counteract this, we register spawn placements for each entity. */
		
		event.enqueueWork(() -> {
			SpawnPlacements.register(ModEntities.ALLOSAURUS.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.ANKYLOSAURUS.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.AQUILOLAMNA.get(), IN_WATER, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricWaterSpawnRules);
			SpawnPlacements.register(ModEntities.ARTHROPLEURA.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.BRACHIOSAURUS.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.CERATOSAURUS.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.CITIPATI.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.COMPSOGNATHUS.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRulesAllowDarkness);
			SpawnPlacements.register(ModEntities.CONFUCIUSORNIS.get(), ON_GROUND, Heightmap.Types.MOTION_BLOCKING, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.CRASSIGYRINUS.get(), IN_WATER, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricWaterSpawnRules);
			SpawnPlacements.register(ModEntities.DEINONYCHUS.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRulesAllowDarkness);
			SpawnPlacements.register(ModEntities.DICRANURUS.get(), IN_WATER, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricWaterSpawnRules);
			SpawnPlacements.register(ModEntities.DILOPHOSAURUS.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.DIMETRODON.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.DIMORPHODON.get(), ON_GROUND, Heightmap.Types.MOTION_BLOCKING, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.DIPLOCAULUS.get(), IN_WATER, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricWaterSpawnRules);
			SpawnPlacements.register(ModEntities.DIPLODOCUS.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.DODO.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.DRYOSAURUS.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.EDAPHOSAURUS.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.ELASMOTHERIUM.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.GALLIMIMUS.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.GASTORNIS.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.HENODUS.get(), IN_WATER, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricWaterSpawnRules);
			SpawnPlacements.register(ModEntities.ICHTHYOSAURUS.get(), IN_WATER, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricWaterSpawnRules);
			SpawnPlacements.register(ModEntities.KELENKEN.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.LIOPLEURODON.get(), IN_WATER, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricWaterSpawnRules);
			SpawnPlacements.register(ModEntities.LONCHODOMAS.get(), IN_WATER, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricWaterSpawnRules);
			SpawnPlacements.register(ModEntities.MAMMOTH.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.MEGALANIA.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.MEGALOCEROS.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.MEGALODON.get(), IN_WATER, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricWaterSpawnRules);
			SpawnPlacements.register(ModEntities.MEGANEURA.get(), ON_GROUND, Heightmap.Types.MOTION_BLOCKING, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.MOSASAURUS.get(), IN_WATER, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricWaterSpawnRules);
			SpawnPlacements.register(ModEntities.ORNITHOLESTES.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.PACHYCEPHALOSAURUS.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.PACHYRHINOSAURUS.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.PARASAUROLOPHUS.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.PHORUSRHACOS.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.PLATYBELODON.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.PLESIOSAURUS.get(), IN_WATER, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricWaterSpawnRules);
			SpawnPlacements.register(ModEntities.PROTOCERATOPS.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.PSITTACOSAURUS.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.PTERANODON.get(), ON_GROUND, Heightmap.Types.MOTION_BLOCKING, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.QUAGGA.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.QUETZALCOATLUS.get(), ON_GROUND, Heightmap.Types.MOTION_BLOCKING, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.SARCOSUCHUS.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.SCOTOHARPES.get(), IN_WATER, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricWaterSpawnRules);
			SpawnPlacements.register(ModEntities.SMILODON.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.SPINOSAURUS.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.STEGOSAURUS.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.THERIZINOSAURUS.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.TIKTAALIK.get(), IN_WATER, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.TITANIS.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.TRICERATOPS.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.TYRANNOSAURUS.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.VELOCIRAPTOR.get(), ON_GROUND, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricSpawnRules);
			SpawnPlacements.register(ModEntities.WALLISEROPS.get(), IN_WATER, MOTION_BLOCKING_NO_LEAVES, PrehistoricSpawns::checkPrehistoricWaterSpawnRules);
		});
    }
	
	public void biomeLoadingEvent(BiomeLoadingEvent eventIn) { 
		if (SPAWNS == null) initSpawns();
		SPAWNS.addSpawns(eventIn);
	}
	
	public static void initSpawns() { 
		SPAWNS = createSpawns();
		SPAWNS.loadSpawns();
	}
	
	public static SpawnHandler createSpawns() {
		SpawnHandler.Builder spawnBuilder = new SpawnHandler.Builder(PrehistoricSpawns.MODID)
			
		// Badlands
		.withSpawnSet(new CorvicraftSpawnSet.Builder("badlands").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.ALLOSAURUS.get()).withWeight(10).withPackSize(1).withRarityModifiers(new RarityModifiers(0.05D)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.CITIPATI.get()).withWeight(4).withPackMin(1).withPackMax(3).withRarityModifiers(new RarityModifiers(0.75D)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DILOPHOSAURUS.get()).withWeight(8).withPackMin(2).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MEGALANIA.get()).withWeight(30).withPackSize(1).withRarityModifiers(new RarityModifiers(0.05D)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PROTOCERATOPS.get()).withWeight(12).withPackMin(1).withPackMax(3).withRarityModifiers(new RarityModifiers(0.75D)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.STEGOSAURUS.get()).withWeight(20).withPackMin(2).withPackMax(5).withRarityModifiers(new RarityModifiers(0.75D)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.VELOCIRAPTOR.get()).withWeight(8).withPackMin(1).withPackMax(3).withRarityModifiers(new RarityModifiers(0.75D)).build()
		).build(), getBadlandsBiomes())
		.withSpawnSet(new CorvicraftSpawnSet.Builder("wooded_badlands").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.ALLOSAURUS.get()).withWeight(10).withPackSize(1).withRarityModifiers(new RarityModifiers(0.05D)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DILOPHOSAURUS.get()).withWeight(6).withPackMin(2).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MEGALANIA.get()).withWeight(30).withPackSize(1).withRarityModifiers(new RarityModifiers(0.05D)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PROTOCERATOPS.get()).withWeight(12).withPackMin(1).withPackMax(3).withRarityModifiers(new RarityModifiers(0.75D)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.STEGOSAURUS.get()).withWeight(20).withPackMin(2).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.VELOCIRAPTOR.get()).withWeight(4).withPackMin(1).withPackMax(3).withRarityModifiers(new RarityModifiers(0.75D)).build()
		).build(), getWoodedBadlandsBiomes())
				
		// Beach
		.withSpawnSet(new CorvicraftSpawnSet.Builder("beach").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.DILOPHOSAURUS.get()).withWeight(2).withPackMin(1).withPackMax(4).withRarityModifiers(new RarityModifiers(0.75D)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DIMORPHODON.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DODO.get()).withWeight(8).withPackMin(1).withPackMax(4).withRarityModifiers(new RarityModifiers(0.75D)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.ICHTHYOSAURUS.get()).withWeight(12).withPackMin(1).withPackMax(2).withRarityModifiers(new RarityModifiers(0.25D)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PACHYCEPHALOSAURUS.get()).withWeight(12).withPackSize(1).withRarityModifiers(new RarityModifiers(0.5D)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PTERANODON.get()).withWeight(4).withPackMin(1).withPackMax(3).build()
		).build(), getBeachBiomes())
		.withSpawnSet(new CorvicraftSpawnSet.Builder("rocky_beach").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.DIMORPHODON.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.ICHTHYOSAURUS.get()).withWeight(12).withPackMin(1).withRarityModifiers(new RarityModifiers(0.25D)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PTERANODON.get()).withWeight(4).withPackMin(1).withPackMax(3).build()
		).build(), getRockyBeachBiomes())
		
		// Desert
		.withSpawnSet(new CorvicraftSpawnSet.Builder("desert").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.ALLOSAURUS.get()).withWeight(10).withPackSize(1).withRarityModifiers(new RarityModifiers(0.05D)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.CITIPATI.get()).withWeight(6).withPackMin(2).withPackMax(4).withRarityModifiers(new RarityModifiers(0.75D)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DILOPHOSAURUS.get()).withWeight(4).withPackMin(2).withPackMax(4).withRarityModifiers(new RarityModifiers(0.75D)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MEGALANIA.get()).withWeight(30).withPackSize(1).withRarityModifiers(new RarityModifiers(0.05D)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PROTOCERATOPS.get()).withWeight(20).withPackMin(1).withPackMax(3).withRarityModifiers(new RarityModifiers(0.5D)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.STEGOSAURUS.get()).withWeight(20).withPackMin(2).withPackMax(5).withRarityModifiers(new RarityModifiers(0.75D)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.VELOCIRAPTOR.get()).withWeight(8).withPackMin(1).withPackMax(3).withRarityModifiers(new RarityModifiers(0.75D)).build()
		).build(), getDesertBiomes())
		.withSpawnSet(new CorvicraftSpawnSet.Builder("red_desert").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.ALLOSAURUS.get()).withWeight(10).withPackSize(1).withRarityModifiers(new RarityModifiers(0.05D)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.CITIPATI.get()).withWeight(6).withPackMin(2).withPackMax(4).withRarityModifiers(new RarityModifiers(0.75D)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DILOPHOSAURUS.get()).withWeight(4).withPackMin(2).withPackMax(4).withRarityModifiers(new RarityModifiers(0.75D)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MEGALANIA.get()).withWeight(30).withPackSize(1).withRarityModifiers(new RarityModifiers(0.05D)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PROTOCERATOPS.get()).withWeight(20).withPackMin(1).withPackMax(3).withRarityModifiers(new RarityModifiers(0.5D)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.STEGOSAURUS.get()).withWeight(20).withPackMin(2).withPackMax(5).withRarityModifiers(new RarityModifiers(0.75D)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.VELOCIRAPTOR.get()).withWeight(8).withPackMin(1).withPackMax(3).withRarityModifiers(new RarityModifiers(0.75D)).build()
		).build(), getRedDesertBiomes())
		
		// Deadlands (No current default entries)
		/**.withSpawnSet(new CorvicraftSpawnSet.Builder("deadlands").withSpawns(
		).build(), getDeadlandBiomes())*/
			
		// Forest
		.withSpawnSet(new CorvicraftSpawnSet.Builder("forest").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.ANKYLOSAURUS.get()).withWeight(4).withPackSize(1).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DIMORPHODON.get()).withWeight(8).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DRYOSAURUS.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.GASTORNIS.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MEGALOCEROS.get()).withWeight(12).withPackMin(2).withPackMax(6).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.ORNITHOLESTES.get()).withWeight(8).withPackMin(1).withPackMax(2).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PACHYRHINOSAURUS.get()).withWeight(8).withPackMin(1).withPackMax(2).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PARASAUROLOPHUS.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PHORUSRHACOS.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PSITTACOSAURUS.get()).withWeight(8).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.SMILODON.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.TRICERATOPS.get()).withWeight(12).withPackMin(1).withPackMax(3).build()
		).build(), getForestBiomes())
		
		.withSpawnSet(new CorvicraftSpawnSet.Builder("autumnal_forest").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.DIMORPHODON.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DRYOSAURUS.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.GASTORNIS.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.ORNITHOLESTES.get()).withWeight(8).withPackMin(1).withPackMax(2).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PACHYRHINOSAURUS.get()).withWeight(8).withPackMin(1).withPackMax(2).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PARASAUROLOPHUS.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PHORUSRHACOS.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PSITTACOSAURUS.get()).withWeight(8).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.SMILODON.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.TRICERATOPS.get()).withWeight(12).withPackMin(1).withPackMax(3).build()
		).build(), getAutumnalForestBiomes())
		
		.withSpawnSet(new CorvicraftSpawnSet.Builder("birch_forest").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.DIMORPHODON.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DRYOSAURUS.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.GASTORNIS.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MEGALOCEROS.get()).withWeight(12).withPackMin(2).withPackMax(6).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.ORNITHOLESTES.get()).withWeight(8).withPackMin(1).withPackMax(2).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PACHYRHINOSAURUS.get()).withWeight(8).withPackMin(1).withPackMax(2).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PARASAUROLOPHUS.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PHORUSRHACOS.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PSITTACOSAURUS.get()).withWeight(8).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.SMILODON.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.TRICERATOPS.get()).withWeight(12).withPackMin(1).withPackMax(3).build()
		).build(), getBirchForestBiomes())
		
		.withSpawnSet(new CorvicraftSpawnSet.Builder("cherry_forest").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.DIMORPHODON.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.GASTORNIS.get()).withWeight(2).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.ORNITHOLESTES.get()).withWeight(8).withPackMin(1).withPackMax(2).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PARASAUROLOPHUS.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PHORUSRHACOS.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PSITTACOSAURUS.get()).withWeight(8).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.TRICERATOPS.get()).withWeight(12).withPackMin(1).withPackMax(3).build()
		).build(), getCherryForestBiomes())
		
		.withSpawnSet(new CorvicraftSpawnSet.Builder("dark_forest").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.DIMORPHODON.get()).withWeight(8).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.GASTORNIS.get()).withWeight(2).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DRYOSAURUS.get()).withWeight(8).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.ORNITHOLESTES.get()).withWeight(8).withPackMin(1).withPackMax(2).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PHORUSRHACOS.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PSITTACOSAURUS.get()).withWeight(12).withPackMin(1).withPackMax(3).build()
		).build(), getDarkForestBiomes())
		
		.withSpawnSet(new CorvicraftSpawnSet.Builder("sparse_forest").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.ANKYLOSAURUS.get()).withWeight(12).withPackSize(1).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DIMORPHODON.get()).withWeight(8).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DRYOSAURUS.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.GASTORNIS.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MEGALOCEROS.get()).withWeight(12).withPackMin(2).withPackMax(6).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.ORNITHOLESTES.get()).withWeight(8).withPackMin(1).withPackMax(2).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PACHYRHINOSAURUS.get()).withWeight(4).withPackMin(1).withPackMax(2).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PARASAUROLOPHUS.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PHORUSRHACOS.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PSITTACOSAURUS.get()).withWeight(8).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.TRICERATOPS.get()).withWeight(20).withPackMin(2).withPackMax(5).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.TYRANNOSAURUS.get()).withWeight(1).withPackSize(1).build()
		).build(), getSparseForestBiomes())
			
		// Frozen
		.withSpawnSet(new CorvicraftSpawnSet.Builder("frozen").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.ELASMOTHERIUM.get()).withWeight(12).withPackMin(2).withPackMax(5).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MAMMOTH.get()).withWeight(8).withPackMin(2).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MEGALOCEROS.get()).withWeight(8).withPackMin(2).withPackMax(4).build()
		).build(), getFrozenBiomes())
		.withSpawnSet(new CorvicraftSpawnSet.Builder("tundra").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.ELASMOTHERIUM.get()).withWeight(12).withPackMin(2).withPackMax(5).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MAMMOTH.get()).withWeight(6).withPackMin(2).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MEGALOCEROS.get()).withWeight(8).withPackMin(2).withPackMax(4).build()
		).build(), getTundraBiomes())
		
		// Magic (No current default entries)
		/**.withSpawnSet(new CorvicraftSpawnSet.Builder("magic").withSpawns(
		).build(), getMagicBiomes())*/
		/**.withSpawnSet(new CorvicraftSpawnSet.Builder("mushroom").withSpawns(
		).build(), getMushroomBiomes())*/
		
		// Mountain
		.withSpawnSet(new CorvicraftSpawnSet.Builder("mountain").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.KELENKEN.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.SMILODON.get()).withWeight(4).withPackMin(1).withPackMax(3).build()
		).build(), getMountainBiomes())
		.withSpawnSet(new CorvicraftSpawnSet.Builder("snowy_mountain").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.SMILODON.get()).withWeight(4).withPackMin(1).withPackMax(3).build()
		).build(), getSnowyMountainBiomes())
		
		// Plains
		.withSpawnSet(new CorvicraftSpawnSet.Builder("plains").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.CERATOSAURUS.get()).withWeight(4).withPackSize(1).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.CONFUCIUSORNIS.get()).withWeight(12).withPackMin(1).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DIPLODOCUS.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DRYOSAURUS.get()).withWeight(16).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.GALLIMIMUS.get()).withWeight(8).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.ORNITHOLESTES.get()).withWeight(12).withPackMin(1).withPackMax(2).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PARASAUROLOPHUS.get()).withWeight(20).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PHORUSRHACOS.get()).withWeight(4).withPackMin(1).withPackMax(2).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.QUAGGA.get()).withWeight(8).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.TITANIS.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.TRICERATOPS.get()).withWeight(8).withPackMin(2).withPackMax(5).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.TYRANNOSAURUS.get()).withWeight(1).withPackSize(1).build()
		).build(), getPlainsBiomes())
		.withSpawnSet(new CorvicraftSpawnSet.Builder("floral_fields").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.CONFUCIUSORNIS.get()).withWeight(12).withPackMin(1).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DIPLODOCUS.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DRYOSAURUS.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.GALLIMIMUS.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.ORNITHOLESTES.get()).withWeight(12).withPackMin(1).withPackMax(2).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PARASAUROLOPHUS.get()).withWeight(20).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PHORUSRHACOS.get()).withWeight(4).withPackMin(1).withPackMax(2).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.QUAGGA.get()).withWeight(8).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.TITANIS.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.TRICERATOPS.get()).withWeight(8).withPackMin(2).withPackMax(5).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.TYRANNOSAURUS.get()).withWeight(1).withPackSize(1).build()
		).build(), getFloralFieldsBiomes())
		.withSpawnSet(new CorvicraftSpawnSet.Builder("highlands").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.PARASAUROLOPHUS.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PHORUSRHACOS.get()).withWeight(4).withPackMin(1).withPackMax(2).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.QUAGGA.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.SMILODON.get()).withWeight(2).withPackMin(1).withPackMax(2).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.TITANIS.get()).withWeight(4).withPackMin(1).withPackMax(3).build()
		).build(), getHighlandsBiomes())
		.withSpawnSet(new CorvicraftSpawnSet.Builder("prairie").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.CERATOSAURUS.get()).withWeight(8).withPackSize(1).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.CONFUCIUSORNIS.get()).withWeight(12).withPackMin(1).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DIPLODOCUS.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DRYOSAURUS.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.GALLIMIMUS.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.ORNITHOLESTES.get()).withWeight(12).withPackMin(1).withPackMax(2).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PARASAUROLOPHUS.get()).withWeight(20).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PHORUSRHACOS.get()).withWeight(4).withPackMin(1).withPackMax(2).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.QUAGGA.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.TITANIS.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.TRICERATOPS.get()).withWeight(8).withPackMin(2).withPackMax(5).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.TYRANNOSAURUS.get()).withWeight(1).withPackSize(1).build()
		).build(), getPrairieBiomes())
		
		// Savanna
		.withSpawnSet(new CorvicraftSpawnSet.Builder("savanna").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.ALLOSAURUS.get()).withWeight(2).withPackSize(1).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.BRACHIOSAURUS.get()).withWeight(4).withPackMin(2).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.CITIPATI.get()).withWeight(12).withPackMin(2).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.GALLIMIMUS.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MEGALANIA.get()).withWeight(6).withPackSize(1).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PLATYBELODON.get()).withWeight(8).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PSITTACOSAURUS.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.QUAGGA.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.STEGOSAURUS.get()).withWeight(12).withPackMin(2).withPackMax(5).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.THERIZINOSAURUS.get()).withWeight(8).withPackMin(1).withPackMax(2).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.TRICERATOPS.get()).withWeight(12).withPackMin(2).withPackMax(5).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.TYRANNOSAURUS.get()).withWeight(1).withPackSize(1).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.VELOCIRAPTOR.get()).withWeight(8).withPackMin(2).withPackMax(5).build()
		).build(), getSavannaBiomes())
		.withSpawnSet(new CorvicraftSpawnSet.Builder("sparse_savanna").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.BRACHIOSAURUS.get()).withWeight(6).withPackMin(2).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.GALLIMIMUS.get()).withWeight(16).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MEGALANIA.get()).withWeight(6).withPackSize(1).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PLATYBELODON.get()).withWeight(8).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PSITTACOSAURUS.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.QUAGGA.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.STEGOSAURUS.get()).withWeight(20).withPackMin(2).withPackMax(5).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.THERIZINOSAURUS.get()).withWeight(8).withPackMin(1).withPackMax(2).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.TRICERATOPS.get()).withWeight(12).withPackMin(2).withPackMax(5).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.TYRANNOSAURUS.get()).withWeight(1).withPackSize(1).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.VELOCIRAPTOR.get()).withWeight(8).withPackMin(2).withPackMax(5).build()
		).build(), getSparseSavannaBiomes())
		.withSpawnSet(new CorvicraftSpawnSet.Builder("dense_savanna").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.GALLIMIMUS.get()).withWeight(8).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MEGALANIA.get()).withWeight(4).withPackSize(1).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PLATYBELODON.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PSITTACOSAURUS.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.QUAGGA.get()).withWeight(8).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.STEGOSAURUS.get()).withWeight(8).withPackMin(2).withPackMax(5).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.THERIZINOSAURUS.get()).withWeight(8).withPackMin(1).withPackMax(2).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.TRICERATOPS.get()).withWeight(8).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.VELOCIRAPTOR.get()).withWeight(8).withPackMin(2).withPackMax(5).build()
		).build(), getDenseSavannaBiomes())
		
		// Seasonal
		.withSpawnSet(new CorvicraftSpawnSet.Builder("seasonal").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.DRYOSAURUS.get()).withWeight(8).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.GALLIMIMUS.get()).withWeight(8).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PSITTACOSAURUS.get()).withWeight(8).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.STEGOSAURUS.get()).withWeight(8).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.TRICERATOPS.get()).withWeight(8).withPackMin(2).withPackMax(5).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.TYRANNOSAURUS.get()).withWeight(1).withPackSize(1).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.VELOCIRAPTOR.get()).withWeight(8).withPackMin(1).withPackMax(3).build()
		).build(), getSeasonalBiomes())
		.withSpawnSet(new CorvicraftSpawnSet.Builder("dense_seasonal").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.DRYOSAURUS.get()).withWeight(8).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.GASTORNIS.get()).withWeight(2).withPackMin(1).withPackMax(2).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PSITTACOSAURUS.get()).withWeight(8).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.STEGOSAURUS.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.TRICERATOPS.get()).withWeight(4).withPackMin(1).withPackMax(3).build()
		).build(), getDenseSeasonalBiomes())
		
		// Semi Arid (NO VANILLA SPAWNS)
		.withSpawnSet(new CorvicraftSpawnSet.Builder("dense_semi_arid").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.ALLOSAURUS.get()).withWeight(4).withPackSize(1).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.CITIPATI.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DILOPHOSAURUS.get()).withWeight(8).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.GALLIMIMUS.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MEGALANIA.get()).withWeight(6).withPackSize(1).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PLATYBELODON.get()).withWeight(2).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PROTOCERATOPS.get()).withWeight(8).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PSITTACOSAURUS.get()).withWeight(8).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.STEGOSAURUS.get()).withWeight(12).withPackMin(1).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.THERIZINOSAURUS.get()).withWeight(12).withPackMin(1).withPackMax(2).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.VELOCIRAPTOR.get()).withWeight(8).withPackMin(2).withPackMax(5).build()
		).build(), getDenseSemiAridBiomes())
		.withSpawnSet(new CorvicraftSpawnSet.Builder("sparse_semi_arid").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.ALLOSAURUS.get()).withWeight(6).withPackSize(1).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.BRACHIOSAURUS.get()).withWeight(12).withPackMin(2).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.CITIPATI.get()).withWeight(12).withPackMin(2).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DILOPHOSAURUS.get()).withWeight(8).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DIPLODOCUS.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.GALLIMIMUS.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MEGALANIA.get()).withWeight(6).withPackSize(1).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PLATYBELODON.get()).withWeight(2).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PROTOCERATOPS.get()).withWeight(8).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PSITTACOSAURUS.get()).withWeight(8).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.STEGOSAURUS.get()).withWeight(12).withPackMin(1).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.THERIZINOSAURUS.get()).withWeight(8).withPackMin(1).withPackMax(2).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.TYRANNOSAURUS.get()).withWeight(1).withPackSize(1).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.VELOCIRAPTOR.get()).withWeight(8).withPackMin(2).withPackMax(5).build()
		).build(), getSparseSemiAridBiomes())
		
		// Swamp
		.withSpawnSet(new CorvicraftSpawnSet.Builder("swamp").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.CONFUCIUSORNIS.get()).withWeight(12).withPackMin(1).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.CRASSIGYRINUS.get()).withWeight(8).withPackSize(1).withRarityModifiers(new RarityModifiers(0.25F)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DIMETRODON.get()).withWeight(8).withPackMin(2).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DIMORPHODON.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DIPLOCAULUS.get()).withWeight(8).withPackSize(1).withRarityModifiers(new RarityModifiers(0.05F)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.EDAPHOSAURUS.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.HENODUS.get()).withWeight(4).withPackSize(1).withRarityModifiers(new RarityModifiers(0.05F)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PACHYRHINOSAURUS.get()).withWeight(4).withPackMin(1).withPackMax(2).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PARASAUROLOPHUS.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MEGANEURA.get()).withWeight(20).withPackMin(2).withPackMax(6).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.QUETZALCOATLUS.get()).withWeight(1).withPackSize(1).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.SARCOSUCHUS.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.SPINOSAURUS.get()).withWeight(4).withPackSize(1).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.TIKTAALIK.get()).withWeight(4).withPackMin(1).withPackMax(3).build()
		).build(), getSwampBiomes())
		.withSpawnSet(new CorvicraftSpawnSet.Builder("marsh").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.ANKYLOSAURUS.get()).withWeight(4).withPackSize(1).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.CERATOSAURUS.get()).withWeight(8).withPackSize(1).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.CONFUCIUSORNIS.get()).withWeight(12).withPackMin(1).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.CRASSIGYRINUS.get()).withWeight(8).withPackSize(1).withRarityModifiers(new RarityModifiers(0.25F)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DIMETRODON.get()).withWeight(8).withPackMin(2).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DIMORPHODON.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DIPLOCAULUS.get()).withWeight(8).withPackSize(1).withRarityModifiers(new RarityModifiers(0.05F)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.EDAPHOSAURUS.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.GALLIMIMUS.get()).withWeight(8).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.HENODUS.get()).withWeight(4).withPackSize(1).withRarityModifiers(new RarityModifiers(0.05F)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PACHYRHINOSAURUS.get()).withWeight(4).withPackMin(1).withPackMax(2).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PARASAUROLOPHUS.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MEGANEURA.get()).withWeight(20).withPackMin(2).withPackMax(6).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.QUETZALCOATLUS.get()).withWeight(1).withPackSize(1).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.SARCOSUCHUS.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.SPINOSAURUS.get()).withWeight(2).withPackSize(1).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.TIKTAALIK.get()).withWeight(8).withPackMin(1).withPackMax(3).build()
		).build(), getMarshBiomes())
		.withSpawnSet(new CorvicraftSpawnSet.Builder("waterlogged_swamp").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.CONFUCIUSORNIS.get()).withWeight(12).withPackMin(1).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.CRASSIGYRINUS.get()).withWeight(8).withPackSize(1).withRarityModifiers(new RarityModifiers(0.25F)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DIMETRODON.get()).withWeight(8).withPackMin(2).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DIMORPHODON.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DIPLOCAULUS.get()).withWeight(12).withPackSize(1).withRarityModifiers(new RarityModifiers(0.05F)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.EDAPHOSAURUS.get()).withWeight(8).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.HENODUS.get()).withWeight(8).withPackSize(1).withRarityModifiers(new RarityModifiers(0.05F)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MEGANEURA.get()).withWeight(20).withPackMin(2).withPackMax(6).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.SARCOSUCHUS.get()).withWeight(8).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.SPINOSAURUS.get()).withWeight(4).withPackSize(1).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.TIKTAALIK.get()).withWeight(8).withPackMin(1).withPackMax(3).build()
		).build(), getWaterloggedSwampBiomes())
		
		// Taiga
		.withSpawnSet(new CorvicraftSpawnSet.Builder("taiga").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.GASTORNIS.get()).withWeight(2).withPackMin(1).withPackMax(2).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MAMMOTH.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MEGALOCEROS.get()).withWeight(12).withPackMin(2).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.SMILODON.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.TRICERATOPS.get()).withWeight(8).withPackMin(2).withPackMax(5).build()
		).build(), getTaigaBiomes())
		.withSpawnSet(new CorvicraftSpawnSet.Builder("mega_taiga").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.GASTORNIS.get()).withWeight(2).withPackMin(1).withPackMax(2).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MAMMOTH.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MEGALOCEROS.get()).withWeight(12).withPackMin(2).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.SMILODON.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.TRICERATOPS.get()).withWeight(8).withPackMin(2).withPackMax(5).build()
		).build(), getMegaTaigaBiomes())
		.withSpawnSet(new CorvicraftSpawnSet.Builder("snowy_taiga").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.GASTORNIS.get()).withWeight(2).withPackMin(1).withPackMax(2).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MAMMOTH.get()).withWeight(2).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MEGALOCEROS.get()).withWeight(12).withPackMin(2).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.SMILODON.get()).withWeight(4).withPackMin(1).withPackMax(3).build()
		).build(), getSnowyTaigaBiomes())
		.withSpawnSet(new CorvicraftSpawnSet.Builder("sparse_taiga").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.GASTORNIS.get()).withWeight(2).withPackMin(1).withPackMax(2).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MAMMOTH.get()).withWeight(8).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MEGALOCEROS.get()).withWeight(20).withPackMin(2).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.SMILODON.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.TRICERATOPS.get()).withWeight(12).withPackMin(2).withPackMax(5).build()
		).build(), getSparseTaigaBiomes())
		.withSpawnSet(new CorvicraftSpawnSet.Builder("redwood").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.GASTORNIS.get()).withWeight(2).withPackMin(1).withPackMax(2).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MAMMOTH.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MEGALOCEROS.get()).withWeight(12).withPackMin(2).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.SMILODON.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.TRICERATOPS.get()).withWeight(12).withPackMin(2).withPackMax(5).build()
		).build(), getRedwoodBiomes())
		
		// Tropical
		.withSpawnSet(new CorvicraftSpawnSet.Builder("tropical").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.ARTHROPLEURA.get()).withWeight(20).withPackMin(1).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.COMPSOGNATHUS.get()).withWeight(8).withPackMin(2).withPackMax(5).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DEINONYCHUS.get()).withWeight(8).withPackMin(2).withPackMax(5).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DODO.get()).withWeight(20).withPackMin(2).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.EDAPHOSAURUS.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PACHYCEPHALOSAURUS.get()).withWeight(8).withPackSize(1).withRarityModifiers(new RarityModifiers(0.75D)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PSITTACOSAURUS.get()).withWeight(8).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MEGANEURA.get()).withWeight(20).withPackMin(2).withPackMax(6).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.THERIZINOSAURUS.get()).withWeight(8).withPackMin(1).withPackMax(2).build()
		).build(), getTropicalBiomes())
		.withSpawnSet(new CorvicraftSpawnSet.Builder("sparse_tropical").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.ANKYLOSAURUS.get()).withWeight(4).withPackSize(1).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.ARTHROPLEURA.get()).withWeight(20).withPackMin(1).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.COMPSOGNATHUS.get()).withWeight(8).withPackMin(2).withPackMax(5).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DEINONYCHUS.get()).withWeight(8).withPackMin(2).withPackMax(5).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DIPLODOCUS.get()).withWeight(8).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DODO.get()).withWeight(12).withPackMin(2).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.EDAPHOSAURUS.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PACHYCEPHALOSAURUS.get()).withWeight(12).withPackSize(1).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PSITTACOSAURUS.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MEGANEURA.get()).withWeight(16).withPackMin(2).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.QUETZALCOATLUS.get()).withWeight(1).withPackSize(1).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.THERIZINOSAURUS.get()).withWeight(4).withPackMin(1).withPackMax(2).build()
		).build(), getSparseTropicalBiomes())
		.withSpawnSet(new CorvicraftSpawnSet.Builder("dense_tropical").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.ARTHROPLEURA.get()).withWeight(20).withPackMin(1).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.COMPSOGNATHUS.get()).withWeight(8).withPackMin(2).withPackMax(5).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DEINONYCHUS.get()).withWeight(6).withPackMin(2).withPackMax(5).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DODO.get()).withWeight(20).withPackMin(2).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.EDAPHOSAURUS.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PACHYCEPHALOSAURUS.get()).withWeight(8).withPackSize(1).withRarityModifiers(new RarityModifiers(0.5D)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PSITTACOSAURUS.get()).withWeight(8).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MEGANEURA.get()).withWeight(16).withPackMin(2).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.THERIZINOSAURUS.get()).withWeight(4).withPackMin(1).withPackMax(2).build()
		).build(), getDenseTropicalBiomes())
		
		// Wasteland (No current default entries)
		/**.withSpawnSet(new CorvicraftSpawnSet.Builder("wasteland").withSpawns(
		).build(), getWastelandBiomes())*/
		
		// Ocean
		.withSpawnSet(new CorvicraftSpawnSet.Builder("ocean").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.ICHTHYOSAURUS.get()).withWeight(8).withPackMin(1).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MEGALODON.get()).withWeight(1).withPackSize(1).withRarityModifiers(new RarityModifiers(0.5F)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MOSASAURUS.get()).withWeight(1).withPackSize(1).build()
		).build(), getOceanBiomes())
		.withSpawnSet(new CorvicraftSpawnSet.Builder("deep_ocean").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.DICRANURUS.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.ICHTHYOSAURUS.get()).withWeight(12).withPackMin(1).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.LIOPLEURODON.get()).withWeight(4).withPackMin(1).withPackMax(2).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.LONCHODOMAS.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MEGALODON.get()).withWeight(4).withPackSize(1).withRarityModifiers(new RarityModifiers(0.5F)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PLESIOSAURUS.get()).withWeight(1).withPackMax(1).withPackMax(2).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.SCOTOHARPES.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.WALLISEROPS.get()).withWeight(12).withPackMin(1).withPackMax(3).build()
		).build(), getDeepOceanBiomes())
		.withSpawnSet(new CorvicraftSpawnSet.Builder("cold_ocean").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.ICHTHYOSAURUS.get()).withWeight(8).withPackMin(1).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MOSASAURUS.get()).withWeight(1).withPackSize(1).build()
		).build(), getColdOceanBiomes())
		.withSpawnSet(new CorvicraftSpawnSet.Builder("deep_cold_ocean").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.DICRANURUS.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.ICHTHYOSAURUS.get()).withWeight(12).withPackMin(1).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.LIOPLEURODON.get()).withWeight(4).withPackMin(1).withPackMax(2).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.LONCHODOMAS.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PLESIOSAURUS.get()).withWeight(1).withPackMax(1).withPackMax(2).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.SCOTOHARPES.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.WALLISEROPS.get()).withWeight(12).withPackMin(1).withPackMax(3).build()
		).build(), getDeepWarmOceanBiomes())
		.withSpawnSet(new CorvicraftSpawnSet.Builder("warm_ocean").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.AQUILOLAMNA.get()).withWeight(8).withPackMin(1).withPackMax(2).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.ICHTHYOSAURUS.get()).withWeight(8).withPackMin(1).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MEGALODON.get()).withWeight(1).withPackSize(1).withRarityModifiers(new RarityModifiers(0.5F)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MOSASAURUS.get()).withWeight(1).withPackSize(1).build()
		).build(), getWarmOceanBiomes())
		.withSpawnSet(new CorvicraftSpawnSet.Builder("deep_warm_ocean").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.AQUILOLAMNA.get()).withWeight(8).withPackMin(1).withPackMax(2).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DICRANURUS.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.ICHTHYOSAURUS.get()).withWeight(12).withPackMin(1).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.LIOPLEURODON.get()).withWeight(4).withPackMin(1).withPackMax(32).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.LONCHODOMAS.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MEGALODON.get()).withWeight(4).withPackSize(1).withRarityModifiers(new RarityModifiers(0.5F)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.SCOTOHARPES.get()).withWeight(12).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.WALLISEROPS.get()).withWeight(12).withPackMin(1).withPackMax(3).build()
		).build(), getDeepWarmOceanBiomes())
		.withSpawnSet(new CorvicraftSpawnSet.Builder("frozen_ocean").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.ICHTHYOSAURUS.get()).withWeight(8).withPackMin(1).withPackMax(4).build()
		).build(), getFrozenOceanBiomes())
		.withSpawnSet(new CorvicraftSpawnSet.Builder("deep_frozen_ocean").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.ICHTHYOSAURUS.get()).withWeight(8).withPackMin(1).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PLESIOSAURUS.get()).withWeight(1).withPackMax(1).withPackMax(2).build()
		).build(), getDeepFrozenOceanBiomes())
		
		// River
		.withSpawnSet(new CorvicraftSpawnSet.Builder("river").withSpawns(
			new CorvicraftSpawnEntry.Builder(ModEntities.CERATOSAURUS.get()).withWeight(4).withPackSize(1).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DIMETRODON.get()).withWeight(4).withPackMin(1).withPackMax(2).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DIMORPHODON.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DIPLOCAULUS.get()).withWeight(12).withPackSize(1).withRarityModifiers(new RarityModifiers(0.05F)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.DRYOSAURUS.get()).withWeight(8).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.EDAPHOSAURUS.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.HENODUS.get()).withWeight(4).withPackSize(1).withRarityModifiers(new RarityModifiers(0.05F)).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.MEGANEURA.get()).withWeight(8).withPackMin(2).withPackMax(4).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PACHYCEPHALOSAURUS.get()).withWeight(8).withPackSize(1).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.PACHYRHINOSAURUS.get()).withWeight(8).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.QUETZALCOATLUS.get()).withWeight(1).withPackSize(1).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.SARCOSUCHUS.get()).withWeight(4).withPackMin(1).withPackMax(3).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.SPINOSAURUS.get()).withWeight(2).withPackSize(1).build(),
			new CorvicraftSpawnEntry.Builder(ModEntities.TIKTAALIK.get()).withWeight(8).withPackMin(1).withPackMax(3).build()
		).build(), getRiverBiomes())
		
		.withEntityTypes(ModEntities.ALLOSAURUS.get(), ModEntities.ANKYLOSAURUS.get(), ModEntities.AQUILOLAMNA.get(), ModEntities.ARTHROPLEURA.get(),
			ModEntities.BRACHIOSAURUS.get(), ModEntities.CERATOSAURUS.get(), ModEntities.CITIPATI.get(), ModEntities.COMPSOGNATHUS.get(), ModEntities.CONFUCIUSORNIS.get(),
			ModEntities.CRASSIGYRINUS.get(), ModEntities.DEINONYCHUS.get(), ModEntities.DICRANURUS.get(), ModEntities.DILOPHOSAURUS.get(), ModEntities.DIMETRODON.get(),
			ModEntities.DIMORPHODON.get(), ModEntities.DIPLOCAULUS.get(), ModEntities.DIPLODOCUS.get(), ModEntities.DODO.get(), ModEntities.DRYOSAURUS.get(),
			ModEntities.EDAPHOSAURUS.get(), ModEntities.ELASMOTHERIUM.get(), ModEntities.GALLIMIMUS.get(), ModEntities.GASTORNIS.get(), ModEntities.HENODUS.get(),
			ModEntities.ICHTHYOSAURUS.get(), ModEntities.KELENKEN.get(), ModEntities.LIOPLEURODON.get(), ModEntities.LONCHODOMAS.get(), ModEntities.MAMMOTH.get(),
			ModEntities.MEGALANIA.get(), ModEntities.MEGALOCEROS.get(), ModEntities.MEGALODON.get(), ModEntities.MEGANEURA.get(), ModEntities.MOSASAURUS.get(),
			ModEntities.ORNITHOLESTES.get(), ModEntities.PACHYCEPHALOSAURUS.get(), ModEntities.PACHYRHINOSAURUS.get(), ModEntities.PARASAUROLOPHUS.get(),
			ModEntities.PHORUSRHACOS.get(), ModEntities.PLATYBELODON.get(), ModEntities.PLESIOSAURUS.get(), ModEntities.PROTOCERATOPS.get(),
			ModEntities.PSITTACOSAURUS.get(), ModEntities.PTERANODON.get(), ModEntities.QUAGGA.get(), ModEntities.QUETZALCOATLUS.get(), ModEntities.SARCOSUCHUS.get(),
			ModEntities.SCOTOHARPES.get(), ModEntities.SMILODON.get(), ModEntities.SPINOSAURUS.get(), ModEntities.STEGOSAURUS.get(), ModEntities.THERIZINOSAURUS.get(),
			ModEntities.TIKTAALIK.get(), ModEntities.TITANIS.get(), ModEntities.TRICERATOPS.get(), ModEntities.TYRANNOSAURUS.get(), ModEntities.VELOCIRAPTOR.get(),
			ModEntities.WALLISEROPS.get());
		return spawnBuilder.build();
	}
	
	// BADLANDS
	
	/** Hot, dry biomes with clay mountains */
	public static ResourceLocation[] getBadlandsBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		biomeList.add(Biomes.BADLANDS.location());
		biomeList.add(Biomes.ERODED_BADLANDS.location());
		if (checkBYG()) biomeList.add(getBYG("red_rock_valley"));
		return getBiomesAsArray(biomeList);
	}
	
	/** Hot, dry biomes with clay mountains and coarse dirt */
	public static ResourceLocation[] getWoodedBadlandsBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		biomeList.add(Biomes.WOODED_BADLANDS.location());
		if (checkBYG()) biomeList.add(getBYG("sierra_badlands"));
		return getBiomesAsArray(biomeList);
	}
	
	// BEACH
	
	/** Coastal areas */
	public static ResourceLocation[] getBeachBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		biomeList.add(Biomes.BEACH.location());
		if (checkBYG()) {
			biomeList.add(getBYG("rainbow_beach"));
			biomeList.add(getBYG("windswept_beach"));
		}
		if (checkBOP()) biomeList.add(getBOP("dune_beach"));
		return getBiomesAsArray(biomeList);
	}
	
	/** Rocky coastal areas */
	public static ResourceLocation[] getRockyBeachBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		biomeList.add(Biomes.STONY_SHORE.location());
		if (checkBYG()) {
			biomeList.add(getBYG("basalt_barrera"));
			biomeList.add(getBYG("dacite_shores"));
		}
		return getBiomesAsArray(biomeList);
	}
	
	// DESERT
	
	/** Hot, dry biomes with minimal vegetation and sand */
	public static ResourceLocation[] getDesertBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		biomeList.add(Biomes.DESERT.location());
		if (checkBYG()) {
			biomeList.add(getBYG("mojave_desert"));
			biomeList.add(getBYG("windswept_desert"));
			biomeList.add(getBYG("windswept_dunes"));
		}
		return getBiomesAsArray(biomeList);
	}
	
	/** Hot, dry biomes with minimal vegetation and red sand
	 * NO VANILLA SPAWNS */
	public static ResourceLocation[] getRedDesertBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		if (checkBYG()) biomeList.add(getBYG("atacama_desert"));
		return getBiomesAsArray(biomeList);
	}
	
	// DEADLANDS
	
	/** Cold biomes with mostly dead vegetation
	 * NO VANILLA SPAWNS */
	public static ResourceLocation[] getDeadlandBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		if (checkBOP()) {
			biomeList.add(getBOP("dead_forest"));
			biomeList.add(getBOP("old_growth_dead_forest"));
		}
		return getBiomesAsArray(biomeList);
	}
	
	// FOREST
	
	/** Temperate forest biomes with moderate tree cover */
	public static ResourceLocation[] getForestBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		biomeList.add(Biomes.FOREST.location());
		biomeList.add(Biomes.FLOWER_FOREST.location());
		if (checkBYG()) {
			biomeList.add(getBYG("orchard"));
			biomeList.add(getBYG("maple_taiga"));
			biomeList.add(getBYG("red_oak_forest"));
			biomeList.add(getBYG("temperate_rainforest"));
		}
		if (checkBOP()) {
			biomeList.add(getBOP("lavender_forest"));
			biomeList.add(getBOP("old_growth_woodland"));
			biomeList.add(getBOP("woodland"));
		}
		return getBiomesAsArray(biomeList);
	}
	
	/** Autumnal forest biomes with moderate tree cover
	 * NO VANILLA SPAWNS */
	public static ResourceLocation[] getAutumnalForestBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		if (checkBYG()) {
			biomeList.add(getBYG("autumnal_forest"));
			biomeList.add(getBYG("autumnal_taiga"));
		}
		if (checkBOP()) biomeList.add(getBOP("maple_woods"));
		return getBiomesAsArray(biomeList);
	}
	
	/** Temperate forest biomes with moderate birch tree cover */
	public static ResourceLocation[] getBirchForestBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		biomeList.add(Biomes.BIRCH_FOREST.location());
		biomeList.add(Biomes.OLD_GROWTH_BIRCH_FOREST.location());
		if (checkBYG()) {
			biomeList.add(getBYG("aspen_forest"));
			biomeList.add(getBYG("grove"));
		}
		return getBiomesAsArray(biomeList);
	}
	
	/** Temperate forest biomes with moderate cherry tree cover
	 * NO VANILLA SPAWNS */
	public static ResourceLocation[] getCherryForestBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		if (checkBYG()) biomeList.add(getBYG("cherry_blossom_forest"));
		if (checkBOP()) biomeList.add(getBOP("cherry_blossom_grove"));
		return getBiomesAsArray(biomeList);
	}
	
	/** Dense, dark forests with an ominous feel */
	public static ResourceLocation[] getDarkForestBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		biomeList.add(Biomes.DARK_FOREST.location());
		if (checkBYG()) {
			biomeList.add(getBYG("ebony_woods"));
			biomeList.add(getBYG("weeping_witch_forest"));
		}
		if (checkBOP()) biomeList.add(getBOP("ominous_woods"));
		return getBiomesAsArray(biomeList);
	}
	
	/** Temperate forest biomes with light tree cover 
	 * NO VANILLA SPAWNS */
	public static ResourceLocation[] getSparseForestBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		if (checkBOP()) {
			biomeList.add(getBOP("orchard"));
			biomeList.add(getBOP("origin_valley"));
			biomeList.add(getBOP("mediterranean_forest"));
		}
		return getBiomesAsArray(biomeList);
	}
	
	// FROZEN
	
	/** Cold, ice-covered open biomes */
	public static ResourceLocation[] getFrozenBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		biomeList.add(Biomes.SNOWY_PLAINS.location());
		biomeList.add(Biomes.ICE_SPIKES.location());
		if (checkBYG()) biomeList.add(getBYG("shattered_glacier"));
		if (checkBOP()) {
			biomeList.add(getBOP("muskeg"));
			biomeList.add(getBOP("snowy_fir_clearing"));
		}
		return getBiomesAsArray(biomeList);
	}
	
	/** Cold, but not snow-covered open biomes
	 * NO VANILLA SPAWNS */
	public static ResourceLocation[] getTundraBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		if (checkBYG()) biomeList.add(getBYG("cardinal_tundra"));
		if (checkBOP()) biomeList.add(getBOP("tundra"));
		return getBiomesAsArray(biomeList);
	}
	
	// MAGIC
	
	/** Magical, fantasy-themed biomes
	 * NO VANILLA SPAWNS */
	public static ResourceLocation[] getMagicBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		if (checkBYG()) {
			biomeList.add(getBYG("forgotten_forest"));
			biomeList.add(getBYG("skyris_vale"));
		}
		if (checkBOP()) biomeList.add(getBOP("mystic_grove"));
		return getBiomesAsArray(biomeList);
	}
	
	/** Mushroom-themed biomes */
	public static ResourceLocation[] getMushroomBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		biomeList.add(Biomes.MUSHROOM_FIELDS.location());
		return getBiomesAsArray(biomeList);
	}
	
	// MOUNTAIN
	
	/** Sheer, stony cliffs */
	public static ResourceLocation[] getMountainBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		biomeList.add(Biomes.STONY_PEAKS.location());
		biomeList.add(Biomes.WINDSWEPT_GRAVELLY_HILLS.location());
		if (checkBOP()) biomeList.add(getBOP("crag"));
		return getBiomesAsArray(biomeList);
	}
	
	/** Sheer, snowy cliffs */
	public static ResourceLocation[] getSnowyMountainBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		biomeList.add(Biomes.FROZEN_PEAKS.location());
		biomeList.add(Biomes.JAGGED_PEAKS.location());
		biomeList.add(Biomes.SNOWY_SLOPES.location());
		return getBiomesAsArray(biomeList);
	}
	
	// PLAINS
	
	/** Flat, warm, open, grassy areas */
	public static ResourceLocation[] getPlainsBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		biomeList.add(Biomes.PLAINS.location());
		biomeList.add(Biomes.SUNFLOWER_PLAINS.location());
		if (checkBYG()) biomeList.add(getBYG("rose_fields"));
		if (checkBOP()) {
			biomeList.add(getBOP("clover_patch"));
			biomeList.add(getBOP("grassland"));
			biomeList.add(getBOP("pasture"));
		}
		return getBiomesAsArray(biomeList);
	}
	
	/** Flat, open areas filled with flowers and some tree cover
	 * NO VANILLA SPAWNS */
	public static ResourceLocation[] getFloralFieldsBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		if (checkBYG()) {
			biomeList.add(getBYG("allium_fields"));
			biomeList.add(getBYG("amaranth_fields"));
		}
		if (checkBOP()) biomeList.add(getBOP("lavender_field"));
		return getBiomesAsArray(biomeList);
	}
	
	/** Flat, cold, open, grassy areas, often at a high elevation */
	public static ResourceLocation[] getHighlandsBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		biomeList.add(Biomes.MEADOW.location());
		biomeList.add(Biomes.WINDSWEPT_HILLS.location());
		if (checkBOP()) {
			biomeList.add(getBOP("field"));
			biomeList.add(getBOP("highland"));
			biomeList.add(getBOP("highland_moor"));
		}
		return getBiomesAsArray(biomeList);
	}
	
	/** Flat, warm, open, grassy areas with some tree cover
	 * NO VANILLA SPAWNS */
	public static ResourceLocation[] getPrairieBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		if (checkBYG()) {
			biomeList.add(getBYG("coconino_meadow"));
			biomeList.add(getBYG("prairie"));
		}
		if (checkBOP()) {
			biomeList.add(getBOP("prairie"));
			biomeList.add(getBOP("rocky_shrubland"));
			biomeList.add(getBOP("scrubland"));
			biomeList.add(getBOP("shrubland"));
			biomeList.add(getBOP("wooded_shrubland"));
		}
		return getBiomesAsArray(biomeList);
	}
	
	// SAVANNA
	
	/** Hot, grassy areas with moderate tree cover */
	public static ResourceLocation[] getSavannaBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		biomeList.add(Biomes.SAVANNA.location());
		biomeList.add(Biomes.SAVANNA_PLATEAU.location());
		biomeList.add(Biomes.WINDSWEPT_SAVANNA.location());
		if (checkBYG()) biomeList.add(getBYG("araucaria_savanna"));
		return getBiomesAsArray(biomeList);
	}
	
	/** Hot, grassy areas with minimal tree cover */
	public static ResourceLocation[] getSparseSavannaBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		biomeList.add(Biomes.SAVANNA.location());
		if (checkBOP()) biomeList.add(getBOP("lush_savanna"));
		return getBiomesAsArray(biomeList);
	}
	
	/** Hot, grassy areas with heavy tree cover
	 * NO VANILLA SPAWNS */
	public static ResourceLocation[] getDenseSavannaBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		if (checkBYG()) biomeList.add(getBYG("baobab_savanna"));
		return getBiomesAsArray(biomeList);
	}
	
	// SEASONAL
	
	/** Seasonally colored sparse forests and fields
	 * NO VANILLA SPAWNS */
	public static ResourceLocation[] getSeasonalBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		if (checkBYG()) biomeList.add(getBYG("autumnal_valley"));
		if (checkBOP()) {
			biomeList.add(getBOP("pumpkin_patch"));
			biomeList.add(getBOP("seasonal_orchard"));
		}
		return getBiomesAsArray(biomeList);
	}
	
	/** Seasonally colored dense forests
	 * NO VANILLA SPAWNS */
	public static ResourceLocation[] getDenseSeasonalBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		if (checkBOP()) biomeList.add(getBOP("seasonal_forest"));
		return getBiomesAsArray(biomeList);
	}
	
	// SEMI ARID
	
	/** Hot biomes with sparse small, scrubby vegetation
	 * NO VANILLA SPAWNS */
	public static ResourceLocation[] getSparseSemiAridBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		if (checkBYG()) biomeList.add(getBYG("firecracker_shrubland"));
		if (checkBOP()) biomeList.add(getBOP("dryland"));
		return getBiomesAsArray(biomeList);
	}
	
	/** Hot biomes with dense, small, scrubby vegetation 
	 * NO VANILLA SPAWNS */
	public static ResourceLocation[] getDenseSemiAridBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		if (checkBOP()) biomeList.add(getBOP("lush_desert"));
		return getBiomesAsArray(biomeList);
	}
	
	// SWAMP
	
	/** Flat, swampy areas with moderate tree cover */
	public static ResourceLocation[] getSwampBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		biomeList.add(Biomes.SWAMP.location());
		if (checkBOP()) {
			biomeList.add(getBOP("bayou"));
			biomeList.add(getBOP("wetland"));
		}
		return getBiomesAsArray(biomeList);
	}
	
	/** Flat, open swampy areas with minimal tree cover
	 * NO VANILLA SPAWNS */
	public static ResourceLocation[] getMarshBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		if (checkBOP()) {
			biomeList.add(getBOP("bog"));
			biomeList.add(getBOP("floodplain"));
			biomeList.add(getBOP("marsh"));
		}
		return getBiomesAsArray(biomeList);
	}
	
	/** Dense, water-saturated swampy areas with heavy tree cover
	 * NO VANILLA SPAWNS */
	public static ResourceLocation[] getWaterloggedSwampBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		if (checkBYG()) {
			biomeList.add(getBYG("bayou"));
			biomeList.add(getBYG("crag_gardens"));
			biomeList.add(getBYG("cypress_swamplands"));
			biomeList.add(getBYG("white_mangrove_marshes"));
		}
		return getBiomesAsArray(biomeList);
	}
	
	// TAIGA
	/** Dense, cold, coniferous forests */
	public static ResourceLocation[] getTaigaBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		biomeList.add(Biomes.TAIGA.location());
		if (checkBYG()) {
			biomeList.add(getBYG("black_forest"));
			biomeList.add(getBYG("borealis_grove"));
			biomeList.add(getBYG("canadian_shield"));
			biomeList.add(getBYG("coniferous_forest"));
			biomeList.add(getBYG("dacite_ridges"));
			biomeList.add(getBYG("zelkova_forest"));
		}
		if (checkBOP()) biomeList.add(getBOP("coniferous_forest"));
		return getBiomesAsArray(biomeList);
	}
	
	/** Dense, cold, coniferous forests with unusually large trees */
	public static ResourceLocation[] getMegaTaigaBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		biomeList.add(Biomes.OLD_GROWTH_PINE_TAIGA.location());
		biomeList.add(Biomes.OLD_GROWTH_SPRUCE_TAIGA.location());
		if (checkBYG()) biomeList.add(getBYG("cika_woods"));
		return getBiomesAsArray(biomeList);
	}
	
	/** Sparse, snowy, coniferous forests */
	public static ResourceLocation[] getSnowyTaigaBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		biomeList.add(Biomes.GROVE.location());
		biomeList.add(Biomes.SNOWY_TAIGA.location());
		if (checkBYG()) {
			biomeList.add(getBYG("frosted_coniferous_forest"));
			biomeList.add(getBYG("frosted_taiga"));
			biomeList.add(getBYG("howling_peaks"));
		}
		if (checkBOP()) {
			biomeList.add(getBOP("auroral_garden"));
			biomeList.add(getBOP("snowy_coniferous_forest"));
			biomeList.add(getBOP("snowy_maple_woods"));
		}
		return getBiomesAsArray(biomeList);
	}
	
	/** Sparse, cold, coniferous forests */
	public static ResourceLocation[] getSparseTaigaBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		biomeList.add(Biomes.WINDSWEPT_FOREST.location());
		if (checkBOP()) {
			biomeList.add(getBOP("fir_clearing"));
			biomeList.add(getBOP("forested_field"));
		}
		return getBiomesAsArray(biomeList);
	}
	
	/** Dense, warm, coniferous forests with redwood trees
	 * NO VANILLA SPAWNS */
	public static ResourceLocation[] getRedwoodBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		if (checkBYG()) biomeList.add(getBYG("redwood_thicket"));
		if (checkBOP()) biomeList.add(getBOP("redwood_forest"));
		return getBiomesAsArray(biomeList);
	}
	
	// TROPICAL
	
	/** Hot, dense, and humid forests
	 * NO VANILLA SPAWNS */
	public static ResourceLocation[] getTropicalBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		biomeList.add(Biomes.JUNGLE.location());
		if (checkBYG()) {
			biomeList.add(getBYG("guiana_shield"));
			biomeList.add(getBYG("tropical_rainforest"));
		}
		if (checkBOP()) biomeList.add(getBOP("rainforest"));
		return getBiomesAsArray(biomeList);
	}
	
	/** Hot, extremely dense, and humid forests */
	public static ResourceLocation[] getDenseTropicalBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		biomeList.add(Biomes.BAMBOO_JUNGLE.location());
		if (checkBYG()) biomeList.add(getBYG("jacaranda_forest"));
		if (checkBOP()) biomeList.add(getBOP("bamboo_grove"));
		return getBiomesAsArray(biomeList);
	}
	
	/** Hot, sparse, and humid forests */
	public static ResourceLocation[] getSparseTropicalBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		biomeList.add(Biomes.SPARSE_JUNGLE.location());
		if (checkBYG()) biomeList.add(getBYG("fragment_forest"));
		if (checkBOP()) {
			biomeList.add(getBOP("fungal_jungle"));
			biomeList.add(getBOP("jade_cliffs"));
			biomeList.add(getBOP("rocky_rainforest"));
			biomeList.add(getBOP("tropics"));
		}
		return getBiomesAsArray(biomeList);
	}
	
	// WASTELAND
	
	/** Warm biomes with mostly dead vegetation
	 * NO VANILLA SPAWNS */
	public static ResourceLocation[] getWastelandBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		if (checkBYG()) biomeList.add(getBYG("twilight_meadow"));
		if (checkBOP()) {
			biomeList.add(getBOP("volcanic_plains"));
			biomeList.add(getBOP("volcano"));
			biomeList.add(getBOP("wasteland"));
			biomeList.add(getBOP("wooded_wasteland"));
		}
		return getBiomesAsArray(biomeList);
	}
	
	// WATER
	
	// Standard ocean biomes
	public static ResourceLocation[] getOceanBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		biomeList.add(Biomes.OCEAN.location());
		return getBiomesAsArray(biomeList);
	}
	
	// Standard deep ocean biomes
	public static ResourceLocation[] getDeepOceanBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		biomeList.add(Biomes.DEEP_OCEAN.location());
		return getBiomesAsArray(biomeList);
	}
	
	// Cold ocean biomes
	public static ResourceLocation[] getColdOceanBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		biomeList.add(Biomes.COLD_OCEAN.location());
		return getBiomesAsArray(biomeList);
	}
	
	// Cold deep ocean biomes
	public static ResourceLocation[] getDeepColdOceanBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		biomeList.add(Biomes.DEEP_COLD_OCEAN.location());
		return getBiomesAsArray(biomeList);
	}
	
	// Warm ocean biomes
	public static ResourceLocation[] getWarmOceanBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		biomeList.add(Biomes.LUKEWARM_OCEAN.location());
		biomeList.add(Biomes.WARM_OCEAN.location());
		if (checkBYG()) {
			biomeList.add(getBYG("dead_sea"));
			biomeList.add(getBYG("lush_stacks"));
		}
		return getBiomesAsArray(biomeList);
	}
	
	// Warm deep ocean biomes
	public static ResourceLocation[] getDeepWarmOceanBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		biomeList.add(Biomes.DEEP_LUKEWARM_OCEAN.location());
		return getBiomesAsArray(biomeList);
	}
	
	// Frozen ocean biomes
	public static ResourceLocation[] getFrozenOceanBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		biomeList.add(Biomes.FROZEN_OCEAN.location());
		biomeList.add(Biomes.SNOWY_BEACH.location());
		return getBiomesAsArray(biomeList);
	}
	
	// Frozen deep ocean biomes
	public static ResourceLocation[] getDeepFrozenOceanBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		biomeList.add(Biomes.DEEP_FROZEN_OCEAN.location());
		return getBiomesAsArray(biomeList);
	}
	
	// River biomes
	public static ResourceLocation[] getRiverBiomes() {
		List<ResourceLocation> biomeList = new LinkedList<>();
		biomeList.add(Biomes.RIVER.location());
		return getBiomesAsArray(biomeList);
	}
	
	public static ResourceLocation[] getBiomesAsArray(List<ResourceLocation> biomeListIn) {
		ResourceLocation[] biomeArray = new ResourceLocation[biomeListIn.size()];
		biomeListIn.toArray(biomeArray);
		return biomeArray;
	}
	
	public static boolean checkPrehistoricSpawnRules(EntityType<?> typeIn, LevelAccessor levelAccIn, MobSpawnType spawnTypeIn, BlockPos blockPosIn, Random randomIn) {
		return checkPrehistoricSpawnRules(typeIn, levelAccIn, spawnTypeIn, blockPosIn, randomIn, false);
	}
	
	public static boolean checkPrehistoricSpawnRulesAllowDarkness(EntityType<?> typeIn, LevelAccessor levelAccIn, MobSpawnType spawnTypeIn, BlockPos blockPosIn, Random randomIn) {
		return checkPrehistoricSpawnRules(typeIn, levelAccIn, spawnTypeIn, blockPosIn, randomIn, true);
	}
	
	public static boolean checkPrehistoricSpawnRules(EntityType<?> typeIn, LevelAccessor levelAccIn, MobSpawnType spawnTypeIn, BlockPos blockPosIn, Random randomIn, boolean allowDarknessIn) {
		TagKey<Block> entityBlocks = BlockTags.create(new ResourceLocation(MODID, typeIn.getRegistryName().getPath() + "_spawnable_on"));
		if (!testRarity(typeIn, levelAccIn, blockPosIn, randomIn)) return false;
		return ((levelAccIn.getBlockState(blockPosIn.below()).is(entityBlocks) || levelAccIn.getBlockState(blockPosIn.below()).is(BlockTags.ANIMALS_SPAWNABLE_ON)) && (allowDarknessIn || levelAccIn.getRawBrightness(blockPosIn, 0) > 8));
	}
	
	public static boolean checkPrehistoricWaterSpawnRules(EntityType<?> typeIn, LevelAccessor levelAccIn, MobSpawnType spawnTypeIn, BlockPos blockPosIn, Random randomIn) {
		return checkPrehistoricWaterSpawnRules(typeIn, levelAccIn, spawnTypeIn, blockPosIn, randomIn, true);
	}
	
	public static boolean checkPrehistoricWaterAndCaveSpawnRules(EntityType<?> typeIn, LevelAccessor levelAccIn, MobSpawnType spawnTypeIn, BlockPos blockPosIn, Random randomIn) {
		if (!testRarity(typeIn, levelAccIn, blockPosIn, randomIn)) return false;
		if (levelAccIn.getFluidState(blockPosIn.below()).is(FluidTags.WATER) && levelAccIn.getBlockState(blockPosIn.above()).is(Blocks.WATER) && (levelAccIn.getBiome(blockPosIn).is(Biomes.LUSH_CAVES))) return true;
		return checkPrehistoricWaterSpawnRules(typeIn, levelAccIn, spawnTypeIn, blockPosIn, randomIn, false);
		
	}
	@SuppressWarnings("deprecation")
	public static boolean checkPrehistoricWaterSpawnRules(EntityType<?> typeIn, LevelAccessor levelAccIn, MobSpawnType spawnTypeIn, BlockPos blockPosIn, Random randomIn, boolean testRarityIn) {
		if (testRarityIn && !testRarity(typeIn, levelAccIn, blockPosIn, randomIn)) return false;
		int seaLevel = levelAccIn.getSeaLevel();
		int belowSeaLevel = seaLevel - 13;
		return blockPosIn.getY() >= belowSeaLevel && blockPosIn.getY() <= seaLevel && levelAccIn.getFluidState(blockPosIn.below()).is(FluidTags.WATER) && levelAccIn.getBlockState(blockPosIn.above()).is(Blocks.WATER);
	}
	
	public static boolean testRarity(EntityType<?> typeIn, LevelAccessor levelAccIn, BlockPos blockPosIn, Random randomIn) {
		Biome currentBiome = levelAccIn.getBiome(blockPosIn).value();
		if (levelAccIn instanceof ServerLevel serverLevel) {
			Optional<CorvicraftSpawnSet> set = SPAWNS.getSetForBiome(currentBiome);
			if (!set.isEmpty()) {
				Optional<CorvicraftSpawnEntry> entry = set.get().getEntryForType(typeIn);
				if (!entry.isEmpty() && entry.get().rarityModifiers.getLocalRarity(serverLevel, blockPosIn) <= randomIn.nextDouble()) {
					if (Config.ADVANCED_DEBUGGING.get()) LOGGER.debug("Spawn for entity type " + typeIn.getRegistryName().toString() + " failed due to rarity!");
					return false;
				}
			}
		}
		return true;
	}
	
	public static boolean checkBYG() { return ModList.get().isLoaded("byg"); }
	public static ResourceLocation getBYG(String biomeIn) { return new ResourceLocation("byg", biomeIn); }
	public static boolean checkBOP() { return ModList.get().isLoaded("biomesoplenty"); }
	public static ResourceLocation getBOP(String biomeIn) { return new ResourceLocation("biomesoplenty", biomeIn); }
	
	public static Logger getLogger() { return LOGGER; }
	
	public class Config {
		public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	    public static final ForgeConfigSpec SPEC;
	    
	    // Whether or not to log advanced debug info
	    public static final ForgeConfigSpec.ConfigValue<Boolean> ADVANCED_DEBUGGING;
	    
	    static {
			BUILDER.push("General Settings");
			ADVANCED_DEBUGGING = BUILDER.comment("Prints additional debug information to the log.",
				"In most cases this information isn't necessary and excessive debugging can generate lag.",
				"However, if you are troubleshooting or reporting an issue, advanced log info may be helpful to turn on.")
				.define("advanced_debugging", false);
			BUILDER.pop();
	        SPEC = BUILDER.build();
	    }
	}

	public static class SpawnHandler extends CorvicraftSpawnHandler {

		protected SpawnHandler(String modidIn, Map<CorvicraftSpawnSet, ResourceLocation[]> defaultSpawnsIn, List<EntityType<?>> validEntityTypesIn) {
			super(modidIn, defaultSpawnsIn, validEntityTypesIn);
		}
		
		@Override
		public boolean shouldLogDebugData() { return Config.ADVANCED_DEBUGGING.get(); }
	
		public static class Builder extends CorvicraftSpawnHandler.Builder {
		
			public Builder(String modIdIn) {
				super(modIdIn);
			}
		
			public Builder withSpawnSet(CorvicraftSpawnSet spawnSet, ResourceLocation... biomes) {
				super.withSpawnSet(spawnSet, biomes);
				return this;
			}
		
			@SuppressWarnings("unchecked")
			public Builder withSpawnSet(CorvicraftSpawnSet spawnSet, ResourceKey<Biome>... biomes) {
				super.withSpawnSet(spawnSet, biomes);
				return this;
			}
		
			public Builder withEntityTypes(EntityType<?>... entityTypesIn) {
				super.withEntityTypes(entityTypesIn);
				return this;
			}
		
			public SpawnHandler build() {
				return new SpawnHandler(this.modid, this.defaultSpawnSets, this.validEntityTypes);
			}
		}
	}
}
