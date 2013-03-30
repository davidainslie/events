Fire and listen to events.
An event listener can either listen for an event type from a specific source, or any source.
Event listeners just register for listening - they are garbage collected automatically.

Example

Say the source is

User user = new User();

where an "event" associated with the User can be declared anywhere as a POJO e.g. let's declare our event within User.

public class User
{
    // ...
    // Properties.
    // ...
    
    public static class Event
    {
    }
}    

And to listen to User.Event on "user", just do:

EventSupport.listen(user, new EventListener<User.Event>()
{
    public void onEvent(User.Event event)
    {
        // ...
    }
});

And to listen to User.Event from anyone (not just users, but anything that fires an User.Event):

EventSupport.listen(new EventListener<User.Event>()
{
    public void onEvent(User.Event event)
    {
        // ...
    }
});


For our "user" to fire an event:
EventSupport.fire(user, new User.Event());

And to fire an anonymoure "user" event:
EventSupport.fire(new User.Event());

Note, this is a Java 7, Maven 3 project.