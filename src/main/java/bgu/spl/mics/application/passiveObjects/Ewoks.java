package bgu.spl.mics.application.passiveObjects;


/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {

    private Ewok[] ewoks;

    private static Ewoks ewoksInstance = null;

    public static Ewoks getInstance(int numOfEwoks){
        if (ewoksInstance == null)
            ewoksInstance = new Ewoks(numOfEwoks);
        return ewoksInstance;
    }

    public static Ewoks getInstance(){
        if (ewoksInstance == null)
            throw new IllegalArgumentException("Ewoks have not been initialized.");
        return ewoksInstance;
    }

    private Ewoks(int numOfEwoks){
        ewoks = new Ewok[numOfEwoks];
        for (int i = 0; i < numOfEwoks; i++){
            ewoks[i] = new Ewok(i);
        }
    }

    public synchronized void acquireEwoks(int[] serialNum) throws InterruptedException{
        for (int i = 0; i < serialNum.length; i++){
            while (!ewoks[serialNum[i]].isAvailable()){
                wait();
            }
            ewoks[serialNum[i]].acquire();
        }
    }

    public void releaseEwoks(int[] serialNum){
        for (int i = 0; i < serialNum.length; i++){
           ewoks[serialNum[i]].release();
        }
        notifyAll();
    }

}
