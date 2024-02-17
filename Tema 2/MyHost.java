import java.util.Comparator;
import java.util.PriorityQueue;

public class MyHost extends Host {
    // coada de prioritati in care stochez taskurile
    private PriorityQueue<Task> taskQueue = new PriorityQueue<>(new Comparator<Task>() {
        @Override
        public int compare(Task t1, Task t2) {
            if (t1.getPriority() == t2.getPriority()) {
                return Long.compare(t1.getStart(), t2.getStart());
            }
            return Integer.compare(t2.getPriority(), t1.getPriority());
        }
    });
        // variabila pentru a inceta rularea thread-urilor
        private boolean isFinished = false;
        // task-ul care se executa
        public Task runningTask = null;
    @Override
    public void run() 
    {
        while(!isFinished || getQueueSize() != 0)
        {
            if (getQueueSize() != 0) {
                runningTask = this.taskQueue.poll();
                long currentTime = (long)(Timer.getTimeDouble() * 1000);
                if (runningTask.getStart() <= currentTime) {
                    try 
                        {
                            long maxDuration = runningTask.getLeft();
                            runningTask = runningTask;
                            for (long i = 0; i < maxDuration && !isFinished; i += 1000) {
                                // cazul in care e preemptabil si schimb taskurile
                                if (runningTask.isPreemptible() && taskQueue.size() != 0 && this.taskQueue.peek().getPriority() > runningTask.getPriority()) {
                                    addTask(runningTask);
                                    runningTask = null;
                                    break;
                                }
                                // cazul in care nu e preemptabil, dar prioritatile difera si schimb task-urile
                                if ((runningTask.getDuration() == runningTask.getLeft()) && taskQueue.size() != 0 && this.taskQueue.peek().getPriority() > runningTask.getPriority()) {
                                    addTask(runningTask);
                                    runningTask = null;
                                    break;
                                }
                                // modific timpul ramas pe task-ul curent
                                synchronized(runningTask) {
                                    runningTask.setLeft(runningTask.getLeft() - 1000);

                                }
                                Thread.sleep(1000); // Modificare aici
                            }
                            if (runningTask != null) {
                                runningTask.finish();
                                runningTask = null;
                            }
    
                    } catch (InterruptedException e) 
                        {
                            System.err.println("Error occured in thread");
                    }
                }
            }
        }    
    }
    


    @Override
    public void addTask(Task task) {
        synchronized(this.taskQueue) {
            taskQueue.add(task);
        }
    }

    @Override
    public int getQueueSize() {
        synchronized(this.taskQueue) {
            if (runningTask == null) {
                return taskQueue.size();
            } else {
                // cazul pentru SQ
                return taskQueue.size() + 1;
            }
        }
    }

    @Override
    public long getWorkLeft() {
        synchronized (taskQueue) {
            if (runningTask == null) {
                return this.taskQueue.stream().map(Task::getLeft).reduce(0L, Long::sum);
            } else {
                return this.taskQueue.stream().map(Task::getLeft).reduce(0L, Long::sum) + runningTask.getLeft();
            }
        }
    }

    @Override
    public void shutdown() {
        // variabila se pune pe true cand thread-urile au terminat executia
        isFinished = true;
    }
}
