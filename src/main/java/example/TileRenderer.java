package example;

import com.bug1312.javajson.javajson.IUseJavaJSON;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;

/** This is registered just like a normal BE renderer, the only difference is the registerJavaJSON() in the constructor and the getModel.renderToBuffer**/
public class TileRenderer implements IUseJavaJSON, BlockEntityRenderer<FurnaceBlockEntity> {

    public TileRenderer() {
        registerJavaJSON(ResourceLocation.parse("javajson:models/furnace.json"));
    }

    @Override
    public void render(FurnaceBlockEntity furnaceBlockEntity, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int i1) {
        poseStack.pushPose();
        getModel().renderToBuffer(poseStack, multiBufferSource.getBuffer(getRenderType()), i, i1, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }
}
