package com.kissthinker.event;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.MapMaker;
import com.kissthinker.collection.CollectionUtil;

/**
 * @author David Ainslie
 * Listen (and fire) events - which can be for a specific source.
 */
public abstract class EventSupport
{
    /** */
    private static final Logger LOGGER = LoggerFactory.getLogger(EventSupport.class);

    /** */
    private static final ExecutorService EXECUTOR_SEVICE = Executors.newCachedThreadPool();
    
    /** */
    private static final Map<Object, PropertyChangeSupport> PROPERTY_CHANGE_SUPPORTS = new MapMaker().weakKeys().softValues().makeMap();

    /** */
    private static final Map<EventListener<?>, Object> NO_SOURCE_EVENT_LISTENERS = new MapMaker().weakKeys().softValues().makeMap();

    /** */
    private static final Object NO_SOURCE_MARKER = new Object();

    /** */
    private static final int MAXIMUM_SAVED_EVENTS = 100;

    /** Events can be fired during object instantiation - save them for later. */
    private static final List<Event<?>> SAVED_EVENTS = new ArrayList<Event<?>>();

    /**
     *
     * @return
     */
    public static int sourceListenerCount()
    {
        List<PropertyChangeSupport> propertyChangeSupportsSnapshot = null;

        synchronized (PROPERTY_CHANGE_SUPPORTS)
        {
            propertyChangeSupportsSnapshot = CollectionUtil.arrayList(PROPERTY_CHANGE_SUPPORTS.values());
        }

        int count = 0;

        for (PropertyChangeSupport propertyChangeSupport : propertyChangeSupportsSnapshot)
        {
            count += propertyChangeSupport.getPropertyChangeListeners().length;
            // TODO Resolve duplicate listeners i.e. a listener may be attached to multiple sources.
        }

        return count;
    }

    /**
     *
     * @return
     */
    public static int noSourceListenerCount()
    {
        synchronized (NO_SOURCE_EVENT_LISTENERS)
        {
            return NO_SOURCE_EVENT_LISTENERS.size();
        }
    }

    /**
     *
     * @param <S>
     * @param <E>
     * @param source
     * @param propertyListener
     */
    public static <S, E> void listen(S source, EventListener<E> eventListener)
    {
        synchronized (PROPERTY_CHANGE_SUPPORTS)
        {
            PropertyChangeSupport propertyChangeSupport = PROPERTY_CHANGE_SUPPORTS.get(source);

            if (propertyChangeSupport == null)
            {
                propertyChangeSupport = new PropertyChangeSupport(source);
                PROPERTY_CHANGE_SUPPORTS.put(source, propertyChangeSupport);
            }

            propertyChangeSupport.addPropertyChangeListener("*", eventListener);
        }

        fireSavedEvents(source);
    }

    /**
     *
     * @param <E>
     * @param eventListener
     */
    public static <E> void listen(EventListener<E> eventListener)
    {
        synchronized (NO_SOURCE_EVENT_LISTENERS)
        {
            NO_SOURCE_EVENT_LISTENERS.put(eventListener, NO_SOURCE_MARKER);
        }
    }

    /**
     *
     * @param <S>
     * @param <E>
     * @param source
     * @param eventListener
     */
    public static <S, E> void unlisten(S source, EventListener<E> eventListener)
    {
        synchronized (PROPERTY_CHANGE_SUPPORTS)
        {
            PropertyChangeSupport propertyChangeSupport = PROPERTY_CHANGE_SUPPORTS.get(source);

            if (propertyChangeSupport != null)
            {
                propertyChangeSupport.removePropertyChangeListener("*", eventListener);
            }
        }
    }

    /**
     *
     * @param <E>
     * @param eventListener
     */
    public static <E> void unlisten(EventListener<E> eventListener)
    {
        synchronized (NO_SOURCE_EVENT_LISTENERS)
        {
            NO_SOURCE_EVENT_LISTENERS.remove(eventListener);
        }
    }

    /**
     * Fire event to all "no source" listeners.
     * @see #fire(Event)
     * @param <E>
     * @param event
     */
    public static <E> void fire(final E event)
    {
        fire(new Event<E>(event));
    }

