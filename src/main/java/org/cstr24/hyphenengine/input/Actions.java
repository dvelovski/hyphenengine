package org.cstr24.hyphenengine.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class Actions {
    private static final Logger LOGGER = Logger.getLogger(Actions.class.getName());

    private static HashMap<String, BindableAction> actionMap;
    private static HashMap<Integer, BindableAction> inputMap;

    private static HashMap<BindableAction, ArrayList<BindableActionListener>> listenerMap;



    public static void registerAction(BindableAction action){

    }
    public static void registerActionListener(String actionName, BindableActionListener listener){

    }
    public static boolean inputTriggersAction(int inputCode){
        if (inputMap.containsKey(inputCode)){
            inputTriggered(inputMap.get(inputCode));
            return true;
        }
        return false;
    }
    public static void mapInputToAction(int inputCode, String action){

    }
    public static void removeInputMapping(int inputCode){

    }
    public static void clearActionInputMappings(String actionName){

    }
    private static void inputTriggered(BindableAction action){
        listenerMap.get(action).forEach(BindableActionListener::actionPerformed);
    }
    private static void createInput(){

    }
}
