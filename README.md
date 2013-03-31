Fire and listen to events.
An event listener can either listen for an event type from a specific source, or any source.
Event listeners just register for listening - they are garbage collected automatically.

Example

Say the source is

<code>
User user = new User();
</code>

where an "event" associated with the User can be declared anywhere as a POJO e.g. let's declare our event within User.

<pre>
<code>
public class User
{
    // Properties.
    // ...
    
    public static class Event
    {
    }
}    
</code>
</pre>

And to listen to User.Event on "user", just do:

<pre>
<code>
EventSupport.listen(user, new EventListener<User.Event>()
{
    public void onEvent(User.Event event)
    {
        // ...
    }
});
</code>
</pre>

And to listen to User.Event from anyone (not just users, but anything that fires an User.Event):

<pre>
<code>
EventSupport.listen(new EventListener<User.Event>()
{
    public void onEvent(User.Event event)
    {
        // ...
    }
});
</code>
</pre>

For our "user" to fire an event:
<code>
EventSupport.fire(user, new User.Event());
</code>

And to fire an anonymoure "user" event:
<code>
EventSupport.fire(new User.Event());
</code>

Note, this is a Java 7, Maven 3 project.