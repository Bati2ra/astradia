package com.astradia.player.body;

import java.util.Collection;
import java.util.EnumSet;
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

    public static BodyProportionsConfig createDefaultConfig() {
        // HEAD: x, y, z ligados
        BodyPartProportion head = new BodyPartProportion(
                EnumSet.of(BodyPartProportion.Axis.X, BodyPartProportion.Axis.Y, BodyPartProportion.Axis.Z),
                Map.of(
                        BodyPartProportion.Axis.X, new BodyPartProportion.Range(0.8f, 1.2f),
                        BodyPartProportion.Axis.Y, new BodyPartProportion.Range(0.8f, 1.2f),
                        BodyPartProportion.Axis.Z, new BodyPartProportion.Range(0.8f, 1.2f)
                ),
                Map.of(
                        BodyPartProportion.Axis.X, 1.0f,
                        BodyPartProportion.Axis.Y, 1.0f,
                        BodyPartProportion.Axis.Z, 1.0f
                )
        );

        // TORSO: xz ligados, y separado
        BodyPartProportion torso = new BodyPartProportion(
                EnumSet.of(BodyPartProportion.Axis.X, BodyPartProportion.Axis.Z),
                Map.of(
                        BodyPartProportion.Axis.X, new BodyPartProportion.Range(0.6f, 1.6f),
                        BodyPartProportion.Axis.Y, new BodyPartProportion.Range(0.5f, 1.5f),
                        BodyPartProportion.Axis.Z, new BodyPartProportion.Range(0.6f, 1.6f)
                ),
                Map.of(
                        BodyPartProportion.Axis.X, 1.0f,
                        BodyPartProportion.Axis.Y, 1.0f,
                        BodyPartProportion.Axis.Z, 1.0f
                )
        );

        // LEFT ARM: xz ligados, y separado
        BodyPartProportion leftArm = new BodyPartProportion(
                EnumSet.of(BodyPartProportion.Axis.X, BodyPartProportion.Axis.Z),
                Map.of(
                        BodyPartProportion.Axis.X, new BodyPartProportion.Range(0.7f, 1.2f),
                        BodyPartProportion.Axis.Y, new BodyPartProportion.Range(0.5f, 1.6f),
                        BodyPartProportion.Axis.Z, new BodyPartProportion.Range(0.7f, 1.2f)
                ),
                Map.of(
                        BodyPartProportion.Axis.X, 1.0f,
                        BodyPartProportion.Axis.Y, 1.0f,
                        BodyPartProportion.Axis.Z, 1.0f
                )
        );

        // RIGHT ARM: igual que leftArm
        BodyPartProportion rightArm = new BodyPartProportion(
                leftArm.getLinkedAxes(),
                leftArm.ranges,
                leftArm.values
        );

        // LEFT LEG
        BodyPartProportion leftLeg = new BodyPartProportion(
                EnumSet.of(BodyPartProportion.Axis.X, BodyPartProportion.Axis.Z),
                Map.of(
                        BodyPartProportion.Axis.X, new BodyPartProportion.Range(0.8f, 1.2f),
                        BodyPartProportion.Axis.Y, new BodyPartProportion.Range(0.5f, 1.8f),
                        BodyPartProportion.Axis.Z, new BodyPartProportion.Range(0.8f, 1.2f)
                ),
                Map.of(
                        BodyPartProportion.Axis.X, 1.0f,
                        BodyPartProportion.Axis.Y, 1.0f,
                        BodyPartProportion.Axis.Z, 1.0f
                )
        );

        // RIGHT LEG: igual que leftLeg
        BodyPartProportion rightLeg = new BodyPartProportion(
                leftLeg.getLinkedAxes(),
                leftLeg.ranges,
                leftLeg.values
        );

        // WIDTH: xz ligados
        BodyPartProportion width = new BodyPartProportion(
                EnumSet.of(BodyPartProportion.Axis.X, BodyPartProportion.Axis.Z),
                Map.of(
                        BodyPartProportion.Axis.X, new BodyPartProportion.Range(0.6f, 1.6f),
                        BodyPartProportion.Axis.Z, new BodyPartProportion.Range(0.6f, 1.6f)
                ),
                Map.of(
                        BodyPartProportion.Axis.X, 1.0f,
                        BodyPartProportion.Axis.Z, 1.0f
                )
        );

        // HEIGHT: solo Y
        BodyPartProportion height = new BodyPartProportion(
                EnumSet.of(BodyPartProportion.Axis.Y),
                Map.of(
                        BodyPartProportion.Axis.Y, new BodyPartProportion.Range(0.5f, 2.0f)
                ),
                Map.of(
                        BodyPartProportion.Axis.Y, 1.0f
                )
        );

        return new BodyProportionsConfig(head, torso, leftArm, rightArm, leftLeg, rightLeg, width, height);
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
