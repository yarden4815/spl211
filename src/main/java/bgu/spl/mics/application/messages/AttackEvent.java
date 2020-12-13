package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;

import java.util.List;


public class AttackEvent implements Event<Boolean> {

    private long duration;
    private List<Integer> serials;

    public AttackEvent(long duration, List<Integer> serials){
        this.duration = duration;
        this.serials = serials;
    }

    public long getDuration() {
        return duration;
    }

    public int[] getSerials() {
        int[] res = new int[serials.size()];
        for (Integer i : serials){
            res[i] = serials.get(i);
        }
        return res;
    }
}
