package com.example.batchprocessing;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@Configuration
public class BatchConfiguration {

    // tag::readerwriterprocessor[]
    /**
     * 定义一个 FlatFileItemReader，用于读取 CSV 文件中的数据。
     * FlatFileItemReader 会将 CSV 文件中的数据映射为 Person 对象。
     * @return 返回配置好的 FlatFileItemReader 实例
     */
    @Bean
    public FlatFileItemReader<Person> reader() {
        return new FlatFileItemReaderBuilder<Person>()
            .name("personItemReader")  // 给 reader 起个名字，用于日志和调试
            .resource(new ClassPathResource("sample-data.csv"))  // 指定 CSV 文件的路径
            .delimited()  // 指定文件是分隔符分隔的
            .names("firstName", "lastName")  // 定义文件中列的名称，这里假设 CSV 文件有两列：firstName 和 lastName
            .targetType(Person.class)  // 设置目标类型为 Person 类，表示每一行数据会被映射成 Person 对象
            .build();
    }

    /**
     * 配置一个自定义的 ItemProcessor，用于处理每个 Person 对象。
     * 在处理过程中，您可以执行任何逻辑（例如数据转换、验证等）。
     * @return 返回配置好的 PersonItemProcessor 实例
     */
    @Bean
    public PersonItemProcessor processor() {
        return new PersonItemProcessor();  // 返回自定义的处理器对象
    }

    /**
     * 配置一个 JdbcBatchItemWriter，用于将处理后的数据写入数据库。
     * 这里的 SQL 语句会将每个 Person 对象的 firstName 和 lastName 插入到数据库的 `people` 表中。
     * @param dataSource 数据源，用于连接数据库
     * @return 返回配置好的 JdbcBatchItemWriter 实例
     */
    @Bean
    public JdbcBatchItemWriter<Person> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Person>()
            .sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)")  // 插入数据的 SQL 语句
            .dataSource(dataSource)  // 设置数据源
            .beanMapped()  // 自动映射 Person 对象的属性到 SQL 语句中的参数
            .build();
    }
    // end::readerwriterprocessor[]

    // tag::jobstep[]
    /**
     * 配置一个 Job，表示批处理任务。
     * 这里的任务包含一个步骤（step1），执行完 step1 后，任务结束。
     * @param jobRepository Job 存储库，用于持久化作业的状态和元数据
     * @param step1 步骤1，处理批处理任务的主要逻辑
     * @param listener 作业完成的通知监听器，用于作业执行后的回调操作（例如记录日志、发送通知等）
     * @return 返回配置好的 Job 实例
     * @throws Exception 
     */
    @Bean
    public Job importUserJob(JobRepository jobRepository, Step step1, JobCompletionNotificationListener listener) throws Exception {
        return new JobBuilder("importUserJob", jobRepository)  // 创建一个 Job 构建器
            .listener(listener)  // 注册监听器，用于作业完成时的通知
            .start(step1)  // 定义作业的第一个步骤
            .incrementer(new RunIdIncrementer())
            .build();  // 构建作业
    }

	/**
     * 配置一个 Step，表示批处理作业中的一个步骤。
     * 每个步骤会处理一批数据，在这个步骤中，我们设置了：
     * - 数据读取（reader）
     * - 数据处理（processor）
     * - 数据写入（writer）
     * @param jobRepository 作业存储库，用于持久化步骤的状态
     * @param transactionManager 事务管理器，用于控制步骤中的事务
     * @param reader 数据读取器，读取 CSV 文件中的数据
     * @param processor 数据处理器，处理读取的数据
     * @param writer 数据写入器，将处理后的数据写入数据库
     * @return 返回配置好的 Step 实例
     */
    @Bean
    public Step step1(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
                      FlatFileItemReader<Person> reader, PersonItemProcessor processor, JdbcBatchItemWriter<Person> writer) {
        return new StepBuilder("step1", jobRepository)  // 创建一个步骤构建器，步骤名为 step1
            .<Person, Person> chunk(3, transactionManager)  // 设置处理的每个批次的大小为 3，每次处理 3 条记录
            .reader(reader)  // 设置读取器
            .processor(processor)  // 设置处理器
            .writer(writer)  // 设置写入器
            .build();  // 构建步骤
    }
    // end::jobstep[]
}
/*

详细中文注释说明：
1. 数据读取器（FlatFileItemReader）：
通过 FlatFileItemReaderBuilder 配置了从 CSV 文件中读取数据，并将每一行的数据映射到 Person 类的实例。CSV 文件中的列 firstName 和 lastName 将对应到 Person 对象的属性。
2. 数据处理器（PersonItemProcessor）：
自定义的 PersonItemProcessor 用于处理每个读取到的 Person 对象。在这个示例中，它可以用来对每个 Person 对象进行修改、验证等操作（虽然此处未展示具体实现）。
3. 数据写入器（JdbcBatchItemWriter）：
使用 JdbcBatchItemWriterBuilder 配置了将处理后的 Person 对象写入数据库。写入的 SQL 语句插入每个 Person 对象的 firstName 和 lastName 到数据库表 people 中。
4. 作业配置（Job）：
通过 JobBuilder 配置了一个名为 importUserJob 的作业，它由一个步骤组成。在作业执行完后，会触发 JobCompletionNotificationListener 中定义的回调方法。
5. 步骤配置（Step）：
使用 StepBuilder 配置了一个步骤 step1，在这个步骤中执行数据的读取、处理和写入操作。通过 chunk(3) 配置了每次批量处理 3 条记录，DataSourceTransactionManager 用于管理事务。
6. 事务管理：
在批处理步骤中，使用 DataSourceTransactionManager 作为事务管理器，保证每次批处理操作的原子性。
总结：
此配置演示了如何使用 Spring Batch 读取 CSV 文件中的数据，经过处理后将其插入到数据库中。代码包含了配置读取器、处理器、写入器以及作业和步骤的设置。通过这种方式，您可以轻松地实现批量数据的导入、处理和持久化操作。


*/