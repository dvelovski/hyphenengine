package org.joml;

/**
 * Sub-class of Quaternionf class with some enhancements.
 */
public class QuaternionfX extends Quaternionf {
    public QuaternionfX() {
    }

    public QuaternionfX(double x, double y, double z, double w) {
        super(x, y, z, w);
    }

    public QuaternionfX(float x, float y, float z, float w) {
        super(x, y, z, w);
    }

    public QuaternionfX(Quaternionfc source) {
        super(source);
    }

    public QuaternionfX(Quaterniondc source) {
        super(source);
    }

    public QuaternionfX(AxisAngle4f axisAngle) {
        super(axisAngle);
    }

    public QuaternionfX(AxisAngle4d axisAngle) {
        super(axisAngle);
    }

    public float component(int component){
        switch (component){
            case 0 -> {
                return x;
            }
            case 1 -> {
                return y;
            }
            case 2 -> {
                return z;
            }
            case 3 -> {
                return w;
            }
            default -> {
                throw new IllegalArgumentException();
            }
        }
    }
    public void setComponent(int component, float value){
        switch (component){
            case 0 -> {
                x = value;
            }
            case 1 -> {
                y = value;
            }
            case 2 -> {
                z = value;
            }
            case 3 -> {
                w = value;
            }
            default -> {
                throw new IllegalArgumentException();
            }
        }
    }

    public QuaternionfX zero(){
         this.set(0, 0, 0, 0);
         return this;
    }

}
