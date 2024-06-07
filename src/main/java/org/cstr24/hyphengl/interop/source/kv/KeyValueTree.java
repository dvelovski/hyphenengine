package org.cstr24.hyphengl.interop.source.kv;

public class KeyValueTree {
    public KeyValueEntry treeRoot;

    public KeyValueTree(){
        treeRoot = new KeyValueEntry();
    }
    public KeyValueTree(KeyValueEntry _root){
        treeRoot = _root;
    }

    public void print(){
        System.out.println("Tree");
        print(treeRoot, 0);
    }
    public void printTabs(int level){
        for (int i = 0; i < level; i++){
            System.out.print("\t");
        }
    }
    public void print(KeyValueEntry kv, int level){
        printTabs(level);
        kv.children.forEach((key, value) -> {
            printTabs(level);
            System.out.println(key);
            value.forEach(child -> {
                print(child, level + 1);
            });
        });
    }
}
