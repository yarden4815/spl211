package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;


public class AttackEvent implements Event<Boolean> {

    private int duration;
    private int[] serials;

    public AttackEvent(int duration, int[] serials){
        this.duration = duration;
        this.serials = serials;
    }

    public int getDuration() {
        return duration;
    }

    public int[] getSerials() {
        return serials;
    }
}
