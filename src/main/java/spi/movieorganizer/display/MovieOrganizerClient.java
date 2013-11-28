package spi.movieorganizer.display;

import java.awt.EventQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import spi.movieorganizer.repository.MovieOrganizerControllerRepository;
import spi.movieorganizer.repository.MovieOrganizerDataManagerRepository;
import exane.osgi.jexlib.core.action.Factoryable;
import exane.osgi.jexlib.core.concurrency.ThreadTaskManager;

public class MovieOrganizerClient {
    public static final ThreadGroup                   THREADGROUP_MOVIEORGANIZER = new ThreadGroup("MovieOrganizer");

    private final MovieOrganizerControllerRepository  controllerRepository;
    private final MovieOrganizerDataManagerRepository dataManagerRepository;
    private final Executor                            updatesExecutor;
    private ThreadTaskManager                         clientThreadManager;

    public MovieOrganizerClient() {
        this.updatesExecutor = new Executor() {
            @Override
            public void execute(final Runnable command) {
                EventQueue.invokeLater(command);
            }
        };

        final ScheduledExecutorService scheduleExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(final Runnable runner) {
                return new Thread(MovieOrganizerClient.THREADGROUP_MOVIEORGANIZER, runner, "Scheduled Executor");
            }
        });
        final Factoryable<ExecutorService, String> externalServiceFactory = new Factoryable<ExecutorService, String>() {
            @Override
            public ExecutorService create(final String externalGroupId) {
                return Executors.newSingleThreadExecutor(new ThreadFactory() {
                    @Override
                    public Thread newThread(final Runnable runner) {
                        return new Thread(MovieOrganizerClient.THREADGROUP_MOVIEORGANIZER, runner, "External Executor " + externalGroupId + ")");
                    }
                });
            }
        };
        this.clientThreadManager = new ThreadTaskManager(this.updatesExecutor, externalServiceFactory, scheduleExecutorService);

        this.dataManagerRepository = new MovieOrganizerDataManagerRepository();
        this.controllerRepository = new MovieOrganizerControllerRepository(this);

    }

    public MovieOrganizerDataManagerRepository getDataManagerRepository() {
        return this.dataManagerRepository;
    }

    public MovieOrganizerControllerRepository getControllerRepository() {
        return this.controllerRepository;
    }

    public ThreadTaskManager getClientThreadManager() {
        return this.clientThreadManager;
    }

    public Executor getUpdatesExecutor() {
        return this.updatesExecutor;
    }

    public static class ActionExecutor {

        private final Executor executor;

        public ActionExecutor() {
            this.executor = Executors.newFixedThreadPool(8, new ThreadFactory() {

                @Override
                public Thread newThread(final Runnable r) {
                    final Thread thread = new Thread(r);
                    thread.setContextClassLoader(this.getClass().getClassLoader());
                    return thread;
                }
            });
        }

        public void execute(final Runnable runnable) {
            this.executor.execute(runnable);
        }
    }
}
