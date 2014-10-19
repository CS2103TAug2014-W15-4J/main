package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import com.thoughtworks.xstream.annotations.XStreamAlias;


public class SortedArrayList<T> extends ArrayList<T> {


    @XStreamAlias("Comparator")
    Comparator<T> comparator;
    public SortedArrayList(Comparator<T> c) {
        this.comparator = c;
    }
    
    public SortedArrayList(int initialCount, Comparator<T> c) {
        super(initialCount);
        this.comparator = c;
    }
    
    public boolean addOrder(T task) {
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
    
    @Override
    public boolean addAll(Collection<? extends T> c) {
        Iterator<? extends T> i = c.iterator();
        while (i.hasNext()) {
            addOrder(i.next());
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
        
        T task = this.remove(index);
        this.addOrder(task);
    }
}
