package pheninux.xdev.ticketcompare.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.Executor;

/**
 * Configuration pour les tâches planifiées et l'exécution asynchrone
 *
 * @EnableScheduling : Active le support des @Scheduled (tâches cron)
 * @EnableAsync : Active le support des @Async (emails non-bloquants)
 */
@Configuration
@EnableScheduling
@EnableAsync
public class SchedulingConfig {

    /**
     * Configuration du pool de threads pour les tâches planifiées
     * Utilisé par @Scheduled
     */
    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5); // 5 threads pour les tâches planifiées
        scheduler.setThreadNamePrefix("scheduled-task-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(30);
        scheduler.initialize();
        return scheduler;
    }

    /**
     * Configuration du pool de threads pour l'exécution asynchrone
     * Utilisé par @Async (notamment pour les emails)
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10); // 10 threads minimum
        executor.setMaxPoolSize(20); // 20 threads maximum
        executor.setQueueCapacity(100); // File d'attente de 100 tâches
        executor.setThreadNamePrefix("async-task-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }
}

