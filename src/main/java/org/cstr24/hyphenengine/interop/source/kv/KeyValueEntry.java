package org.cstr24.hyphenengine.interop.source.kv;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

public class KeyValueEntry {
    public String name = "";
    public String value = "";

    public TreeMap<String, ArrayList<KeyValueEntry>> children; //allowing for the possibility of multiple keys with the same name

    public KeyValueEntry(){
        this("", "");
    }
    public KeyValueEntry(String k, String v){
        this.name = k;
        this.value = v;
        this.children = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    public String getName(){
        return name;
    }
    public String getValue(){
        return value;
    }
    public KeyValueEntry getChild(String key){
        return getChild(key, 0);
    }
    public void addChild(String key, KeyValueEntry child){
        //children.putIfAbsent(key, new ArrayList<>());
        //children.get(key).add(child);

        //System.out.println("adding: " + key + " to arraylist: " + children.get(key));
        children.computeIfAbsent(key, k -> new ArrayList<>()).add(child);
    }
    public KeyValueEntry getChild(String key, int index){
        if (children.containsKey(key)){
            return children.get(key).get(index);
        }else{
            throw new IllegalArgumentException("Key " + key + " not present in element " + this.name);
        }
    }
    public boolean hasChild(String key){
        return children.containsKey(key);
    }

    public String toString(){
        return this.name + ": " + (this.children.isEmpty() ? this.value : "[" + this.children.size() + "]");
    }

    public Set<String> getKeySet(){
        return children.keySet();
    }
}
