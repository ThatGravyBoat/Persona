package tech.thatgravyboat.persona.client.renderer;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;

public class CustomPlayerModel<T extends LivingEntity> extends PlayerModel<T> {
    public CustomPlayerModel(ModelPart root, boolean thinArms) {
        super(root, thinArms);
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of(this.head, this.hat);
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(
                this.body, this.jacket,
                this.rightArm, this.rightSleeve,
                this.rightLeg, this.rightPants,
                this.leftLeg, this.leftPants,
                this.leftArm, this.leftSleeve);
    }
}
