package bgu.spl.mics;



import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private static MessageBus messageBusInstance = null;

	private ConcurrentHashMap<MicroService, Queue<Message>> microServiceQueueHashMap;
	private ConcurrentHashMap<Class<? extends Message>, Queue<MicroService>> roundRobinQueues;
	private ConcurrentHashMap<Class<? extends Message>, Vector<MicroService>> broadcasts;


	private Vector<MicroService> registered;

	private ConcurrentHashMap<Event, Future> futures;


	private final Object eventLock;
	private final Object broadcastLock;

	private MessageBusImpl(){
		microServiceQueueHashMap = new ConcurrentHashMap<>();
		roundRobinQueues = new ConcurrentHashMap<>();
		broadcasts = new ConcurrentHashMap<>();
		futures = new ConcurrentHashMap<>();
		registered = new Vector<>();
		eventLock = new Object();
		broadcastLock = new Object();
	}

	public static MessageBus getInstance(){
		if (messageBusInstance == null)
			messageBusInstance = new MessageBusImpl();
		return messageBusInstance;
	}
	
	
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		boolean found = false;
		synchronized (eventLock) {
			for (Class<? extends Message> c : roundRobinQueues.keySet()) {
				if (c == type) {
					roundRobinQueues.get(c).add(m);
					found = true;
				}
			}
			if (!found) {
				roundRobinQueues.put(type, new ConcurrentLinkedQueue<>());
				roundRobinQueues.get(type).add(m);
			}
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		boolean found = false;
		synchronized (broadcastLock) {
			for (Class<? extends Message> c : broadcasts.keySet()) {
				if (c == type) {
					broadcasts.get(c).add(m);
					found = true;
				}
			}
			if (!found) {
				broadcasts.put(type, new Vector<>());
				broadcasts.get(type).add(m);
			}
		}
    }

	@Override
	public <T> void complete(Event<T> e, T result) {
		Future<T> future = futures.get(e);
		future.resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		synchronized (broadcastLock) {
			Vector<MicroService> v = broadcasts.get(b.getClass());
			for (MicroService m : v) {
				if (registered.contains(m)){
					Queue<Message> q = microServiceQueueHashMap.get(m);
					q.add(b);
				}
			}
		}
		synchronized (this) {
			this.notifyAll();
		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e){
		Future<T> future = new Future<>();
		Queue<MicroService> q = roundRobinQueues.get(e.getClass());
		if (q == null) {
			return null;
		}
		synchronized (eventLock) {
			MicroService m = q.poll();
			if (microServiceQueueHashMap.get(m) == null)
				return null;
			if (registered.contains(m)) {
				microServiceQueueHashMap.get(m).add(e);
				q.add(m);
			}
		}
		futures.put(e, future);
		synchronized (this) {
			this.notifyAll();
		}
		return future;
	}

	@Override
	public void register(MicroService m) {
		if (microServiceQueueHashMap.containsKey(m))
			throw new IllegalStateException();
		microServiceQueueHashMap.put(m, new ConcurrentLinkedQueue<>());
		registered.add(m);
	}

	@Override
	public void unregister(MicroService m) {
		registered.remove(m);
		microServiceQueueHashMap.remove(m);
		if (registered.isEmpty())
			clean();
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		Queue<Message> q = microServiceQueueHashMap.get(m);
		while (q.isEmpty()){
			synchronized (this) {
				this.wait();
			}
		}
		return q.poll();
	}

	private void clean(){
		messageBusInstance = null;
	}

}
