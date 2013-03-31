package com.kissthinker.event;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author David Ainslie
 */
public abstract class EventListener<E> implements PropertyChangeListener
{    
    /**
     *
     * @param event
     */
    public abstract void onEvent(E event);

    /**
     *
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent)
    {
        try
        {
            onEvent((E)propertyChangeEvent.getNewValue());
        }
        catch (ClassCastException e)
        {
            // Ignore event.
        }
    }
}