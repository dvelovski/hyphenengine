package org.cstr24.hyphenengine.input;

import org.cstr24.hyphenengine.core.Engine;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

public class InputManager {
    private static final int INPUT_EVENT_QUEUE_SIZE = 256;

    private static final Queue<InputEvent<?>> inputQueue = new ArrayDeque<>(INPUT_EVENT_QUEUE_SIZE);
    private static final ArrayList<InputEventConsumer> eventConsumers = new ArrayList<>();

    public static void queueEvent(InputEvent<?> in){
        //System.out.println("queueing: " + in.getClass().getSimpleName());
        inputQueue.offer(in);
    }

    public static Queue<InputEvent<?>> getInputQueue() {
        return inputQueue;
    }

    public static void processEvents(){
        synchronized (inputQueue){
            while (!inputQueue.isEmpty()){
                InputEvent<?> event = inputQueue.poll();

                int cIndex = 0;
                while (cIndex < eventConsumers.size() && !event.isConsumed()){
                    InputEventConsumer eventConsumer = eventConsumers.get(cIndex++);

                    boolean result = eventConsumer.consume(event);
                    if (result){
                        event.consume();
                    }
                }
            }
        }
    }

    public static void registerEventConsumer(InputEventConsumer consumer){
        eventConsumers.add(consumer);
    }

    //load default bindings

    //how do i get a way to register raw keyboard input... oh yeah
    //should that come before or after 'game' actions (as in can game actions consume - nah)

    public static float[] getMousePosition(){
        double[] x = {0};
        double[] y = {0};
        GLFW.glfwGetCursorPos(Engine.getGame().applicationWindow.getWindowHandle(), x, y);
        return new float[]{(float) x[0], (float) y[0]};
    }
}
