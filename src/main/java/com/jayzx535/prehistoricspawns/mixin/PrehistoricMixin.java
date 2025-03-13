package com.jayzx535.prehistoricspawns.mixin;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.teamfossilsarcheology.fossil.entity.prehistoric.base.Prehistoric;
import com.jayzx535.prehistoricspawns.INaturalSpawnedPrehistoric;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

@Mixin(Prehistoric.class)
public abstract class PrehistoricMixin extends TamableAnimal implements INaturalSpawnedPrehistoric {
	
	protected PrehistoricMixin(EntityType<? extends TamableAnimal> tamableIn, Level levelIn) {
		super(tamableIn, levelIn);
	}

	@Unique
	protected boolean isNaturalSpawn = false;
	
	/* Saves this creature's naturally spawned status to NBT */
	@Inject(method = "Lcom/github/teamfossilsarcheology/fossil/entity/prehistoric/base/Prehistoric;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("HEAD"))
    public void addAdditionalSaveData(CompoundTag compound, CallbackInfo callbackIn) {
		compound.putBoolean("IsNaturalSpawn", this.isNaturalSpawn);
		return;
    }

	/* Reads this creature's naturally spawned status from NBT */
	@Inject(method = "Lcom/github/teamfossilsarcheology/fossil/entity/prehistoric/base/Prehistoric;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("HEAD"))
    public void readAdditionalSaveData(CompoundTag compound, CallbackInfo callbackIn) {
		this.isNaturalSpawn = compound.getBoolean("IsNaturalSpawn");
        return;
    }
	
	/* If this prehistoric creature has spawned in naturally, we mark it as such.
	 * This distinction is necessary to allow despawning in specific edge cases, especially for natural water spawns. */
	@Inject(method = "Lcom/github/teamfossilsarcheology/fossil/entity/prehistoric/base/Prehistoric;finalizeSpawn(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/world/DifficultyInstance;Lnet/minecraft/world/entity/MobSpawnType;Lnet/minecraft/world/entity/SpawnGroupData;Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/entity/SpawnGroupData;", at = @At("TAIL"))
	public void finalizeSpawn(ServerLevelAccessor levelIn, DifficultyInstance difficultyIn, MobSpawnType reasonIn, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag, CallbackInfoReturnable<SpawnGroupData> callbackReturnableIn) {
		this.isNaturalSpawn = reasonIn == MobSpawnType.CHUNK_GENERATION || reasonIn == MobSpawnType.NATURAL;
		if (this.isNaturalSpawn) this.persistenceRequired = false; // F&A enables persistence on all mobs by default, which will also prevent despawning in cases where we need it to be allowed.
		return;
	}

	@Override
	public boolean isNaturalSpawn() { return this.isNaturalSpawn; }
}
