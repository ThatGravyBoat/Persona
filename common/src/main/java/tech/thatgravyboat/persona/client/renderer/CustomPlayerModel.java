package tech.thatgravyboat.persona.client.renderer;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;

public class CustomPlayerModel<T extends LivingEntity> extends PlayerEntityModel<T> {
    public CustomPlayerModel(ModelPart root, boolean thinArms) {
        super(root, thinArms);
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of(this.head, this.hat);
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(
                this.body, this.jacket,
                this.rightArm, this.rightSleeve,
                this.rightLeg, this.rightPants,
                this.leftLeg, this.leftPants,
                this.leftArm, this.leftSleeve);
    }
}
