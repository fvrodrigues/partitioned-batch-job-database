package com.batch.database.configuration;

import com.batch.database.domain.Customer;
import com.batch.database.mapper.CustomerRowMapper;
import com.batch.database.partitioner.ColumnRangePartitioner;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.deployer.resource.support.DelegatingResourceLoader;
import org.springframework.cloud.deployer.spi.task.TaskLauncher;
import org.springframework.cloud.task.batch.partition.DeployerPartitionHandler;
import org.springframework.cloud.task.batch.partition.DeployerStepExecutionHandler;
import org.springframework.cloud.task.batch.partition.PassThroughCommandLineArgsProvider;
import org.springframework.cloud.task.batch.partition.SimpleEnvironmentVariablesProvider;
import org.springframework.cloud.task.repository.TaskRepository;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class JobConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public JobRepository jobRepository;

    @Autowired
    private ConfigurableApplicationContext context;

    @Autowired
    private DelegatingResourceLoader resourceLoader;

    @Autowired
    private Environment environment;

    @Autowired
    private DataSource dataSource;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Value("${particionamento.image}")
    private String image;

    @Bean
    public JobRepository getJobRepository() throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTransactionManager(getTransactionManager()	);
        factory.setIsolationLevelForCreate("ISOLATION_READ_UNCOMMITTED");
        factory.afterPropertiesSet();
        return (JobRepository) factory.getObject();
    }

    private PlatformTransactionManager getTransactionManager() {
        return new ResourcelessTransactionManager();
    }

    @Bean
    public PartitionHandler partitionHandler(TaskLauncher taskLauncher, JobExplorer jobExplorer, TaskRepository taskRepository) throws Exception {
        Resource resource = this.resourceLoader.getResource(image);
        DeployerPartitionHandler partitionHandler = new DeployerPartitionHandler(taskLauncher, jobExplorer, resource, "workerStep");
        List<String> commandLineArgs = new ArrayList<>(3);
        commandLineArgs.add("--spring.profiles.active=worker");
        commandLineArgs.add("--spring.cloud.task.initialize-enabled=false");
        commandLineArgs.add("--spring.batch.initializer.enabled=false");
        partitionHandler.setCommandLineArgsProvider(new PassThroughCommandLineArgsProvider(commandLineArgs));
        partitionHandler.setEnvironmentVariablesProvider(new SimpleEnvironmentVariablesProvider(this.environment));
        partitionHandler.setMaxWorkers(2);
        partitionHandler.setApplicationName("PartitionedBatchJobTask");
        return partitionHandler;
    }

    @Bean
    @Profile("worker")
    public DeployerStepExecutionHandler stepExecutionHandler(JobExplorer jobExplorer) {
        return new DeployerStepExecutionHandler(this.context, jobExplorer, this.jobRepository);
    }

    @Bean
    public ColumnRangePartitioner partitioner() {
        return new ColumnRangePartitioner();
    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<Customer> pagingItemReader(@Value("#{stepExecutionContext['particao']}") Integer particao ,
                                                           @Value("#{stepExecutionContext['minValue']}") Long minValue ,
                                                           @Value("#{stepExecutionContext['maxValue']}") Long maxValue) {

        if (particao == 4) {
            throw new RuntimeException("FORCANDO ERRO");
        }

        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id", Order.ASCENDING);
        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("id, firstName, lastName, birthdate");
        queryProvider.setFromClause("from customer");
        queryProvider.setWhereClause("where id >= " + minValue + " and id < " + maxValue);
        queryProvider.setSortKeys(sortKeys);
        JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(this.dataSource);
        reader.setFetchSize(1000);
        reader.setRowMapper(new CustomerRowMapper());
        reader.setQueryProvider(queryProvider);
        return reader;
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<Customer> customerItemWriter() {
        JdbcBatchItemWriter<Customer> itemWriter = new JdbcBatchItemWriter<>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("INSERT INTO new_customer VALUES (:id, :firstName, :lastName, :birthdate)");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        itemWriter.afterPropertiesSet();
        return itemWriter;
    }

    @Bean
    public Step workerStep() {
        return stepBuilderFactory.get("workerStep")
                .<Customer, Customer>chunk(10000)
                .reader(pagingItemReader(null,null, null))
                .writer(customerItemWriter())
                .build();
    }

    @Bean
    public Step splitWorkers(PartitionHandler partitionHandler) throws Exception {
        return this.stepBuilderFactory.get("step1")
                .partitioner(workerStep().getName(), partitioner())
                .step(workerStep())
                .partitionHandler(partitionHandler)
                .build();
    }

    @Bean
    @Profile("!worker")
    public Job partitionedJob(PartitionHandler partitionHandler) throws Exception {
        return this.jobBuilderFactory.get("partitionedJob")
                   .start(splitWorkers(partitionHandler))
                   .build();
    }

}
