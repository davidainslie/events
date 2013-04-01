Fire and listen to events.
An event listener can either listen for an event type from a specific source, or any source.
Event listeners just register for listening - they are garbage collected automatically.

Example

Say the source is

```java
User user = new User();
```

where an "event" associated with the User can be declared anywhere as a POJO e.g. let's declare our event within User.

```java
public class User
{
    // Properties.
    // ...
    
    public static class Event
    {
    }
}    
```

And to listen to User.Event on "user", just do:

```java
EventSupport.listen(user, new EventListener<User.Event>()
{
    public void onEvent(User.Event event)
    {
        // ...
    }
});
```

And to listen to User.Event from anyone (not just users, but anything that fires an User.Event):

```java
EventSupport.listen(new EventListener<User.Event>()
{
    public void onEvent(User.Event event)
    {
        // ...
    }
});
```

For our "user" to fire an event:
```java
EventSupport.fire(user, new User.Event());
```

And to fire an anonymous "user" event:
```java
EventSupport.fire(new User.Event());
```

Note, this is a Java 7, Maven 3 project.