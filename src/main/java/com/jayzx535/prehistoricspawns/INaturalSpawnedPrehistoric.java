package com.jayzx535.prehistoricspawns;

/* This is used by mixins in order to determine if a dinosaur is naturally spawned.
 * While this is mostly needed for water spawns, we apply it to all prehistoric entities. */
public interface INaturalSpawnedPrehistoric {
	public abstract boolean isNaturalSpawn();
}
