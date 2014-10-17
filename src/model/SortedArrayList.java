package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SortedArrayList extends ArrayList<Task> {
    
    Comparator<Task> comparator;
    public SortedArrayList(Comparator<Task> c) {
        this.comparator = c;
        
    }
    
    @Override
    public boolean add(Task task) {
        for (int i=0; i<this.size(); i++) {
            int index = Collections.binarySearch(this, task, this.comparator);
            if (index < 0) {
                index = -(index + 1);
            }
            super.add(index, task);
        }
        return true;
        
    }
}