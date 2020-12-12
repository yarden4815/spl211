package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.BroadcastImpl;
import bgu.spl.mics.application.services.C3POMicroservice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusTest {

    private MessageBus messageBus;
    private MicroService m;

    @BeforeEach
    private void setUp(){
        messageBus = MessageBusImpl.getInstance();
        m = new C3POMicroservice();
        messageBus.register(m);
        messageBus.subscribeEvent(AttackEvent.class, m);
    }



    @Test
    public void testComplete(){
        int[] serials = {1,2,3};
        Event<Boolean> event = new AttackEvent(1000, serials);
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
        int[] serials = {1,2,3};
        Event<Boolean> event = new AttackEvent(1000, serials);
        Future<Boolean> fEvent = messageBus.sendEvent(event);
        Message message = null;
        try {
            message = messageBus.awaitMessage(m);
        } catch (InterruptedException e){e.printStackTrace();}
        assertEquals(message, event);
    }

    // Also testing subscribeBroadcast.
    @Test
    void SendBroadcastTest(){
        Broadcast broadcast = new BroadcastImpl();
        Class type = broadcast.getClass();
        messageBus.subscribeBroadcast(type, m);
        messageBus.sendBroadcast(broadcast);
        Message message = null;
        try {
            message = messageBus.awaitMessage(m);
        } catch (InterruptedException e){e.printStackTrace();}
        assertEquals(message, broadcast);
    }


}