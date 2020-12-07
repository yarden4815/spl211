package bgu.spl.mics;


import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private static MessageBus messageBusInstance = null;

	private HashMap<MicroService, Queue<Message>> microServiceQueueHashMap;
	private HashMap<Class<Message>, Queue<MicroService>> roundRobinQueues;

	private MessageBusImpl(){
		microServiceQueueHashMap = new HashMap<>();
		roundRobinQueues = new HashMap<>();

	}

	public static MessageBus getInstance(){
		if (messageBusInstance == null)
			messageBusInstance = new MessageBusImpl();
		return messageBusInstance;
	}
	
	
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		boolean found = false;
		for (Class<Message> c : roundRobinQueues.keySet()){
			if (c == type){
				roundRobinQueues.get(c).add(m);
				found = true;
			}
		}
		if (!found) {
			roundRobinQueues.put(type, new ConcurrentLinkedQueue<>());
			roundRobinQueues.get(type).add(m);
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		
    }

	@Override
	public <T> void complete(Event<T> e, T result) {
		
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		
        return null;
	}

	@Override
	public void register(MicroService m) {
		if (microServiceQueueHashMap.containsKey(m))
			throw new IllegalStateException();
		microServiceQueueHashMap.put(m, new ConcurrentLinkedQueue<>());
	}

	@Override
	public void unregister(MicroService m) {
		microServiceQueueHashMap.remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		Queue<Message> q = microServiceQueueHashMap.get(m);
		while (q.isEmpty()){

		}
		return q.poll();
	}
}