    /**
     * Fire event to all listeners (whether listeners are listening to a specific source, or no source).
     * @see #fire(Event)
     * @param <S>
     * @param <E>
     * @param source
     * @param event
     */
    public static <S, E> void fire(final S source, final E event)
    {
        fire(new Event<E>(source, event));
    }

    /**
     *
     */
    static void reset()
    {
        synchronized (PROPERTY_CHANGE_SUPPORTS)
        {
            PROPERTY_CHANGE_SUPPORTS.clear();
        }

        synchronized (NO_SOURCE_EVENT_LISTENERS)
        {
            NO_SOURCE_EVENT_LISTENERS.clear();
        }

        synchronized (SAVED_EVENTS)
        {
            SAVED_EVENTS.clear();
        }
    }

    /**
     * Fire event to all listeners (whether listeners are listening to a specific source, or no source).
     * @param <E>
     * @param event
     */
    private static <E> void fire(final Event<E> event)
    {
        LOGGER.trace(event.toString());

        EXECUTOR_SEVICE.submit(new Runnable()
        {
            /**
             *
             * @see java.lang.Runnable#run()
             */
            @Override
            public void run()
            {
                // Inform all event listeners per source.
                PropertyChangeSupport propertyChangeSupport = null;

                synchronized (PROPERTY_CHANGE_SUPPORTS)
                {
                    propertyChangeSupport = PROPERTY_CHANGE_SUPPORTS.get(event.getSource());
                }

                if (propertyChangeSupport == null)
                {
                    save(event);
                }
                else
                {
                    propertyChangeSupport.firePropertyChange(event);
                }

                // Inform all event listeners for no source.
                fireNoSource(event);
            }
        });
    }

    /**
     * Saving events allows them to be processed if they were originally fired during object construction.
     * E.g.
     * Object source = new Source();
     * EventSupport.listen(source, EventListener<SomeEvent>()
     * {
     *      public void onEvent(SomeEvent someEvent){}
     * });
     * Now if source fires an event during its construction i.e. within its constructor, the listener will miss the event.
     * It would be best to avoid firing events during construction, however it can make sense to fire an event at this time.
     * @param <E>
     * @param event
     */
    private static synchronized <E> void save(Event<E> event)
    {
        synchronized (SAVED_EVENTS)
        {
            if (SAVED_EVENTS.size() >= MAXIMUM_SAVED_EVENTS)
            {
                // Events not fired have been hanging around too long.
                for (int i = 0; i < 20; i++)
                {
                    Event<?> removedEvent = SAVED_EVENTS.remove(i);

                    if (LOGGER.isTraceEnabled())
                    {
                        LOGGER.trace(String.format("Removed saved event %s, before saving next as save list has reached its maximum capacity.", removedEvent));
                    }
                }
            }

            LOGGER.trace(event.toString());
            SAVED_EVENTS.add(event);
        }
    }

    /**
     *
     * @param <S>
     * @param source
     */
    private static synchronized <S> void fireSavedEvents(S source)
    {
        List<Event<?>> firedSavedEvents = new ArrayList<Event<?>>();

        synchronized (SAVED_EVENTS)
        {
            for (Event<?> event : SAVED_EVENTS)
            {
                if (event.getSource().equals(source))
                {
                    fire(event);
                    firedSavedEvents.add(event);
                }
            }

            for (Event<?> event : firedSavedEvents)
            {
                SAVED_EVENTS.remove(event);
            }
        }
    }

    /**
     * Fire event to all "no source" listeners.
     * @param <E>
     * @param event
     */
    private static <E> void fireNoSource(Event<E> event)
    {
        List<EventListener<?>> noSourceEventListenersSnapshot = null;

        synchronized (NO_SOURCE_EVENT_LISTENERS)
        {
            noSourceEventListenersSnapshot = CollectionUtil.arrayList(NO_SOURCE_EVENT_LISTENERS.keySet());
        }

        for (EventListener<?> eventListener : noSourceEventListenersSnapshot)
        {
            try
            {
                eventListener.propertyChange(new PropertyChangeEvent(event.getSource(), event.getPropertyName(), event.getOldValue(), event.getNewValue()));
            }
            catch (Exception e)
            {
                LOGGER.info("Ignoring: {}", e.getMessage());
            }
        }
    }

    /**
     * Utility.
     */
    private EventSupport()
    {
        super();
    }
}