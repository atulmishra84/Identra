package com.identra.provisioning.config;

import com.identra.provisioning.temporal.ProvisioningActivitiesImpl;
import com.identra.provisioning.temporal.ProvisioningWorkflowImpl;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "identra.temporal.enabled", havingValue = "true")
public class TemporalConfig {

    public static final String TASK_QUEUE = "identra-provisioning";

    private WorkerFactory workerFactory;

    @Bean(destroyMethod = "shutdown")
    public WorkflowServiceStubs workflowServiceStubs(
            @Value("${identra.temporal.target:127.0.0.1:7233}") String target
    ) {
        return WorkflowServiceStubs.newServiceStubs(
                WorkflowServiceStubsOptions.newBuilder().setTarget(target).build()
        );
    }

    @Bean
    public WorkflowClient workflowClient(
            WorkflowServiceStubs stubs,
            @Value("${identra.temporal.namespace:default}") String namespace
    ) {
        return WorkflowClient.newInstance(
                stubs,
                WorkflowClientOptions.newBuilder().setNamespace(namespace).build()
        );
    }

    @Bean
    public WorkerFactory workerFactory(
            WorkflowClient client,
            ProvisioningActivitiesImpl activities
    ) {
        WorkerFactory factory = WorkerFactory.newInstance(client);
        Worker worker = factory.newWorker(TASK_QUEUE);
        worker.registerWorkflowImplementationTypes(ProvisioningWorkflowImpl.class);
        worker.registerActivitiesImplementations(activities);
        factory.start();
        this.workerFactory = factory;
        return factory;
    }

    @PreDestroy
    public void shutdown() {
        if (workerFactory != null) {
            workerFactory.shutdown();
        }
    }
}
