package com.bug1312.javajson.javajson;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryAccess.ImmutableRegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ReflectionBuddy {
    /**
     * Gets a getter-like object for a reflective field. Only to be used for obfuscatable vanilla minecraft fields
     *
     * @param <FIELDHOLDER>    The type of the object containing the field
     * @param <FIELDTYPE>      The type of the values the field would contain
     * @param fieldHolderClass The class of the object containing the field
     * @param fieldName        The SRG (intermediary-obfuscated) name of the field
     * @return A getter for the field
     */
    public static <FIELDHOLDER, FIELDTYPE> Function<FIELDHOLDER, FIELDTYPE> getInstanceFieldGetter(Class<FIELDHOLDER> fieldHolderClass, String fieldName) {
        // forge's ORH is needed to reflect into vanilla minecraft java
        Field field = ObfuscationReflectionHelper.findField(fieldHolderClass, fieldName);
        return getInstanceFieldGetter(field);
    }

    public static <FIELDHOLDER, FIELDTYPE> MutableInstanceField<FIELDHOLDER, FIELDTYPE> getInstanceField(Class<FIELDHOLDER> fieldHolderClass, String fieldName) {
        return new MutableInstanceField<>(fieldHolderClass, fieldName);
    }

    @SuppressWarnings("unchecked")
    // throws ClassCastException if the types are wrong, the returned function can also throw RuntimeException
    private static <FIELDHOLDER, FIELDTYPE> Function<FIELDHOLDER, FIELDTYPE> getInstanceFieldGetter(Field field) {
        return instance -> {
            try {
                return (FIELDTYPE) (field.get(instance));
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static class MutableInstanceField<FIELDHOLDER, FIELDTYPE> {
        private final Function<FIELDHOLDER, FIELDTYPE> getter;
        private final BiConsumer<FIELDHOLDER, FIELDTYPE> setter;

        private MutableInstanceField(Class<FIELDHOLDER> fieldHolderClass, String fieldName) {
            Field field = ObfuscationReflectionHelper.findField(fieldHolderClass, fieldName);
            this.getter = getInstanceFieldGetter(field);
            this.setter = getInstanceFieldSetter(field);
        }

        /**
         * Returns the current value of the field in a given instance
         *
         * @param instance The object containing the instance field to get the value from
         * @return The value in that field
         */
        public FIELDTYPE get(FIELDHOLDER instance) {
            return this.getter.apply(instance);
        }

        /**
         * Sets an object's field to the given value
         *
         * @param instance The object containing the instance field to set the value in
         * @param value    The value to set
         */
        public void set(FIELDHOLDER instance, FIELDTYPE value) {
            this.setter.accept(instance, value);
        }

        // the returned function throws RuntimeException if the types are wrong
        private static <FIELDHOLDER, FIELDTYPE> BiConsumer<FIELDHOLDER, FIELDTYPE> getInstanceFieldSetter(Field field) {
            return (instance, value) -> {
                try {
                    field.set(instance, value);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            };
        }
    }

    public static class ModelPartAccess {
        public static final Function<ModelPart, List<ModelPart.Cube>> cubes =
                getInstanceFieldGetter(ModelPart.class, "f_104212_");
    }
}