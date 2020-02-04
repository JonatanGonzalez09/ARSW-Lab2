# ARSW-Lab2 #

**Realizado por: Jonatan Esteban Gonzalez Rodriguez y David Eduardo Caycedo **

# Part I - Before finishing class #

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

# Part II #
1. Review the “highlander-simulator” program, provided in the edu.eci.arsw.highlandersim package. This is a game in which:
- You have N immortal players. 
- Each player knows the remaining N-1 player.
- Each player permanently attacks some other immortal. The one who first attacks subtracts M life points from his opponent, and increases his own life points by the same amount.
- The game could never have a single winner. Most likely, in the end there are only two left, fighting indefinitely by removing and adding life points. 

2. Review the code and identify how the functionality indicated above was implemented. Given the intention of the game, an invariant should be that the sum of the life points of all players is always the same (of course, in an instant of time in which a time increase / reduction operation is not in process ). For this case, for N players, what should this value be?

_El valor de la suma debería ser_ **N(players)** _*_ **DEFAULT_IMMORTAL_HEALTH**

3. Run the application and verify how the ‘pause and check’ option works. Is the invariant fulfilled?

**El botón** _“pause and check”_ **retorna la suma total de todo los inmortales que se encuentran peleando.**

**El** _invariante no se cumple_ **ya que al instante que retorna el valor, los datos no son verdaderos porque el valor cambia debido a que hay un daño a algún inmortal.**

4. A first hypothesis that the race condition for this function (pause and check) is presented is that the program consults the list whose values it will print, while other threads modify their values. To correct this, do whatever is necessary so that, before printing the current results, all other threads are paused. Additionally, implement the ‘resume’ option.

![](https://github.com/JonatanGonzalez09/ARSW-Lab2/blob/master/Resources/Pause%26Check.jpg)

**Se implemento los botones de Pause and check, que pausa los hilos y retorna el valor correcto de los inmortales que hay y la suma de su vida y  el botón resume, que vuelve y reanuda todos los hilos del sistema.**

5. Check the operation again (click the button many times). Is the invariant fulfilled or not ?.

**Si se cumple el invariante ya que en cada iteración se va aumentando la vida de los immortlas.**

6. Identify possible critical regions in regards to the fight of the immortals. Implement a blocking strategy that avoids race conditions. Remember that if you need to use two or more ‘locks’ simultaneously, you can use nested synchronized blocks:

**La región critica se encuentra en el método figth() cuando se quiere cambiar el healt**

![](https://github.com/JonatanGonzalez09/ARSW-Lab2/blob/master/Resources/RegionCritica.jpg)

7. After implementing your strategy, start running your program, and pay attention to whether it comes to a halt. If so, use the jps and jstack programs to identify why the program stopped.



Utilizando la herramientas jps y jstack podemos evidenciar que utilizando la solucion sugerida en el enunciado de bloques sincronizados anidados se presento un Deadlock debido a que los inmortales necesitan acceder a recursos, pero en el momento estos estan bloqueados por otros inmortales, por lo que se detiene la ejecucion.
```
synchronized(locka){
	synchronized(lockb){
		…
	}
}
```
![](https://github.com/JonatanGonzalez09/ARSW-Lab2/blob/master/Resources/dead.PNG)

8. Consider a strategy to correct the problem identified above (you can review Chapter 15 of Java Concurrency in Practice again).



9. Once the problem is corrected, rectify that the program continues to function consistently when 100, 1000 or 10000 immortals are executed. If in these large cases the invariant begins to be breached again, you must analyze what was done in step 4.

10. An annoying element for the simulation is that at a certain point in it there are few living 'immortals' making failed fights with 'immortals' already dead. It is necessary to suppress the immortal dead of the simulation as they die. 
 - Analyzing the simulation operation scheme, could this create a race condition? Implement the functionality, run the simulation and see what problem arises when there are many 'immortals' in it. Write your conclusions about it in the file ANSWERS.txt.
 - Correct the previous problem WITHOUT using synchronization, since making access to the shared list of immortals sequential would make simulation extremely slow. 
 
 11. To finish, implement the STOP option.
