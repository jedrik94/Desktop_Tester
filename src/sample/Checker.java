package sample;

import java.util.Map;

/**
 * Created by Komputer on 6/22/2017.
 */
public class Checker extends Thread {

    private Map<String, Process> processes;

    public Checker(Map<String, Process> processes) {
        this.processes = processes;
    }

    @Override
    public void run() {

        while (areProcessAlive()) {
            //System.out.println("run");
        }
    }

    private boolean areProcessAlive() {
        boolean areAlive = true;

        for (String keyName : processes.keySet()) {
            areAlive = areAlive && processes.get(keyName).isAlive();
        }
        return areAlive;
    }
}
