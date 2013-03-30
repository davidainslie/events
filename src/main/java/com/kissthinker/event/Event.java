package com.kissthinker.event;

import java.beans.PropertyChangeEvent;

/**
 * @author David Ainslie
 */
class Event<V> extends PropertyChangeEvent
{
    /** */
    private static final long serialVersionUID = 1L;
    
    /** */
    private static final Object DUMMY_SOURCE = new Object();

    /**
     *
     * @param <S>
     * @param source
     * @param event an object that can hold multiple (and nested if required) properties that have changed/occurred.
     */
    public <S> Event(S source, V event)
    {
        super(source, "*", null, event);
    }

    /**
     *
     * @param event
     */
    public Event(V event)
    {
        super(DUMMY_SOURCE, "*", null, event);
    }

    /**
     *
     * @param <S>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <S> S source()
    {
        return (S)super.getSource();
    }
}