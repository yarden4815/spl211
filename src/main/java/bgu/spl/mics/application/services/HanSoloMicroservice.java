package bgu.spl.mics.application.services;
import bgu.spl.mics.application.messages.*;


import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;

/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends MicroService {

    private Diary diary;

    public HanSoloMicroservice() {
        super("Han");
        diary = Diary.getInstance();
    }


    @Override
    protected void initialize() {
        subscribeBroadcast(TerminationBroadcast.class, c -> {
            terminate();
            diary.setHanSoloTerminate(System.currentTimeMillis());
        });
        subscribeEvent(AttackEvent.class, c -> {
            Ewoks ewoks = Ewoks.getInstance();
            try {
                ewoks.acquireEwoks(c.getSerials());
                Thread.sleep(c.getDuration());
            }catch (InterruptedException e){e.printStackTrace();}
            ewoks.releaseEwoks(c.getSerials());
            diary.setTotalAttacks();
            complete(c, true);
            diary.setHanSoloFinish(System.currentTimeMillis());
        });
        subscribeBroadcast(EndAttacks.class, c -> {
            sendEvent(new FinishedAttacks());
        });
    }
}
