package com.astradia.api.player;

import java.util.Map;

public class BodyProportionsConfig {

    public BodyPartProportion head;
    public BodyPartProportion torso;
    public BodyPartProportion leftArm;
    public BodyPartProportion rightArm;
    public BodyPartProportion leftLeg;
    public BodyPartProportion rightLeg;
    public BodyPartProportion width;
    public BodyPartProportion height;

    public BodyProportionsConfig(
            BodyPartProportion head,
            BodyPartProportion torso,
            BodyPartProportion leftArm,
            BodyPartProportion rightArm,
            BodyPartProportion leftLeg,
            BodyPartProportion rightLeg,
            BodyPartProportion width,
            BodyPartProportion height
    ) {
        this.head = head;
        this.torso = torso;
        this.leftArm = leftArm;
        this.rightArm = rightArm;
        this.leftLeg = leftLeg;
        this.rightLeg = rightLeg;
        this.width = width;
        this.height = height;
    }

    public Map<String, BodyPartProportion> getAllParts() {
        return Map.of(
                "Head", head,
                "Torso", torso,
                "Left Arm", leftArm,
                "Right Arm", rightArm,
                "Left Leg", leftLeg,
                "Right Leg", rightLeg,
                "Width", width,
                "Height", height
        );
    }
}
