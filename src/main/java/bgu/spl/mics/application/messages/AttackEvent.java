package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;


public class AttackEvent implements Event<Boolean> {

    private long duration;
    private int[] serials;

    public AttackEvent(long duration, int[] serials){
        this.duration = duration;
        this.serials = serials;
    }

    public long getDuration() {
        return duration;
    }

    public int[] getSerials() {
        return serials;
    }
}
