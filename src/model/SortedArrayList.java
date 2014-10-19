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
    
    public boolean addOrder(Task task) {
        if (this.size() == 0) {
            super.add(0, task);
        } else {
            int index = Collections.binarySearch(this, task, this.comparator);
            if (index < 0) {
                index = -(index + 1);
            }
            super.add(index, task);
            
        }
        
        return true;
       
    }
    
    /** 
     * @param index of task that has been edited
     * 
     * call this method to re-order the task edited when the deadline has been changed
     */
    public void updateListOrder(int index) {
        assert ((index < this.size()) && (index >= 0));
        
        Task task = this.remove(index);
        this.addOrder(task);
    }
}
