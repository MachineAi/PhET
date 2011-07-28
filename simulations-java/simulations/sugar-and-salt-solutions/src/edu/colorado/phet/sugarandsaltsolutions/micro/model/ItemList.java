package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.CompositeDoubleProperty;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Observable list class that can be observed for items added or removed.
 *
 * @author Sam Reid
 */
public class ItemList<T> implements Iterable<T> {

    //The items in the list
    private final ArrayList<T> items = new ArrayList<T>();

    //Listeners that are notified when another item is added
    private final ListenerList<T> itemAddedListeners = new ListenerList<T>();

    //Listeners that are notified when any item is removed
    private final ListenerList<T> itemRemovedListeners = new ListenerList<T>();

    //Listeners that are notified when a particular item (as determined by identity) is removed
    //It is important to use identity here so this list can work with mutable values (such as moving particles)
    private final IdentityHashMap<T, ArrayList<VoidFunction0>> particularItemRemovedListeners = new IdentityHashMap<T, ArrayList<VoidFunction0>>();

    //Property that can be used to monitor the number of items in the list.
    //It is typed as Double since that package provides support for composition (through >, +, etc)
    //When support is added for IntegerProperty, this should be switched to use IntegerProperty instead of DoubleProperty
    //TODO: this shouldn't provide a settable interface
    public final DoubleProperty size = new DoubleProperty( 0.0 ) {{
        VoidFunction1<T> listener = new VoidFunction1<T>() {
            public void apply( T t ) {
                set( getItems().size() + 0.0 );
            }
        };
        itemAddedListeners.addListener( listener );
        itemRemovedListeners.addListener( listener );
    }};

    public void addItemAddedListener( VoidFunction1<T> listener ) {
        itemAddedListeners.addListener( listener );
    }

    public void removeItemAddedListener( VoidFunction1<T> listener ) {
        itemAddedListeners.removeListener( listener );
    }

    public void addItemRemovedListener( VoidFunction1<T> listener ) {
        itemRemovedListeners.addListener( listener );
    }

    public void removeItemRemovedListener( VoidFunction1<T> listener ) {
        itemRemovedListeners.removeListener( listener );
    }

    //Listen for the removal of a specific item
    public void addItemRemovedListener( T item, VoidFunction0 listener ) {
        if ( !particularItemRemovedListeners.containsKey( item ) ) {
            particularItemRemovedListeners.put( item, new ArrayList<VoidFunction0>() );
        }
        particularItemRemovedListeners.get( item ).add( listener );
    }

    //Remove a listener that was listening for a specific item removal
    public void removeItemRemovedListener( T item, VoidFunction0 listener ) {
        if ( particularItemRemovedListeners.containsKey( item ) ) {
            particularItemRemovedListeners.get( item ).remove( listener );
        }
    }

    public void add( T item ) {
        items.add( item );
        itemAddedListeners.notifyListeners( item );
    }

    //TODO: Make removal work for subtypes of T, not on arbitrary Object type
    public void remove( Object item ) {

        //Remove the item
        items.remove( item );

        //Notify listeners that are just interested in removal of any item
        itemRemovedListeners.notifyListeners( (T) item );

        //Notify listeners that were specifically listening for when the specified item would be removed
        if ( particularItemRemovedListeners.containsKey( item ) ) {
            for ( VoidFunction0 listener : new ArrayList<VoidFunction0>( particularItemRemovedListeners.get( item ) ) ) {
                listener.apply();
            }
        }
    }

    private ArrayList<T> getItems() {
        return items;
    }

    public Iterator<T> iterator() {
        return items.iterator();
    }

    public boolean contains( T item ) {
        return items.contains( item );
    }

    //Count the items in the list that match the predicate
    public int count( Function1<T, Boolean> predicate ) {
        int count = 0;
        for ( T item : items ) {
            if ( predicate.apply( item ) ) {
                count++;
            }
        }
        return count;
    }

    //Count the items in the list that are an instance of the specified class
    public int count( final Class<? extends T>... clazz ) {
        return filter( clazz ).size();
    }

    //Remove all instances that match the specified classes
    public void clear( final Class<? extends T>... clazz ) {
        for ( T item : filter( clazz ) ) {
            remove( item );
        }
    }

    //Determine which items are instances of the specified classes
    public ArrayList<T> filter( final Class<? extends T>... clazz ) {
        return new ArrayList<T>() {{
            for ( T item : items ) {
                for ( Class<? extends T> aClass : clazz ) {
                    if ( aClass.isInstance( item ) ) {
                        add( item );
                        break;
                    }
                }
            }
        }};
    }

    //Remove all items from the list
    public void clear() {
        while ( size() > 0 ) {
            remove( getItems().get( 0 ) );
        }
    }

    //Get the number of elements in the list
    public int size() {
        return getItems().size();
    }

    //Add all items from the list
    public void addAll( ArrayList<? extends T> elements ) {
        for ( T element : elements ) {
            add( element );
        }
    }

    public CompositeDoubleProperty propertyCount( final Class<? extends T> type ) {
        return new CompositeDoubleProperty( new Function0<Double>() {
            public Double apply() {
                return count( type ) + 0.0;
            }
        }, size );
    }

    public T get( int i ) {
        return items.get( i );
    }
}