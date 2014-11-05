package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import com.thoughtworks.xstream.annotations.XStreamAlias;

//@author A0115384H
/**
 * This class models an array list that is sorted using a comparator.
 * 
 * @param <T>   The generalised class a SortedArrayList contains.
 */
public class SortedArrayList<T> extends ArrayList<T> {

    @XStreamAlias("Comparator")
    Comparator<T> comparator;
    
    /**
     * This constructor creates a new SortedArrayList, and assigns the specified comparator.
     * 
     * @param comparator The comparator that is assigned to this SortedArrayList instance. 
     */
    public SortedArrayList(Comparator<T> comparator) {
        this.comparator = comparator;
    }
    
    /**
     * This constructor creates a new SortedArrayList with the specified size, 
     * and assigns the specified comparator.
     * 
     * @param initialCount  The initial size of the SortedArrayList.
     * @param comparator    The comparator that is assigned to this SortedArrayList instance.
     */
    public SortedArrayList(int initialCount, Comparator<T> comparator) {
        super(initialCount);
        this.comparator = comparator;
    }
    
    /**
     * This method adds the object only if the list does not already contain this object.
     * If the list does not contain the object, it is added into the list sorted.
     *  
     * @param <T> element   The element to be added.
     * @return              true if the element is added, false otherwise.
     */
    public boolean addUnique(T element) {
        if (this.contains(element)) {
            return false;
        } else {
            this.addOrder(element);
            return true;
        }
    }
    
    /**
     * This method adds the element into the list in its sorted position.
     *  
     * @param <T> element   The element to be added.
     * @return              true if the element is added successfully.
     */
    public boolean addOrder(T element) {
        if (this.size() == 0) {
            super.add(0, element);
        } else {
            int index = Collections.binarySearch(this, element, this.comparator);
            if (index < 0) {
                index = -(index + 1);
            }
            super.add(index, element);
        }
        return true;
    }
    
    /**
     * This method adds all elements in the given collection into the list, ordered.
     * 
     * @param collection    The collection of items to be added.
     * @return              true after all the elements are added successfully.
     */
    public boolean addAllOrdered(Collection<? extends T> collection) {
        Iterator<? extends T> i = collection.iterator();
        while (i.hasNext()) {
            addOrder(i.next());
        }
        return true;
    }
    
    /**
     * This method adds all elements in the given collection at the end of the list, unordered.
     * 
     * @param collection    The collection of items to be added.
     * @return              true after all the elements are added successfully.
     */
    public boolean addAllUnordered(Collection<? extends T> collection) {
        return super.addAll(collection);
    }
    
    /** 
     * This method re-orders the task edited.
     * This method is called when the deadline of that task has been changed.
     * 
     * @param index of task that has been edited
     */
    public void updateListOrder(int index) {
        assert ((index < this.size()) && (index >= 0));
        
        T task = this.remove(index);
        this.addOrder(task);
    }
}
