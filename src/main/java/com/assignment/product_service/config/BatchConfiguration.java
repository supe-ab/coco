package com.assignment.product_service.config;

import com.assignment.product_service.vo.ItemVO;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class BatchConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(BatchConfiguration.class);

    // ---------- Import CSV to DB (Chunk-based) ----------
    @Bean
    @Qualifier("csvItemReader")
    public FlatFileItemReader<ItemVO> csvItemReader() {
        return new FlatFileItemReaderBuilder<ItemVO>()
                .name("itemReader")
                .resource(new ClassPathResource("items.csv"))
                .delimited()
                .names("id", "name", "quantity")
                .linesToSkip(1) // Skip header row
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(ItemVO.class);
                }})
                .build();
    }

    @Bean
    public ItemProcessor<ItemVO, ItemVO> csvItemProcessor() {
        return item -> {
            logger.info("Processing item: {}", item);
            // Remove ID to let DB generate it
            item.setId(null);
            item.setName(item.getName().trim().toUpperCase());
            if (item.getQuantity() < 0) {
                item.setQuantity(0);
            }
            return item;
        };
    }

    @Bean
    public JpaItemWriter<ItemVO> databaseItemWriter(EntityManagerFactory entityManagerFactory) {
        JpaItemWriter<ItemVO> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

    @Bean
    @Qualifier("csvImportStep")
    public Step csvImportStep(JobRepository jobRepository,
                            PlatformTransactionManager transactionManager,
                            @Qualifier("csvItemReader") FlatFileItemReader<ItemVO> reader,
                            ItemProcessor<ItemVO, ItemVO> processor,
                            JpaItemWriter<ItemVO> writer) {
        return new StepBuilder("csvImportStep", jobRepository)
                .<ItemVO, ItemVO>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(10)
                .listener(new StepExecutionListener() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        logger.info("Starting CSV import step");
                    }
                    
                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        logger.info("Finished CSV import step. Status: {}", stepExecution.getStatus());
                        return stepExecution.getExitStatus();
                    }
                })
                .build();
    }

    @Bean
    @Qualifier("importCsvToDbJob")
    public Job importCsvToDbJob(JobRepository jobRepository, 
                               @Qualifier("csvImportStep") Step csvImportStep) {
        return new JobBuilder("importCsvToDbJob", jobRepository)
                .start(csvImportStep)
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        logger.info("Starting CSV import job");
                    }
                    
                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        logger.info("Finished CSV import job. Status: {}", jobExecution.getStatus());
                    }
                })
                .build();
    }

    // ---------- Export DB to CSV (Chunk-based) ----------
    @Bean
    public JdbcCursorItemReader<ItemVO> databaseReader(DataSource dataSource) {
        JdbcCursorItemReader<ItemVO> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("SELECT id, name, quantity FROM item");
        reader.setRowMapper(new BeanPropertyRowMapper<>(ItemVO.class));
        return reader;
    }

    @Bean
    public FlatFileItemWriter<ItemVO> csvFileWriter() {
        File outputDirectory = new File("./batch-output");
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }

        return new FlatFileItemWriterBuilder<ItemVO>()
                .name("itemWriter")
                .resource(new FileSystemResource("./batch-output/items.csv"))
                .delimited()
                .delimiter(",")
                .names("id", "name", "quantity")
                .headerCallback(writer -> writer.write("id,name,quantity"))
                .build();
    }

    @Bean
    @Qualifier("exportItemsStep")
    public Step exportItemsStep(JobRepository jobRepository,
                              PlatformTransactionManager transactionManager,
                              JdbcCursorItemReader<ItemVO> reader,
                              FlatFileItemWriter<ItemVO> writer) {
        return new StepBuilder("exportItemsStep", jobRepository)
                .<ItemVO, ItemVO>chunk(10, transactionManager)
                .reader(reader)
                .writer(writer)
                .build();
    }

    @Bean
    @Qualifier("exportItemsJob")
    public Job exportItemsJob(JobRepository jobRepository, 
                             @Qualifier("exportItemsStep") Step exportItemsStep) {
        return new JobBuilder("exportItemsJob", jobRepository)
                .start(exportItemsStep)
                .build();
    }

    // ---------- Import CSV to DB (Tasklet-based) ----------
    @Bean
    @Qualifier("readerTasklet")
    public Tasklet readerTasklet(@Qualifier("csvItemReader") FlatFileItemReader<ItemVO> reader) {
        return (contribution, chunkContext) -> {
            List<ItemVO> items = new ArrayList<>();
            ItemVO item;

            try {
                reader.open(contribution.getStepExecution().getExecutionContext());
                while ((item = reader.read()) != null) {
                    logger.info("Read item from CSV: {}", item);
                    items.add(item);
                }
            } catch (Exception e) {
                logger.error("Error reading from CSV: ", e);
                throw e;
            } finally {
                reader.close();
            }

            if (items.isEmpty()) {
                logger.warn("No items read from CSV file");
                throw new IllegalStateException("No items read from CSV file");
            } else {
                logger.info("Read {} items from CSV file", items.size());
            }

            // Store directly in JobExecutionContext
            ExecutionContext jobExecutionContext = contribution.getStepExecution()
                .getJobExecution().getExecutionContext();
            jobExecutionContext.put("items", (Serializable) items);

            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    @Qualifier("processorTasklet")
    public Tasklet processorTasklet() {
        return (contribution, chunkContext) -> {
            ExecutionContext jobExecutionContext = contribution.getStepExecution()
                .getJobExecution().getExecutionContext();

            @SuppressWarnings("unchecked")
            List<ItemVO> items = (List<ItemVO>) jobExecutionContext.get("items");

            if (items == null || items.isEmpty()) {
                logger.error("No items found in job execution context");
                throw new IllegalStateException("No items to process");
            }

            List<ItemVO> processedItems = new ArrayList<>();
            for (ItemVO item : items) {
                // Remove ID to let DB generate it
                item.setId(null);
                item.setName(item.getName().trim().toUpperCase());
                if (item.getQuantity() < 0) {
                    item.setQuantity(0);
                }
                processedItems.add(item);
                logger.info("Processed item: {}", item);
            }

            // Store processed items directly
            jobExecutionContext.put("processedItems", (Serializable) processedItems);
            logger.info("Processed {} items", processedItems.size());

            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    @Qualifier("writerTasklet")
    public Tasklet writerTasklet(EntityManagerFactory entityManagerFactory) {
        return (contribution, chunkContext) -> {
            ExecutionContext jobExecutionContext = contribution.getStepExecution()
                .getJobExecution().getExecutionContext();

            @SuppressWarnings("unchecked")
            List<ItemVO> items = (List<ItemVO>) jobExecutionContext.get("processedItems");

            if (items == null || items.isEmpty()) {
                logger.error("No items found in job execution context");
                throw new IllegalStateException("No items to write");
            }

            // Write each item as a separate entity
            JpaItemWriter<ItemVO> writer = new JpaItemWriter<>();
            writer.setEntityManagerFactory(entityManagerFactory);
            writer.afterPropertiesSet();

            try {
                for (ItemVO item : items) {
                    writer.write(Chunk.of(item)); // Write each entity individually
                    logger.info("Written item to database: {}", item);
                }
            } catch (Exception e) {
                logger.error("Error writing to database: ", e);
                throw e;
            }

            return RepeatStatus.FINISHED;
        };
    }


    @Bean
    @Qualifier("taskletReaderStep")
    public Step taskletReaderStep(JobRepository jobRepository,
                                  PlatformTransactionManager transactionManager,
                                  @Qualifier("readerTasklet") Tasklet readerTasklet) {
        return new StepBuilder("taskletReaderStep", jobRepository)
                .tasklet(readerTasklet, transactionManager)
                .build();
    }

    @Bean
    @Qualifier("taskletProcessorStep")
    public Step taskletProcessorStep(JobRepository jobRepository,
                                     PlatformTransactionManager transactionManager,
                                     @Qualifier("processorTasklet") Tasklet processorTasklet) {
        return new StepBuilder("taskletProcessorStep", jobRepository)
                .tasklet(processorTasklet, transactionManager)
                .build();
    }

    @Bean
    @Qualifier("taskletWriterStep")
    public Step taskletWriterStep(JobRepository jobRepository,
                                  PlatformTransactionManager transactionManager,
                                  @Qualifier("writerTasklet") Tasklet writerTasklet) {
        return new StepBuilder("taskletWriterStep", jobRepository)
                .tasklet(writerTasklet, transactionManager)
                .build();
    }

    @Bean
    @Qualifier("taskletBasedJob")
    public Job taskletBasedJob(JobRepository jobRepository,
                               @Qualifier("taskletReaderStep") Step taskletReaderStep,
                               @Qualifier("taskletProcessorStep") Step taskletProcessorStep,
                               @Qualifier("taskletWriterStep") Step taskletWriterStep) {
        return new JobBuilder("taskletBasedJob", jobRepository)
                .start(taskletReaderStep)
                .next(taskletProcessorStep)
                .next(taskletWriterStep)
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        logger.info("Starting tasklet-based import job");
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        logger.info("Finished tasklet-based import job. Status: {}", jobExecution.getStatus());
                    }
                })
                .build();
    }


 // ---------- Export DB to CSV (Tasklet-based) ----------
    @Bean
    @Qualifier("dbReaderTasklet")
    public Tasklet dbReaderTasklet(DataSource dataSource) {
        return (contribution, chunkContext) -> {
            List<ItemVO> items = new ArrayList<>();
            JdbcCursorItemReader<ItemVO> reader = new JdbcCursorItemReader<>();
            reader.setDataSource(dataSource);
            reader.setSql("SELECT id, name, quantity FROM item");
            reader.setRowMapper(new BeanPropertyRowMapper<>(ItemVO.class));
            
            // Initialize the reader
            reader.afterPropertiesSet();
            
            ExecutionContext executionContext = contribution.getStepExecution().getExecutionContext();
            reader.open(executionContext);
            
            try {
                ItemVO item;
                while ((item = reader.read()) != null) {
                    items.add(item);
                }
                
                contribution.getStepExecution().getJobExecution()
                    .getExecutionContext()
                    .put("items", items);
                
                // Add logging
                if (items.isEmpty()) {
                    throw new IllegalStateException("No items found in database");
                }
                
            } finally {
                reader.close();
            }
            
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    @Qualifier("csvWriterTasklet")
    public Tasklet csvWriterTasklet() {
        return (contribution, chunkContext) -> {
            // Get items from job execution context instead of step execution context
            ExecutionContext jobContext = contribution.getStepExecution()
                .getJobExecution()
                .getExecutionContext();
            
            @SuppressWarnings("unchecked")
            List<ItemVO> items = (List<ItemVO>) jobContext.get("items");
            
            if (items == null || items.isEmpty()) {
                throw new IllegalStateException("No items available for writing to CSV");
            }
            
            File outputDirectory = new File("./batch-output");
            if (!outputDirectory.exists()) {
                outputDirectory.mkdirs();
            }

            FlatFileItemWriter<ItemVO> writer = new FlatFileItemWriterBuilder<ItemVO>()
                    .name("taskletItemWriter")
                    .resource(new FileSystemResource("./batch-output/exported_items.csv"))
                    .delimited()
                    .delimiter(",")
                    .names("id", "name", "quantity")
                    .headerCallback(headerWriter -> headerWriter.write("id,name,quantity"))
                    .shouldDeleteIfExists(true)
                    .build();

            writer.afterPropertiesSet();
            writer.open(contribution.getStepExecution().getExecutionContext());
            
            try {
                for (ItemVO item : items) {
                    writer.write(Chunk.of(item));
                }
            } finally {
                writer.close();
            }
            
            return RepeatStatus.FINISHED;
        };
    }
    @Bean
    @Qualifier("taskletDbReaderStep")
    public Step taskletDbReaderStep(JobRepository jobRepository,
                                  PlatformTransactionManager transactionManager,
                                  @Qualifier("dbReaderTasklet") Tasklet dbReaderTasklet) {
        return new StepBuilder("taskletDbReaderStep", jobRepository)
                .tasklet(dbReaderTasklet, transactionManager)
                .build();
    }

    @Bean
    @Qualifier("taskletCsvWriterStep")
    public Step taskletCsvWriterStep(JobRepository jobRepository,
                                   PlatformTransactionManager transactionManager,
                                   @Qualifier("csvWriterTasklet") Tasklet csvWriterTasklet) {
        return new StepBuilder("taskletCsvWriterStep", jobRepository)
                .tasklet(csvWriterTasklet, transactionManager)
                .build();
    }

    @Bean
    @Qualifier("taskletBasedExportJob")
    public Job taskletBasedExportJob(JobRepository jobRepository,
                                    @Qualifier("taskletDbReaderStep") Step taskletDbReaderStep,
                                    @Qualifier("taskletCsvWriterStep") Step taskletCsvWriterStep) {
        return new JobBuilder("taskletBasedExportJob", jobRepository)
                .start(taskletDbReaderStep)
                .next(taskletCsvWriterStep)
                .build();
    }

}