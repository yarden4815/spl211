package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.BroadcastImpl;
import bgu.spl.mics.application.services.C3POMicroservice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusTest {

    private MessageBus messageBus;

    @BeforeEach
    private void setUp(){
        messageBus = new MessageBusImpl();
    }



    @Test
    public void testComplete(){
        MicroService m = new C3POMicroservice();
        messageBus.register(m);
        messageBus.subscribeEvent(AttackEvent.class, m);
        Event<Boolean> event = new AttackEvent();
        Future<Boolean> future = messageBus.sendEvent(event);
        Message message = null;
        try {
            message = messageBus.awaitMessage(m);
        } catch (InterruptedException e){e.printStackTrace();}
        messageBus.complete((Event<Boolean>) message, true);
        assertTrue(future.isDone());
        assertTrue(future.get());
    }



    // Testing the method awaitMessage while also testing register, sendEvent and subscribeEvent.
    @Test
    void testAwaitMessage(){
        MicroService m1 = new C3POMicroservice();
        Event<Boolean> event = new AttackEvent();
        messageBus.register(m1);
        messageBus.subscribeEvent(AttackEvent.class, m1);
        Future<Boolean> fEvent = messageBus.sendEvent(event);
        Message message = null;
        try {
            message = messageBus.awaitMessage(m1);
        } catch (InterruptedException e){e.printStackTrace();}
        assertEquals(message, event);
    }

    // Also testing subscribeBroadcast.
    @Test
    void SendBroadcastTest(){
        MicroService m1 = new C3POMicroservice();
        Broadcast broadcast = new BroadcastImpl();
        Class type = broadcast.getClass();
        messageBus.register(m1);
        messageBus.subscribeBroadcast(type, m1);
        messageBus.sendBroadcast(broadcast);
        Message message = null;
        try {
            message = messageBus.awaitMessage(m1);
        } catch (InterruptedException e){e.printStackTrace();}
        assertEquals(message, broadcast);
    }


}