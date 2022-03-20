package PaceMaker;
//60 – 100 beats per minute.
import java.util.LinkedList;
import java.util.concurrent.*;
import java.util.Random;

public class MainClass {
    
    public static void main(String[] args) {
        
        LinkedBlockingQueue<HeartRate> Heartrate = new LinkedBlockingQueue<HeartRate>();
        LinkedBlockingQueue<HeartRate> Monitor = new LinkedBlockingQueue<HeartRate>();
        
        HeartRate Producer = new HeartRate(1, Heartrate);
        HeartRateMonitor Consumer = new HeartRateMonitor(Producer, Heartrate, Monitor);
        
        ScheduledExecutorService ThreadPool = Executors.newScheduledThreadPool(2);
        ThreadPool.scheduleAtFixedRate(Producer,0, 1, TimeUnit.SECONDS);
        ThreadPool.scheduleAtFixedRate(Consumer,0, 2, TimeUnit.SECONDS);
        
    }
    
    static class HeartRate implements Runnable {
        
        public int HR;
        LinkedBlockingQueue<HeartRate> Heartrate;
        
        public HeartRate(int hr, LinkedBlockingQueue<HeartRate> Heartrate)
        {
            HR = hr;
            this.Heartrate = Heartrate;
        }
        
        public void run() {
            
           while(true) {
               
            try{Thread.sleep(500);}catch(Exception e){}
            int upper = 150; int lower = 40;
            int HeartRateReading = (int) (Math.random() * (upper - lower));
            HeartRate H = new HeartRate(HeartRateReading, Heartrate);
            System.out.println("[---] Heart rate sent to the monitor [---]");
            Heartrate.add(H);
            
               
           } 
            
            
        }
        
    }
    
    static class HeartRateMonitor implements Runnable {
        
        LinkedBlockingQueue<HeartRate> Heartrate;
        LinkedBlockingQueue<HeartRate> Monitor; 
        HeartRate HM;
        ExecutorService EC = Executors.newFixedThreadPool(1);
        
        public HeartRateMonitor(HeartRate HM, LinkedBlockingQueue<HeartRate> Heartrate, LinkedBlockingQueue<HeartRate> Monitor)
        {
            this.Heartrate = Heartrate;
            this.Monitor = Monitor;
        }
        
        public void run () {
            
            while(true) {
                
                try{
                    
                    Thread.sleep(1000);
                    HM = Heartrate.take();
                    System.out.println("[***] Heart-rate reading : "+ HM.HR+" [***]");
                    Monitor.add(HM);
                    
                    if(HM.HR <= 60 || HM.HR >= 100)
                    {
                        EC.submit(new PaceMaker());
                    }
                   
                }catch(Exception e){}
              
            }
            
            
        }
    }
    
    static class PaceMaker implements Callable {
        
        public PaceMaker call()
        {
            System.out.println("[===========================]");
            System.out.println("[ DETECTED ABNORMAL READING ]");
            System.out.println("[    PACEMAKER ACTIVATED    ]");
            System.out.println("[===========================]");
            return null;

        }
    }
}
