package jwm.ir.crawler;

import jwm.ir.utils.Db;
import jwm.ir.utils.DbImpl;
import jwm.ir.utils.HibernateUtil;
import org.hibernate.SessionFactory;

import java.util.concurrent.*;

/**
 * Created by Jeff on 2016-07-25.
 */
public class UrlFeedRunner {

    private final ScheduledExecutorService executorService;
    private final UrlFeed feed;

    public static void main(String[] args) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Db db = new DbImpl(sessionFactory);
        final BlockingQueue<String> out = new LinkedBlockingQueue<>();
        UrlFeed feeder = new UrlFeed(db, out);
        UrlFeedRunner x = new UrlFeedRunner(executorService, feeder);
        x.start();


        Runnable listener = new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        System.out.println(out.take());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread t = new Thread(listener);
        t.start();
    }

    public UrlFeedRunner(ScheduledExecutorService executorService,
                         UrlFeed feed) {
        this.executorService = executorService;
        this.feed = feed;
    }

    public void start() {

        long initialDelay = 0;
        long delay = 5;
        TimeUnit timeUnit = TimeUnit.SECONDS;

        executorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                feed.process();
            }
        }, initialDelay, delay, timeUnit);
    }
}
