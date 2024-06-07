package org.cstr24.hyphengl.interop.source;

import org.cstr24.hyphengl.interop.source.structs.vector_t;
import org.cstr24.hyphengl.interop.source.studiomdl.structs.Quaternion;
import org.joml.*;

import java.lang.Math;
import java.util.Arrays;

import static org.cstr24.hyphengl.interop.source.studiomdl.StudioModel.FLT_EPSILON;

//TODO, now that I've made a QuaternionfX class with some functions I like to use (and a Vector3fX class is probably imminent), I should move some of these functions into those classes and make them instance methods.

public class SourceMath {
    public static void angleQuaternion(Vector3f angles, QuaternionfX outQuat){
        float sr, sp, sy, cr, cp, cy;

        //sincos
        sy = (float) Math.sin(angles.x() * 0.5f);
        cy = (float) Math.cos(angles.x() * 0.5f);
        //sincos
        sp = (float) Math.sin(angles.y() * 0.5f);
        cp = (float) Math.cos(angles.y() * 0.5f);
        //sincos
        sr = (float) Math.sin(angles.z() * 0.5f);
        cr = (float) Math.cos(angles.z() * 0.5f);

        if (1 == 1){
            outQuat.x = sy * cp * cr - cy * sp * sr;
            outQuat.y = cy * sp * cr + sy * cp * sr;
            outQuat.z = cy * cp * sr - sy * sp * cr;
            outQuat.w = cy * cp * cr + sy * sp * sr;
        }else{
            float srXcp = sr * cp;
            float crXsp = cr * sp;
            outQuat.x = srXcp * cy - crXsp * sy;
            outQuat.y = crXsp * cy + srXcp * sy;

            float crXcp = cr * cp;
            float srXsp = sr * sp;
            outQuat.z = crXcp * sy - srXsp * cy;
            outQuat.w = crXcp * cy + srXsp * sy;

        }


        /*var qat = new Quaternionf().rotateXYZ(angles.x, angles.y, angles.z);
        outQuat.set(qat.x, qat.y, qat.z, qat.w);*/

        //System.out.println("is this being reached? anglequaternion");
    }

    public static Vector3fX vectorRotate(Vector3fX pos, QuaternionfX q, Vector3fX out){
        float a = q.x;
        float n = q.y;
        float r = q.z;
        float s = q.w;
        var o = pos.x;
        var A = pos.y;
        var l = pos.z;

        float h = n * l - r * A;
        float c = r * o - a * l;
        float u = a * A - n * o;
        float g = n * u - r * c;
        float d = r * h - a * u;
        float m = a * c - n * h;
        float p = 2 * s;

        h *= p;
        c *= p;
        u *= p;
        g *= 2;
        d *= 2;
        m *= 2;

        out.x = o + h + g;
        out.y = A + c + d;
        out.z = l + u + m;

        return out;
    }

    public static void quaternionBlend(QuaternionfX p, QuaternionfX q, float t, QuaternionfX qt){
        QuaternionfX q2 = new QuaternionfX();
        quaternionAlign(p, q, q2);
        quaternionBlendNoAlign(p, q2, t, qt);
    }

    public static void quaternionAlign(QuaternionfX p, QuaternionfX q, QuaternionfX qt){
        float a = 0;
        float b = 0;
        for (int i = 0; i < 4; i++){
            a += (p.component(i) - q.component(i)) * (p.component(i) - q.component(i));
            b += (p.component(i) + q.component(i)) * (p.component(i) + q.component(i));
        }
        if (a > b) {
            for (int i = 0; i < 4; i++){
                qt.setComponent(i, -q.component(i));
            }
        }else if (qt != q){ //'&qt != &q' i have confirmed means that in the original code, we're checking '&qt and &q' are not referencing the same memory address (as opposed to being equal in that they're sharing the same value)
            qt.set(q);
        }
    }

    public static void quaternionBlendNoAlign(QuaternionfX p, QuaternionfX q, float t, QuaternionfX qt){
        float sclp = 1.0f - t;
        qt.x = sclp * p.x + t * q.x;
        qt.y = sclp * p.y + t * q.y;
        qt.z = sclp * p.z + t * q.z;
        qt.w = sclp * p.w + t * q.w;
        quaternionNormalize(qt);
    }

    public static void quaternionNormalize(QuaternionfX qt){
        float radius, iRadius;

        radius = qt.x * qt.x + qt.y * qt.y + qt.z * qt.z + qt.w * qt.w;

        if (radius > 0){
            radius = (float) Math.sqrt(radius);
            iRadius = 1.0f / radius;

            qt.w *= iRadius;
            qt.z *= iRadius;
            qt.y *= iRadius;
            qt.x *= iRadius;
        }
    }

