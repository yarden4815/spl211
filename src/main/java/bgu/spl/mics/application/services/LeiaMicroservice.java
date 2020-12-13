package bgu.spl.mics.application.services;

import java.util.ArrayList;
import java.util.List;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Diary;


/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
	private Attack[] attacks;
	private Diary diary;
	private int attacksCounter;
	private Future<Boolean> deactivationFuture;

    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
		this.attacks = attacks;
		diary = Diary.getInstance();
		attacksCounter = 0;
		deactivationFuture = null;
    }

    @Override
    protected void initialize() {
    	subscribeBroadcast(TerminationBroadcast.class, c -> {
    	    terminate();
    	    diary.setLeiaTerminate(System.currentTimeMillis());
        });
    	subscribeEvent(FinishedAttacks.class, c -> {
    	    attacksCounter += 1;
    	    if (attacksCounter == 2){
    	        deactivationFuture = sendEvent(new DeactivationEvent());
    	        deactivationFuture.get();
    	        sendEvent(new BombDestroyerEvent());
            }
        });
    	try {
            Thread.sleep(100);
        }catch (InterruptedException e){e.printStackTrace();}
		for (Attack attack : attacks) {
			sendEvent(new AttackEvent(attack.getDuration(), attack.getSerials()));
		}
    }
}
