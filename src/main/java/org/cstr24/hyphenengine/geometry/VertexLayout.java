package org.cstr24.hyphenengine.geometry;

import java.util.ArrayList;

public class VertexLayout {
    public ArrayList<VertexElement> elements;
    public int elementCount;
    private int computedSize;

    public VertexLayout(){
        elements = new ArrayList<>();
    }

    public VertexLayout addElement(VertexElement element){
        if (!elements.isEmpty()){
            var last = elements.getLast();
            element.relativeOffset = last.relativeOffset + last.computeSize();
        }

        elements.add(element);
        elementCount++;

        //System.out.println(element.componentType + " @ offset " + element.relativeOffset);
        computedSize += element.computeSize();

        return this;
    }

    public void recalculateOffsets(){
        if (!elements.isEmpty()){
            int runningOffset = 0;
            elements.getFirst().relativeOffset = runningOffset;

            var lastElement = elements.getFirst();

            for (int i = 1; i < elements.size(); i++){
                var currElement = elements.get(i);
                currElement.relativeOffset = runningOffset + lastElement.computeSize();

                runningOffset = currElement.relativeOffset;
                lastElement = currElement;
            }
            computedSize = runningOffset + lastElement.computeSize();
        }else{
            computedSize = 0;
        }

    }

    public int sizeOf(){
        return computedSize;
    }

    public int getElementOffset(int element) {
        return elements.get(element).relativeOffset;
    }

    public static VertexLayout create(){
        return new VertexLayout();
    }

    public static VertexLayout with(VertexElement... arr){
        var layout = create();
        for (VertexElement element : arr) {
            layout.addElement(element);
        }
        return layout;
    }
}