    public static void quaternionSlerp(QuaternionfX q1, QuaternionfX q2, float t, QuaternionfX dest){
        QuaternionfX tmp = new QuaternionfX();
        quaternionAlign(q1, q2, tmp);
        quaternionSlerpNoAlign(q1, tmp, t, dest);
    }
    public static void quaternionSlerpNoAlign(QuaternionfX q1, QuaternionfX q2, float t, QuaternionfX dest){
        float omega, cosom, sinom, sclp, sclq;
        cosom = q1.x * q2.x + q1.y * q2.y + q1.z * q2.z + q1.w * q2.w;

        if ((1.0f + cosom) > Math.ulp(0)){
            if ((1.0f - cosom) > Math.ulp(0)) {
                omega = (float) Math.acos(cosom);
                sinom = (float) Math.sin(omega);
                sclp = (float) (Math.sin((1.0f - t) * omega) / sinom);
                sclq = (float) (Math.sin(t * omega) / sinom);
            }else{
                sclp = 1.0f - t;
                sclq = t;
            }
            for (int i = 0; i < 4; i++){
                dest.setComponent(i, sclp * q1.component(i) + sclq * q2.component(i));
            }
        }else{
            dest.x = -q2.y;
            dest.y = q2.x;
            dest.z = -q2.w;
            dest.w = q2.z;
            sclp = (float) (Math.sin(1.0f - t) * (0.5f * Math.PI));
            sclq = (float) Math.sin(t * (0.5f * Math.PI));
            for (int i = 0; i < 3; i++){
                dest.setComponent(i, sclp * q1.component(i) + sclq * dest.component(i));
            }
        }
    }

    public static void quaternionMA(QuaternionfX p, float s, QuaternionfX q, QuaternionfX qt){
        QuaternionfX p1 = new QuaternionfX(), q1 = new QuaternionfX();

        quaternionScale(q, s, q1); //scale q by s and store into q1
        quaternionMult(p, q1, p1); //multiply p by q1 and store into p1
        quaternionNormalize(p1); //normalize p1

        qt.set(p1);
    }

    public static void quaternionSM(float s, QuaternionfX p, QuaternionfX q, QuaternionfX qt){
        QuaternionfX p1 = new QuaternionfX(), q1 = new QuaternionfX();
        p.scale(s, p1);

        quaternionScale(p, s, p1);
        quaternionMult(p1, q, q1);
        quaternionNormalize(q1);

        qt.set(q1);
    }

