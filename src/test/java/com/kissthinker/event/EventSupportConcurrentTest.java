package com.kissthinker.event;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mycila.junit.concurrent.ConcurrentJunitRunner;

/**
 * @author David Ainslie
 */
@RunWith(ConcurrentJunitRunner.class)
public class EventSupportConcurrentTest
{
    /** */
    private static final CountDownLatch COUNT_DOWN_LATCH = new CountDownLatch(3);

    /** */
    private static User user;
    
    /** */
    private static int sourceEventCount;
    
    /** */
    private static int noSourceEventCount;

    /**
     *
     */
    @BeforeClass
    public static void initialiseClass()
    {
        user = new User("Scooby Doo", User.Gender.male, false);

        EventSupport.listen(user, new EventListener<User.Event>()
        {
            @Override
            public void onEvent(User.Event event)
            {
                assertTrue(event instanceof User.Event);
                sourceEventCount++;
                COUNT_DOWN_LATCH.countDown();
            }
        });

        EventSupport.listen(new EventListener<User.Event>()
        {
            @Override
            public void onEvent(User.Event event)
            {
                assertTrue(event instanceof User.Event);
                noSourceEventCount++;
                COUNT_DOWN_LATCH.countDown();
            }
        });
    }

    /**
     * @throws InterruptedException
     *
     */
    @AfterClass
    public static void classAssertion() throws InterruptedException
    {
        COUNT_DOWN_LATCH.await(3, TimeUnit.SECONDS);
        assertEquals(1, sourceEventCount);
        assertEquals(2, noSourceEventCount);
    }

    /**
     *
     */
    @Test
    public void fireSourceIgnoreEvent()
    {
        EventSupport.fire(user, new IgnoreEvent());
    }

    /**
     * 
     */
    @Test
    public void fireNoSourceUserEvent()
    {
        EventSupport.fire(new User.Event());
    }

    /**
     * 
     */
    @Test
    public void fireNoSourceIgnoreEvent()
    {
        EventSupport.fire(new IgnoreEvent());
    }

    /**
     * 
     */
    @Test
    public void fireSourceUserEvent()
    {
        EventSupport.fire(user, new User.Event());
    }
}