# ARSW-Lab2 #

**Thread control with wait/notify. Producer/consumer**

1. Check the operation of the program and run it. While this occurs, run jVisualVM and check the CPU consumption of the corresponding process.

![](https://github.com/JonatanGonzalez09/ARSW-Lab2/blob/master/Resources/primera.jpg)

**Why is this consumption?**
Porque el consumidor no deja que la lista se llene, en cada iteracion y siempre se esta preguntando que la lista tenga elementos, por loq ue evidenciamos un aumento de consumo en la CPU.

**Which is the responsible class?**
La clase responsable es ```Consumer.java``` ya que siempre esta preguntando si la cola tiene elementos. 

2. Make the necessary adjustments so that the solution uses the CPU more efficiently, taking into account that - for now - production is slow and consumption is fast. Verify with JVisualVM that the CPU consumption is reduced.
**Clase Consumidor**
```
public class Consumer extends Thread{
    private Queue<Integer> queue;
    public Consumer(Queue<Integer> queue){
        this.queue=queue;        
    }
    
    @Override
    public void run() {
        while (true) {
            synchronized(queue){
                while (queue.isEmpty()) {
                    try {
                        queue.wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
                }
                int elem=queue.poll();
                System.out.println("Consumer consumes "+elem);
            }
        }
    }
}
```
**Clase Productor**
```
public class Producer extends Thread {
    private Queue<Integer> queue = null;
    private int dataSeed = 0;
    private Random rand=null;
    private final long stockLimit;
    
    public Producer(Queue<Integer> queue,long stockLimit) {
        this.queue = queue;
        rand = new Random(System.currentTimeMillis());
        this.stockLimit=stockLimit;
    }

    @Override
    public void run() {
        while (true) {
            dataSeed = dataSeed + rand.nextInt(100);
            System.out.println("Producer added " + dataSeed);
            synchronized(queue){
                queue.add(dataSeed);
                queue.notifyAll();
            }
       
        }
    }
}
```

3. Make the producer now produce very fast, and the consumer consumes slow. Taking into account that the producer knows a Stock limit (how many elements he should have, at most in the queue), make that limit be respected. Review the API of the collection used as a queue to see how to ensure that this limit is not exceeded. Verify that, by setting a small limit for the 'stock', there is no high CPU consumption or errors.
