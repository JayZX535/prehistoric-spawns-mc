package com.jayzx535.prehistoricspawns.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.github.teamfossilsarcheology.fossil.entity.prehistoric.base.PrehistoricSwimming;
import com.jayzx535.prehistoricspawns.INaturalSpawnedPrehistoric;

import net.minecraft.world.entity.animal.Animal;

@Mixin(Animal.class)
public class AnimalMixin {
	/* Adds a special clause for prehistoric water mobs that are identified as having spawned naturally to allow them to despawn.
	 * Otherwise, they won't despawn naturally and, as water mobs continue to spawn in endlessly, this will lead to worlds being flooded with fish and becoming unplayably laggy. */
	@Inject(method = "Lnet/minecraft/world/entity/animal/Animal;removeWhenFarAway(D)Z", at = @At("HEAD"), cancellable = true)
	private void removeWhenFarAway(double distanceIn, CallbackInfoReturnable<Boolean> callbackReturnableIn) {
		if ((Animal) (Object) this instanceof PrehistoricSwimming swimming && this instanceof INaturalSpawnedPrehistoric naturalPrehistoric && naturalPrehistoric.isNaturalSpawn()) callbackReturnableIn.setReturnValue(true);
		return;
	}
}
