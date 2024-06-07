package org.cstr24.hyphengl.input;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

public class InputManager {
    private static final int INPUT_EVENT_QUEUE_SIZE = 256;

    private static InputManager manager;
    public static InputManager get() {
        if (manager == null) {
            manager = new InputManager();
        }
        return manager;
    }

    private final Queue<InputEvent<?>> inputQueue;
    private final ArrayList<InputEventConsumer> eventConsumers;

    public InputManager(){
         inputQueue = new ArrayDeque<>(INPUT_EVENT_QUEUE_SIZE);
         eventConsumers = new ArrayList<>();
    }

    public void queueEvent(InputEvent<?> in){
        //System.out.println("queueing: " + in.getClass().getSimpleName());
        inputQueue.offer(in);
    }

    public Queue<InputEvent<?>> getInputQueue() {
        return inputQueue;
    }

    public void processEvents(){
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

    public void registerEventConsumer(InputEventConsumer consumer){
        eventConsumers.add(consumer);
    }
}
