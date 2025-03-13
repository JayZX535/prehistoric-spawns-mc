package com.jayzx535.prehistoricspawns;

import javax.annotation.Nullable;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = PrehistoricSpawns.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PrehistoricSpawnsData {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
    	DataGenerator generator = event.getGenerator();
    	ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
    	if (event.includeServer()) {
    		PrehistoricBlockTags blockTags = new PrehistoricBlockTags(generator, existingFileHelper);
        	generator.addProvider(blockTags);
    	}
    }
    
    /* The mod is configured to automatically check for a blocktag with a list of valid spawn blocks correspawning to the creature's name.
     * In most cases, this isn't actually needed, because most dinosaurs spawn in areas with blocks like grass, which are enabled for normal animal spawns.
     * However, for prehistoric creatures which spawn in nontraditional environments i.e. on sand in the desert, those blocks should be added to their spawn tag. */
    public static class PrehistoricBlockTags extends BlockTagsProvider {
    	
    	public static final TagKey<Block> BEACH_SPAWN_BLOCKS = BlockTags.create(new ResourceLocation(PrehistoricSpawns.MODID, "beach_spawn_blocks"));
    	public static final TagKey<Block> BADLANDS_SPAWN_BLOCKS = BlockTags.create(new ResourceLocation(PrehistoricSpawns.MODID, "badlands_spawn_blocks"));
    	public static final TagKey<Block> DESERT_SPAWN_BLOCKS = BlockTags.create(new ResourceLocation(PrehistoricSpawns.MODID, "desert_spawn_blocks"));
    	public static final TagKey<Block> MOUNTAIN_SPAWN_BLOCKS = BlockTags.create(new ResourceLocation(PrehistoricSpawns.MODID, "mountain_spawn_blocks"));

    	public static final TagKey<Block> ALLOSAURUS_SPAWNABLE_ON = BlockTags.create(new ResourceLocation(PrehistoricSpawns.MODID, "allosaurus_spawnable_on"));
    	public static final TagKey<Block> BRACHIOSAURUS_SPAWNABLE_ON = BlockTags.create(new ResourceLocation(PrehistoricSpawns.MODID, "brachiosaurus_spawnable_on"));
    	public static final TagKey<Block> CITIPATI_SPAWNABLE_ON = BlockTags.create(new ResourceLocation(PrehistoricSpawns.MODID, "citipati_spawnable_on"));
    	public static final TagKey<Block> DILOPHOSAURUS_SPAWNABLE_ON = BlockTags.create(new ResourceLocation(PrehistoricSpawns.MODID, "dilophosaurus_spawnable_on"));
    	public static final TagKey<Block> DIMORPHODON_SPAWNABLE_ON = BlockTags.create(new ResourceLocation(PrehistoricSpawns.MODID, "dimorphodon_spawnable_on"));
    	public static final TagKey<Block> DODO_SPAWNABLE_ON = BlockTags.create(new ResourceLocation(PrehistoricSpawns.MODID, "dodo_spawnable_on"));
    	public static final TagKey<Block> GALLIMIMUS_SPAWNABLE_ON = BlockTags.create(new ResourceLocation(PrehistoricSpawns.MODID, "gallimimus_spawnable_on"));
    	public static final TagKey<Block> KELENKEN_SPAWNABLE_ON = BlockTags.create(new ResourceLocation(PrehistoricSpawns.MODID, "kelenken_spawnable_on"));
    	public static final TagKey<Block> MEGALANIA_SPAWNABLE_ON = BlockTags.create(new ResourceLocation(PrehistoricSpawns.MODID, "megalania_spawnable_on"));
    	public static final TagKey<Block> PACHYCEPHALOSAURUS_SPAWNABLE_ON = BlockTags.create(new ResourceLocation(PrehistoricSpawns.MODID, "pachycephalosaurus_spawnable_on"));
    	public static final TagKey<Block> PROTOCERATOPS_SPAWNABLE_ON = BlockTags.create(new ResourceLocation(PrehistoricSpawns.MODID, "protoceratops_spawnable_on"));
    	public static final TagKey<Block> PTERANODON_SPAWNABLE_ON = BlockTags.create(new ResourceLocation(PrehistoricSpawns.MODID, "pteranodon_spawnable_on"));
    	public static final TagKey<Block> STEGOSAURUS_SPAWNABLE_ON = BlockTags.create(new ResourceLocation(PrehistoricSpawns.MODID, "stegosaurus_spawnable_on"));
    	public static final TagKey<Block> THERIZINOSAURUS_SPAWNABLE_ON = BlockTags.create(new ResourceLocation(PrehistoricSpawns.MODID, "therizinosaurus_spawnable_on"));
    	public static final TagKey<Block> VELOCIRAPTOR_SPAWNABLE_ON = BlockTags.create(new ResourceLocation(PrehistoricSpawns.MODID, "velociraptor_spawnable_on"));
    	
    	public PrehistoricBlockTags(DataGenerator dataGeneratorIn, @Nullable ExistingFileHelper existingFileHelperIn) {
    		super(dataGeneratorIn, PrehistoricSpawns.MODID, existingFileHelperIn);
    	}
    	
    	@Override
        protected void addTags() {
    		this.tag(BEACH_SPAWN_BLOCKS).addTag(Tags.Blocks.SAND);
    		this.tag(BADLANDS_SPAWN_BLOCKS).addTag(BlockTags.TERRACOTTA);
    		this.tag(DESERT_SPAWN_BLOCKS).addTag(Tags.Blocks.SAND).addTag(Tags.Blocks.SANDSTONE);
    		this.tag(MOUNTAIN_SPAWN_BLOCKS).addTag(Tags.Blocks.STONE).addTag(Tags.Blocks.GRAVEL);
    		
    		this.tag(ALLOSAURUS_SPAWNABLE_ON).addTag(DESERT_SPAWN_BLOCKS);
    		this.tag(BRACHIOSAURUS_SPAWNABLE_ON).addTag(DESERT_SPAWN_BLOCKS);
    		this.tag(CITIPATI_SPAWNABLE_ON).addTag(BADLANDS_SPAWN_BLOCKS).addTag(DESERT_SPAWN_BLOCKS);
    		this.tag(DILOPHOSAURUS_SPAWNABLE_ON).addTag(BEACH_SPAWN_BLOCKS).addTag(BADLANDS_SPAWN_BLOCKS).addTag(DESERT_SPAWN_BLOCKS);
    		this.tag(DIMORPHODON_SPAWNABLE_ON).addTag(BEACH_SPAWN_BLOCKS);
    		this.tag(DODO_SPAWNABLE_ON).addTag(BEACH_SPAWN_BLOCKS);
    		this.tag(GALLIMIMUS_SPAWNABLE_ON).addTag(DESERT_SPAWN_BLOCKS);
    		this.tag(KELENKEN_SPAWNABLE_ON).addTag(MOUNTAIN_SPAWN_BLOCKS);
    		this.tag(MEGALANIA_SPAWNABLE_ON).addTag(BADLANDS_SPAWN_BLOCKS).addTag(DESERT_SPAWN_BLOCKS);
    		this.tag(PACHYCEPHALOSAURUS_SPAWNABLE_ON).addTag(BEACH_SPAWN_BLOCKS);
    		this.tag(PROTOCERATOPS_SPAWNABLE_ON).addTag(BADLANDS_SPAWN_BLOCKS).addTag(DESERT_SPAWN_BLOCKS);
    		this.tag(PTERANODON_SPAWNABLE_ON).addTag(BEACH_SPAWN_BLOCKS);
    		this.tag(STEGOSAURUS_SPAWNABLE_ON).addTag(DESERT_SPAWN_BLOCKS);
    		this.tag(THERIZINOSAURUS_SPAWNABLE_ON).addTag(DESERT_SPAWN_BLOCKS);
    		this.tag(VELOCIRAPTOR_SPAWNABLE_ON).addTag(DESERT_SPAWN_BLOCKS);
    	}
    }

}
