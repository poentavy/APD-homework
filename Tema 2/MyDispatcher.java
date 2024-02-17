import java.util.List;

public class MyDispatcher extends Dispatcher {
    private int lastDispatchedHostIndex;
    private final Object lock = new Object();
    public MyDispatcher(SchedulingAlgorithm algorithm, List<Host> hosts) {
        super(algorithm, hosts);
        lastDispatchedHostIndex = -1;
    }

    protected int getLastDispatchedHostIndex() {
        return lastDispatchedHostIndex;
    }

    protected void setLastDispatchedHostIndex(int index) {
        lastDispatchedHostIndex = index;
    }

    protected List<Host> getHostList() {
        return hosts;
    }

    protected SchedulingAlgorithm getSchedulingAlgorithm() {
        return algorithm;
    }

    @Override
    public void addTask(Task task) {
        switch (getSchedulingAlgorithm()) {
            case ROUND_ROBIN:
                roundRobin(task);
                break;
            case SHORTEST_QUEUE:
                shortestQueue(task);
                break;
            case SIZE_INTERVAL_TASK_ASSIGNMENT:
                sizeIntervalTaskAssignment(task);
                break;
            case LEAST_WORK_LEFT:
                leastWorkLeft(task);
                break;
            default:
                throw new IllegalArgumentException("Invalid scheduling algorithm");
        }
    }

    private void roundRobin(Task task) {
        // calculez indexul
        int nextHost = (getLastDispatchedHostIndex() + 1) % getHostList().size();
        Host host = null;
        synchronized(this) {
            // iau hostul
            host = getHostList().get(nextHost);
        }
        // adaug in coada
        host.addTask(task);
        setLastDispatchedHostIndex(nextHost);
    }

    private void shortestQueue(Task task) {
        Host bestChoice = null;
        for (Host host : this.hosts) {
            // aleg nodul cu coada cea mai mica
            if (bestChoice == null || bestChoice.getQueueSize() > (host.getQueueSize())) {
                bestChoice = host;
            }
        }

        if (bestChoice != null) {
            bestChoice.addTask(task);
        }
    }
    

    private void sizeIntervalTaskAssignment(Task task) {
        switch(task.getType())
        {
            // aleg tipul taskului si trimit nodului respectiv
            case SHORT:
                this.hosts.get(0).addTask(task);
                break;
            case MEDIUM:
                this.hosts.get(1).addTask(task);
                break;
            case LONG:
                this.hosts.get(2).addTask(task);
                break;
        }
    }

    private void leastWorkLeft(Task task) {
        Host bestChoice = hosts.get(0);
        for (Host host : this.hosts) {
            // aleg nodul cu durata mai mica
            if (bestChoice.getWorkLeft() > host.getWorkLeft()) {
                bestChoice = host;
            } else if (bestChoice.getWorkLeft() == host.getWorkLeft() && bestChoice.getId() > host.getId()) {
                // in caz de egalitate, alege nodul cu ID-ul mai mic
                bestChoice = host;
            }
        }
        bestChoice.addTask(task);
    }
}
