package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.thoughtworks.xstream.annotations.XStreamAlias;


public class SortedArrayList extends ArrayList<Task> {
	@XStreamAlias("Comparator")
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
