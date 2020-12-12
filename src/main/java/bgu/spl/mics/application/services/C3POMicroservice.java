package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;


/**
 * C3POMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class C3POMicroservice extends MicroService {

    private Diary diary;
	
    public C3POMicroservice() {
        super("C3PO");
        diary = Diary.getInstance();
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TerminationBroadcast.class, c -> {
            terminate();
            diary.setC3POTerminate(System.currentTimeMillis());
        });
        subscribeEvent(AttackEvent.class, c -> {
            Ewoks ewoks = Ewoks.getInstance();
            try {
                ewoks.acquireEwoks(c.getSerials());
                Thread.sleep(c.getDuration());
            }catch (InterruptedException e){e.printStackTrace();}
            ewoks.releaseEwoks(c.getSerials());
            diary.setTotalAttacks();
            diary.setC3POFinish(System.currentTimeMillis());
        });
    }
}
