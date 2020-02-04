package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.*;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback=null;
    
    private AtomicInteger health;
    
    private int defaultDamageValue;

    private final List<Immortal> immortalsPopulation;

    private final String name;

    private final Random r = new Random(System.currentTimeMillis());

    private boolean pausar = false;


    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue,
            ImmortalUpdateReportCallback ucb) {
        super(name);
        this.updateCallback=ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = new AtomicInteger(health);
        this.defaultDamageValue=defaultDamageValue;
    }

    public void run() {

        while (true) {
            Immortal im;

            int myIndex = immortalsPopulation.indexOf(this);

            int nextFighterIndex = r.nextInt(immortalsPopulation.size());

            synchronized(this){
                while (pausar){
                    try{
                        wait();
                    } catch(Exception e){

                    }
                }
            }

            //avoid self-fight
            if (nextFighterIndex == myIndex) {
                nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
            }

            im = immortalsPopulation.get(nextFighterIndex);

            this.fight(im);

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    public void pausar(){
        pausar = true;
    }

    public void reanudar(){
        pausar = false;
        synchronized(this){
            notifyAll();
        }
    }

    public void fight(Immortal i2) {
    	Immortal One = getId() > i2.getId() ? this : i2;
    	Immortal Two = getId() > i2.getId() ? i2 : this;
    	synchronized(One){
    		synchronized(Two){
    	
		        if (i2.getHealth().get() > 0) {
		            i2.getHealth().addAndGet(-defaultDamageValue);
		            this.health.addAndGet(defaultDamageValue);
		            updateCallback.processReport("Fight: " + this + " vs " + i2+"\n");
		        } else {
		            updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
		        }
    		}
    	}	
    }

    public void changeHealth(int v) {
        health.getAndSet(v);
    }

	public AtomicInteger getHealth() {
        return health;
    }

    @Override
    public String toString() {

        return name + "[" + health.toString() + "]";
    }

}
