package com.kissthinker.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.base.Stopwatch;

/**
 * @author David Ainslie
 */
public class EventSupportTest
{
    /** */
    private static final ExecutorService EXECUTOR_SEVICE = Executors.newCachedThreadPool();
    
    /** */
    private User user;

    /** */
    private EventListener<?> eventListener;

    /**
     *
     */
    @Before
    public void intialise()
    {
        EventSupport.reset();

        user = new User("Scooby Doo", User.Gender.male, false);

        eventListener = new EventListener<User.Event>()
        {
            @Override
            public void onEvent(User.Event event)
            {
            }
        };
    }

    /**
     * @throws InterruptedException
     *
     */
    @Test
    public void fire() throws InterruptedException
    {
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        EventSupport.listen(user, new EventListener<User.Event>()
        {
            @Override
            public void onEvent(User.Event event)
            {
                assertTrue(event instanceof User.Event);
                countDownLatch.countDown();
            }
        });

        EventSupport.fire(user, new IgnoreEvent());
        EventSupport.fire(user, new User.Event());

        countDownLatch.await();
    }

    /**
     * @throws InterruptedException
     *
     */
    @Test
    public void fireNoSource() throws InterruptedException
    {
        final CountDownLatch countDownLatch = new CountDownLatch(2);

        EventSupport.listen(new EventListener<User.Event>()
        {
            @Override
            public void onEvent(User.Event event)
            {
                assertTrue(event instanceof User.Event);
                countDownLatch.countDown();
            }
        });

        EventSupport.fire(user, new IgnoreEvent());
        EventSupport.fire(new IgnoreEvent());
        EventSupport.fire(user, new User.Event());
        EventSupport.fire(new User.Event());

        countDownLatch.await();
    }

    /**
     *
     */
    @Test
    public void unlistenSourceEventListener()
    {
        EventSupport.listen(user, eventListener);
        assertEquals(1, EventSupport.sourceListenerCount());

        EventSupport.unlisten(user, eventListener);
        assertEquals(0, EventSupport.sourceListenerCount());
    }

    /**
     *
     */
    @Test
    public void unlistenNoSourceEventListener()
    {
        EventSupport.listen(eventListener);
        assertEquals(1, EventSupport.noSourceListenerCount());

        EventSupport.unlisten(eventListener);
        assertEquals(0, EventSupport.noSourceListenerCount());
    }

    /**
     *
     */
    @Test
    public void anyMemoryLeaks()
    {
        final int runningTimeMilliseconds = 1000 * 30; /* Run for 30 seconds */
        Stopwatch stopWatch = new Stopwatch();
        stopWatch.start();
        boolean running = true;

        while (running)
        {
            // Here, without using a thread pool, objects (in this case event listeners) are created slowly because of creating and disposing of threads.
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    createNoSourceEventListener();
                }
            }, "Any Memory Leaks").start();

            try
            {
                TimeUnit.MILLISECONDS.sleep(100);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }

            if (stopWatch.elapsed(TimeUnit.MILLISECONDS) > runningTimeMilliseconds)
            {
                running = false;
            }
        }
        
        stopWatch.stop();
    }

    /**
     * Note test "garbageCollectSourceEventListener" cannot be tested
     * as setting source to null will not work as PropertyChangeListener references the source.
     * @throws ExecutionException
     * @throws InterruptedException
     *
     */
    @Ignore
    @Test
    public void garbageCollectNoSourceEventListener() throws InterruptedException, ExecutionException
    {
        EventSupport.listen(eventListener);
        assertEquals(1, EventSupport.noSourceListenerCount());

        Future<?> future = EXECUTOR_SEVICE.submit(new Runnable()
        {
            @Override
            public void run()
            {
                while (EventSupport.noSourceListenerCount() != 0)
                {
                    try
                    {
                        eventListener = null;
                        System.gc();
                        Thread.sleep(500);
                    }
                    catch (InterruptedException ignored)
                    {
                    }

                    System.out.printf("No source listener count = %d. Checking for empty%n", EventSupport.noSourceListenerCount());
                    System.gc();
                }
            }
        });

        future.get();
        assertEquals(0, EventSupport.noSourceListenerCount());
    }

    /**
     *
     */
    private void createNoSourceEventListener()
    {
        for (int i = 0; i < 10000; i++)
        {
            EventSupport.listen(new EventListener<User.Event>()
            {
                @Override
                public void onEvent(User.Event event)
                {
                }
            });
        }

        System.out.println("Number of no source event listeners = " + EventSupport.noSourceListenerCount());
    }
}