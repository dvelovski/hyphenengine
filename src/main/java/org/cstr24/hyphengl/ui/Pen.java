package org.cstr24.hyphengl.ui;

import org.cstr24.hyphengl.assets.AssetLoader;
import org.cstr24.hyphengl.rendering.Colour;
import org.cstr24.hyphengl.ui.datatypes.*;
import org.cstr24.hyphengl.ui.fonts.FontMetrics;
import org.cstr24.hyphengl.ui.fonts.HyFont;
import org.joml.QuaternionfX;
import org.joml.Vector2f;
import org.lwjgl.nanovg.NanoVG;

/**
 * Class for drawing vector graphics and UI controls using the underlying vector renderer.
 */
public class Pen {

    /** state **/
    private State state;
    private final long ctx;

    //my autistic observations and recollections of other software hopefully will serve me well here

    //we need a transform

    public Pen(long ctx){
        this.ctx = ctx;
        state = new State.Default(this);
    }

    public void drawRectangle(float x, float y, float w, float h){
        NanoVG.nvgBeginPath(ctx);
        nRectangle(x, y, w, h);
        NanoVG.nvgStroke(ctx);
    }
    public void fillRectangle(float x, float y, float w, float h){
        NanoVG.nvgBeginPath(ctx);
        nRectangle(x, y, w, h);

        state.fill.apply(ctx);
        NanoVG.nvgFill(ctx);
    }
    private void nRectangle(float x, float y, float w, float h){
        NanoVG.nvgRect(ctx, x, y, w, h);
    }
    public void setFillColour(float r, float g, float b, float a){
        if (this.state.fill.type != FillType.Colour){
            this.state.setFill(new ColourFill());
        }
        ((ColourFill) this.state.fill).setColour(r, g, b, a);
    }
    public void setFillColour(Colour in){
        setFillColour(in.r, in.g, in.b, in.a);
    }
    public void setStrokeColour(float r, float g, float b, float a){
        if (this.state.stroke.type != FillType.Colour){
            this.state.setStroke(new ColourFill());
        }
        ((ColourFill) this.state.stroke).setColour(r, g, b, a);
    }
    public void setStrokeColour(Colour in){
        setStrokeColour(in.r, in.g, in.b, in.a);
    }
    public void setScale(float newX, float newY){

    }

    public void drawString(String text, float x, float y){
        state.stroke.apply(ctx);

        NanoVG.nvgFontSize(ctx, state.fontSize);

        float fA = state.fontMetrics.getAscent();
        float fD = state.fontMetrics.getDescent();
        float scale = state.fontSize / (fA - fD);

        NanoVG.nvgText(ctx, x, y + (state.fontMetrics.getAscent(scale) - state.fontMetrics.getDescent(scale)), text);
    }

    public void setState(State newState){
        this.state = newState;
    }
    public State getState(){
        return this.state;
    }
    public State cloneState(){
        return new State(this.state);
    }
    public Transform getTransform(){return this.state.transform;}
    public Transform getRelativeTransform(){return this.state.relativeTransform;}

    public void setFont(String fontName, int size){
        state.currentFont = ((HyFont) AssetLoader.get().loadResource(HyFont.RESOURCE_TYPE, fontName).get());
        state.fontSize = size;
        state.fontMetrics = state.currentFont.getFontVMetrics();

        NanoVG.nvgFontFaceId(ctx, state.currentFont.getNVGHandle());
    }
    public void getTextBounds(String text, float[] wh){
        NanoVG.nvgTextBounds(ctx, 0, 0, text, wh);
    }

    /** Stores a particular state of the Pen **/
    public static class State{
        public Object userData;
        public Pen creator;

        public Fill fill;
        public Fill stroke;

        public HyFont currentFont;
        public int fontSize;
        public FontMetrics fontMetrics;

        private Transform relativeTransform;
        private Transform transform;

        public State(){
            fill = new ColourFill().setColour(0, 0, 0, 1);
            stroke = new ColourFill().setColour(0, 0, 0, 1);
            transform = new Transform();
            relativeTransform = new Transform();
        }

        public State(Pen c){
            this();
            this.creator = c;
        }
        public State(State other){
            userData = other.userData;
            creator = other.creator;

            fill = other.fill;
            stroke = other.stroke;

            relativeTransform = new Transform(other.relativeTransform);
            transform = new Transform(other.transform);

            currentFont = other.currentFont;
            fontSize = other.fontSize;
            fontMetrics = other.fontMetrics;
        }
        public Fill getFill(){
            return fill;
        }
        public void setFill(Fill newFill){
            if (fill != null){
                fill.discard();
            }
            fill = newFill;
        }
        public void setStroke(Fill newStroke){
            if (stroke != null){
                stroke.discard();
            }
            stroke = newStroke;
        }

        public Transform getTransform(){
            return this.transform;
        }
        public Transform getRelativeTransform(){
            return this.relativeTransform;
        }
        public void setTransform(Transform xform){
            this.transform = xform;
        }
        public void setRelativeTransform(Transform rXform){
            this.relativeTransform = rXform;
        }

        public static final class Default extends State{
            public Default(Pen p){
                super(p);
            }
        }
    }

    public static class Transform{
        public Vector2f position;
        public QuaternionfX rotation;
        public Vector2f scale;

        public Transform(){
            position = new Vector2f(0, 0);
            rotation = new QuaternionfX();
            rotation.rotationXYZ(0, 0, 0);
            scale = new Vector2f(1, 1);
        }
        public Transform(Transform other){
            position = new Vector2f(other.position);
            rotation = new QuaternionfX(other.rotation);
            scale = new Vector2f(other.scale);
        }
    }
}
