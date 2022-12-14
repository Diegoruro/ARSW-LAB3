package arsw.highlandersim;

import java.util.List;
import java.util.Random;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback=null;
    
    private int health;
    
    private int defaultDamageValue;

    private final List<Immortal> immortalsPopulation;

    private final String name;

    private final Random r = new Random(System.currentTimeMillis());

    private final Object lock;

    private boolean paused = false;
    private boolean play = true;


    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb, Object lock) {
        super(name);
        this.updateCallback=ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = health;
        this.defaultDamageValue=defaultDamageValue;
        this.lock = lock;
    }

    public void run() {

        while (immortalsPopulation.size() > 1 && play) {
            synchronized (lock){
                Immortal im;

                int myIndex = immortalsPopulation.indexOf(this);

                int nextFighterIndex = r.nextInt(immortalsPopulation.size());

                //avoid self-fight
                if (nextFighterIndex == myIndex) {
                    nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
                }

                im = immortalsPopulation.get(nextFighterIndex);


                    while (paused){
                        try{
                           lock.wait();
                        } catch (InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                    this.fight(im);
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        if (immortalsPopulation.size() == 1){
            updateCallback.processReport(immortalsPopulation.get(0).toString());
        }
    }

    public void fight(Immortal i2) {

        if (i2.getHealth() > 0) {
            i2.changeHealth(i2.getHealth() - defaultDamageValue);
            this.health += defaultDamageValue;
            updateCallback.processReport(this + " vs " + i2 + "\n");
        } else {
            immortalsPopulation.remove(i2);
            i2.pauseIm();
            updateCallback.processReport(i2 + " is dead\n");
        }

    }

    public void pauseIm() {
        this.paused = true;
    }

    public void resumeIm() {
        this.paused = false;
    }

    public void play(boolean play){
        this.play = play;
    }

    public void changeHealth(int v) {
        health = v;
    }

    public int getHealth() {
        return health;
    }

    @Override
    public String toString() {

        return name + "[" + health + "]";
    }
}
