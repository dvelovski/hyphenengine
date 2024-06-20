package org.cstr24.hyphenengine.ui.css;

public abstract class TransitionTimingFunction {
    public static TransitionTimingFunction Ease = new CubicBezier(0.25f, 0.1f, 0.25f, 1.0f);
    public static TransitionTimingFunction Linear = new CubicBezier(0f, 0f, 1f, 1f);
    public static TransitionTimingFunction EaseIn = new CubicBezier(0.42f, 0f, 1.0f, 1.0f);
    public static TransitionTimingFunction EaseOut = new CubicBezier(0f, 0f, 0.58f, 1.0f);
    public static TransitionTimingFunction EaseInOut = new CubicBezier(0.42f, 0, 0.58f, 1.0f);

    public static TransitionTimingFunction TemplateCubicBezier = new CubicBezier(0, 0, 0,0);

    public static TransitionTimingFunction Steps = null;

    public static TransitionTimingFunction getOrDefault(String name){
        switch (name){
            case "linear" -> {
                return Linear;
            }
            case "ease-in" -> {
                return EaseIn;
            }
            case "ease-out" -> {
                return EaseOut;
            }
            case "ease-in-out" -> {
                return EaseInOut;
            }
            case "steps" -> {
                return Steps;
            }
            case "cubic-bezier" -> {
                return TemplateCubicBezier;
            }
            default -> {
                return Ease;
            }
        }
    }

    public abstract float solve(float t);

    public static class CubicBezier extends TransitionTimingFunction{
        float x0, y0;
        float x1, y1;
        float x2, y2;
        float x3, y3;

        public CubicBezier(float inX1, float inY1, float inX2, float inY2){
            this.x0 = y0 = 0;

            this.x1 = inX1;
            this.y1 = inY1;

            this.x2 = inX2;
            this.y2 = inY2;

            this.x3 = y3 = 1;
        }

        public float x(float t){
            float v0 = (float) (Math.pow(1 - t, 3) * x0);
            float v1 = (float) (3 * Math.pow(1 - t, 2) * t * x1);
            float v2 = (float) (3 * (1 - t) * Math.pow(t, 2) * x2);
            float v3 = (float) (Math.pow(t, 3) * x3);

            return v0 + v1 + v2 + v3;
        }
        public float y(float t){
            float v0 = (float) (Math.pow(1 - t, 3) * y0);
            float v1 = (float) (3 * Math.pow(1 - t, 2) * t * y1);
            float v2 = (float) (3 * (1 - t) * Math.pow(t, 2) * y2);
            float v3 = (float) Math.pow(t, 3) * y3;

            return v0 + v1 + v2 + v3;
        }
        public float solve(float t){
            return y(t);
        }
    }
}
