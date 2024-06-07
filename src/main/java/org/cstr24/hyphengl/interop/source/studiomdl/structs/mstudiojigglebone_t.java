package org.cstr24.hyphengl.interop.source.studiomdl.structs;

import org.cstr24.hyphengl.interop.source.StructWrapper;
import org.cstr24.hyphengl.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public class mstudiojigglebone_t extends BaseStruct implements StructWrapper<mstudiojigglebone_t> {
    public static final int JIGGLE_IS_FLEXIBLE = 0x01;
    public static final int JIGGLE_IS_RIGID = 0x02;
    public static final int JIGGLE_HAS_YAW_CONSTRAINT = 0x04;
    public static final int JIGGLE_HAS_PITCH_CONSTRAINT = 0x08;
    public static final int JIGGLE_HAS_ANGLE_CONSTRAINT = 0x10;
    public static final int JIGGLE_HAS_LENGTH_CONSTRAINT = 0x20;
    public static final int JIGGLE_HAS_BASE_SPRING = 0x40;
    public static final int JIGGLE_IS_BOING = 0x80;


    public int flags;

    public float length; //how far from bone base, along bone, is the tip
    public float tipmass;

    public float yawStiffness;
    public float yawDamping;
    public float pitchStiffness;
    public float pitchDamping;
    public float alongStiffness;
    public float alongDamping;

    public float angleLimit;

    public float minYaw;
    public float maxYaw;
    public float yawFriction;
    public float yawBounce;

    public float minPitch;
    public float maxPitch;
    public float pitchFriction;
    public float pitchBounce;

    public float baseMass;
    public float baseStiffness;
    public float baseDamping;
    public float baseMinLeft;
    public float baseMaxLeft;
    public float baseLeftFriction;
    public float baseMinUp;
    public float baseMaxUp;
    public float baseUpFriction;
    public float baseMinForward;
    public float baseMaxForward;
    public float baseForwardFriction;

    public float boingImpactSpeed;
    public float boingImpactAngle;
    public float boingDampingRate;
    public float boingFrequency;
    public float boingAmplitude;


    @Override
    public mstudiojigglebone_t parse(ByteBuffer in) {
        flags = in.getInt();

        length = in.getFloat();
        tipmass = in.getFloat();

        yawStiffness = in.getFloat();
        yawDamping = in.getFloat();
        pitchStiffness = in.getFloat();
        pitchDamping = in.getFloat();
        alongStiffness = in.getFloat();
        alongDamping = in.getFloat();

        angleLimit = in.getFloat();

        minYaw = in.getFloat();
        maxYaw = in.getFloat();
        yawFriction = in.getFloat();
        yawBounce = in.getFloat();

        minPitch = in.getFloat();
        maxPitch = in.getFloat();
        pitchFriction = in.getFloat();
        pitchBounce = in.getFloat();

        baseMass = in.getFloat();
        baseStiffness = in.getFloat();
        baseDamping = in.getFloat();
        baseMinLeft = in.getFloat();
        baseMaxLeft = in.getFloat();
        baseLeftFriction = in.getFloat();
        baseMinUp = in.getFloat();
        baseMaxUp = in.getFloat();
        baseUpFriction = in.getFloat();
        baseMinForward = in.getFloat();
        baseMaxForward = in.getFloat();
        baseForwardFriction = in.getFloat();

        boingImpactSpeed = in.getFloat();
        boingImpactAngle = in.getFloat();
        boingDampingRate = in.getFloat();
        boingFrequency = in.getFloat();
        boingAmplitude = in.getFloat();

        return this;
    }

    @Override
    public int sizeOf() {
        return 0;
    }
}