    public static float quaternionDotXYZ(QuaternionfX a, QuaternionfX b){
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    public static void quaternionMult(QuaternionfX q1, QuaternionfX q2, QuaternionfX out){
        float a = q1.x;
        float n = q1.y;
        float r = q1.z;
        float s = q1.w;
        float o = q2.x;
        float A = q2.y;
        float l = q2.z;
        float h = q2.w;

        out.x = a * h + s * o + n * l - r * A;
        out.y = n * h + s * A + r * o - a * l;
        out.z = r * h + s * l + a * A - n * o;
        out.w = s * h - a * o - n * A - r * l;
    }

    public static void quaternionScale(QuaternionfX p, float scale, QuaternionfX qt){
        float r;
        float sinom = (float) Math.sqrt(quaternionDotXYZ(p, p));
        sinom = Math.min(sinom, 1.0f);

        float sinsom = (float) Math.sin(Math.asin(sinom) * scale);

        scale = sinsom / (sinsom + FLT_EPSILON);
        quaternionScaleXYZ(p, scale, qt);

        r = 1.0f - sinsom * sinsom;

        if (r < 0.0f){
            r = 0.0f;
        }
        r = (float) Math.sqrt(r);


        //System.out.println("epsilon float: " + FLT_EPSILON + " vs math.ulp: " + Math.ulp(1.0f));

        if (p.w < 0) {
            qt.w = -r;
        }else{
            qt.w = r;
        }
    }

    public static void quaternionScaleXYZ(QuaternionfX in, float scale, QuaternionfX result){
        result.set(in.x * scale, in.y * scale, in.z * scale, in.w);
    }

    public static void quaternionIdentityBlend(QuaternionfX p, float t, QuaternionfX qt){
        float sclp = 1.0f - t;
        qt.x = p.x * sclp;
        qt.y = p.y * sclp;
        qt.z = p.z * sclp;
        if (qt.w < 0.0f){
            qt.w = p.w * sclp - t;
        }else{
            qt.w = p.w * sclp + t;
        }
        quaternionNormalize(qt);
    }

    public static Matrix4f quaternionMatrix(Quaternionf q, Vector3f pos, Matrix4f matrix, float scale){
        /*matrix.identity().rotation(new Quaternionf().set(q.x, q.y, q.w, q.z)); //investigate difference between 'rotation' and 'rotate' based on same starting point of 'identity()'
        matrix.translate(pos.getX(), pos.getY(), pos.getZ());*/
        //matrix.translationRotateScale(pos.toVec3f(), q.toQuaternionf(), 1.0f);

        matrix.identity();

        //matrix.translationRotateScale(pos, q, scale);
        //System.out.println("QuaternionMatrix: matrix.translationRotate result - ");
        //System.out.println(matrix);

        //and now try to copy whatever quaternionmatrix does, see if the result is different.

        float XPlusX = q.x + q.x;
        float YPlusY = q.y + q.y;
        float ZPlusZ = q.z + q.z;

        float XTimesXPX = q.x * XPlusX; //x times x plus x
        float XTimesYPY = q.x * YPlusY; //x times y plus y
        float XTimesZPZ = q.x * ZPlusZ; //x times z plus z

        float YTimesYPY = q.y * YPlusY;
        float YTimesZPZ = q.y * ZPlusZ;

        float ZTimesZPZ = q.z * ZPlusZ;

        float WTimesXPX = q.w * XPlusX;
        float WTimesYPY = q.w * YPlusY;
        float WTimesZPZ = q.w * ZPlusZ;

        matrix.m00(((1 - (YTimesYPY + ZTimesZPZ))) * scale);
        matrix.m01((XTimesYPY + WTimesZPZ) * scale);
        matrix.m02((XTimesZPZ - WTimesYPY) * scale);
        matrix.m03(0);
        matrix.m10((XTimesYPY - WTimesZPZ) * scale);
        matrix.m11((1 - (XTimesXPX + ZTimesZPZ)) * scale);
        matrix.m12((YTimesZPZ + WTimesXPX) * scale);
        matrix.m13(0);
        matrix.m20((XTimesZPZ + WTimesYPY) * scale);
        matrix.m21((YTimesZPZ - WTimesXPX) * scale);
        matrix.m22((1 - (XTimesXPX + YTimesYPY)) * scale);
        matrix.m23(0);
        matrix.m30(pos.x);
        matrix.m31(pos.y);
        matrix.m32(pos.z);
        matrix.m33(1);

        //System.out.println("Custom result: ");
        //System.out.println(matrix);
        return matrix;
    }

    //some c++ trickery seems to allow them to pass a quaternion's first member as a pointer to vectorScale, either way I think vectorScale then works on the first 3 members and leaves 'w' unchanged
    public static Vector3f vectorScale(Vector3f in, float scale, Vector3f result){
        return in.mul(scale, result);
    }

    public static Matrix4f concatTransforms(Matrix4f m1, Matrix4f m2, Matrix4f out){
        out = (out == null ? new Matrix4f() : out);
        float cR0, cR1, cR2, cR3;

        if (true){
            float a = m1.m00(),
                    n = m1.m01(),
                    r = m1.m02(),
                    s = m1.m03(),
                    o = m1.m10(),
                    A = m1.m11(),
                    l = m1.m12(),
                    h = m1.m13(),
                    c = m1.m20(),
                    u = m1.m21(),
                    g = m1.m22(),
                    d = m1.m23(),
                    m = m1.m30(),
                    p = m1.m31(),
                    f = m1.m32(),
                    I = m1.m33(),

                    C = m2.m00(),
                    E = m2.m01(),
                    B = m2.m02(),
                    Q = m2.m03();

            float[] outVals = new float[16];
            outVals[0] = (C * a + E * o + B * c + Q * m);
            outVals[1] = (C * n + E * A + B * u + Q * p);
            outVals[2] = (C * r + E * l + B * g + Q * f);
            outVals[3] = (C * s + E * h + B * d + Q * I);
            C = m2.m10();
            E = m2.m11();
            B = m2.m12();
            Q = m2.m13();
            outVals[4] = (C * a + E * o + B * c + Q * m);
            outVals[5] = (C * n + E * A + B * u + Q * p);
            outVals[6] = (C * r + E * l + B * g + Q * f);
            outVals[7] = (C * s + E * h + B * d + Q * I);
            C = m2.m20();
            E = m2.m21();
            B = m2.m22();
            Q = m2.m23();
            outVals[8] = (C * a + E * o + B * c + Q * m);
            outVals[9] = (C * n + E * A + B * u + Q * p);
            outVals[10] = (C * r + E * l + B * g + Q * f);
            outVals[11] = (C * s + E * h + B * d + Q * I);
            C = m2.m30();
            E = m2.m31();
            B = m2.m32();
            Q = m2.m33();
            outVals[12] = (C * a + E * o + B * c + Q * m);
            outVals[13] = (C * n + E * A + B * u + Q * p);
            outVals[14] = (C * r + E * l + B * g + Q * f);
            outVals[15] = (C * s + E * h + B * d + Q * I);

            out.set(outVals);
            /*
            cR0 = m2.m00(); //current col, row 0
            cR1 = m2.m01(); //current col, row 1
            cR2 = m2.m02(); //current col, row 2
            cR3 = m2.m03(); //current col, row 3

            out.m00((cR0 * m1.m00()) + (cR1 * m1.m10()) + (cR2 * m1.m20()) + (cR3 * m1.m30()));
            out.m01((cR0 * m1.m01()) + (cR1 * m1.m11()) + (cR2 * m1.m21()) + (cR3 * m1.m31()));
            out.m02((cR0 * m1.m02()) + (cR1 * m1.m12()) + (cR2 * m1.m22()) + (cR3 * m1.m32()));
            out.m03((cR0 * m1.m03()) + (cR1 * m1.m13()) + (cR2 * m1.m32()) + (cR3 * m1.m33()));

            cR0 = m2.m10();
            cR1 = m2.m11();
            cR2 = m2.m12();
            cR3 = m2.m13();

            out.m10(cR0 * m1.m00() + cR1 * m1.m10() + cR2 * m1.m20() + cR3 * m1.m30());
            out.m11(cR0 * m1.m01() + cR1 * m1.m11() + cR2 * m1.m21() + cR3 * m1.m31());
            out.m12(cR0 * m1.m02() + cR1 * m1.m12() + cR2 * m1.m22() + cR3 * m1.m32());
            out.m13(cR0 * m1.m03() + cR1 * m1.m13() + cR2 * m1.m32() + cR3 * m1.m33());

            cR0 = m2.m20();
            cR1 = m2.m21();
            cR2 = m2.m22();
            cR3 = m2.m23();

            out.m20(cR0 * m1.m00() + cR1 * m1.m10() + cR2 * m1.m20() + cR3 * m1.m30());
            out.m21(cR0 * m1.m01() + cR1 * m1.m11() + cR2 * m1.m21() + cR3 * m1.m31());
            out.m22(cR0 * m1.m02() + cR1 * m1.m12() + cR2 * m1.m22() + cR3 * m1.m32());
            out.m23(cR0 * m1.m03() + cR1 * m1.m13() + cR2 * m1.m32() + cR3 * m1.m33());

            cR0 = m2.m30();
            cR1 = m2.m31();
            cR2 = m2.m32();
            cR3 = m2.m33();

            out.m30(cR0 * m1.m00() + cR1 * m1.m10() + cR2 * m1.m20() + cR3 * m1.m30());
            out.m31(cR0 * m1.m01() + cR1 * m1.m11() + cR2 * m1.m21() + cR3 * m1.m31());
            out.m32(cR0 * m1.m02() + cR1 * m1.m12() + cR2 * m1.m22() + cR3 * m1.m32());
            out.m33(cR0 * m1.m03() + cR1 * m1.m13() + cR2 * m1.m32() + cR3 * m1.m33());*/
        }else{
            for (int a = 0; a < 4; a++){
                cR0 = m2.get(a, 0);
                cR1 = m2.get(a, 1);
                cR2 = m2.get(a, 2);
                cR3 = m2.get(a, 3);

                for (int b = 0; b < 4; b++){
                    out.setRowColumn(b, a, cR0 * m1.m00() + cR1 * m1.m10() + cR2 * m1.m20() + cR3 * m1.m30());
                    out.setRowColumn(b, a, cR0 * m1.m01() + cR1 * m1.m11() + cR2 * m1.m21() + cR3 * m1.m31());
                    out.setRowColumn(b, a, cR0 * m1.m02() + cR1 * m1.m12() + cR2 * m1.m22() + cR3 * m1.m32());
                    out.setRowColumn(b, a, cR0 * m1.m03() + cR1 * m1.m13() + cR2 * m1.m32() + cR3 * m1.m33());
                }
            }
        }

        //System.out.println(Arrays.toString(out.get(new float[16])));

        return out;
    }
    public static float mult(Matrix4f in1, Matrix4f in2, int i, int j){
        float result =
                in1.getRowColumn(i, 0) * in2.getRowColumn(0, j) +
                        in1.getRowColumn(i, 1) * in2.getRowColumn(1, j) +
                        in1.getRowColumn(i, 2) * in2.getRowColumn(2, j) +
                        in1.getRowColumn(i, 3) + in2.getRowColumn(3, j);

        return result;
    }

    public static float simpleSpline(float value){
        float valueSquared = value * value;
        return (3 * valueSquared - 2 * valueSquared * value);
    }
}
