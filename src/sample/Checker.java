package sample;

import java.util.List;

/**
 * Created by Komputer on 6/22/2017.
 */
public class Checker extends Thread {

    private List<Process> processes;

    public Checker(List<Process> processes) {
        this.processes = processes;
    }

    @Override
    public void run() {
        while (areProcessAlive()) ;
    }

    private boolean areProcessAlive() {
        return processes.stream().anyMatch(Process::isAlive);
    }
}
