# event-manager

A Java event management project with event priority and multi-event listening.

### Instructions on how to use this within your own projects

Annotate a method with the @ETarget annotation as follows:
```java
    public class MyEventHandlerClass {
        @ETarget(MyEvent.class)
        public void myEventMethod() {
            //here you do something you'd like to happen where you fire your event
        }
    }
```
Then fire MyEvent where it's needed as follows:
```java
    public void doSomething() {
        // your method does something here
        MyEvent myEvent = new MyEvent();
        EventManager.getInstance().fire(event);
    }
```
Finally, register the class you put myEventMethod() in for all (or one) event as follows:
```java
    public class MyEventHandlerClass {
        public MyEventHandlerClass() {
            EventManager.getInstance().register(this, new MyEvent())
            // or EventManager.getInstance().register(this) to register all events.
        }
        
        @ETarget(MyEvent.class)
        public void myEventMethod() {
            //here you do something you'd like to happen where you fire your event
        }
    }
```
Repeat for however many classes you use for handling events.
