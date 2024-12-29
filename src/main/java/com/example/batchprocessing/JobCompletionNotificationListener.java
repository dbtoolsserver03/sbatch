package com.example.batchprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

    // 创建日志对象，记录日志
    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    // JdbcTemplate 用于执行 SQL 查询
    private final JdbcTemplate jdbcTemplate;

    // 构造函数，注入 JdbcTemplate
    public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 作业完成后调用此方法
     * @param jobExecution 作业执行信息
     */
    @Override
    public void afterJob(JobExecution jobExecution) {
        // 检查作业执行状态是否为完成
        if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
            // 如果作业完成，打印日志信息
            log.info("!!! JOB FINISHED! Time to verify the results");

            // 使用 JdbcTemplate 执行 SQL 查询，验证数据是否正确插入到数据库中
            jdbcTemplate
                    // 查询 people 表中的 first_name 和 last_name
                    .query("SELECT first_name, last_name FROM people", new DataClassRowMapper<>(Person.class))
                    // 对每一行结果进行处理
                    .forEach(person -> log.info("Found <{{}}> in the database.", person));
        }
    }
}
/*
 
 
 详细中文注释说明：
1. 日志记录 (Logger 和 LoggerFactory)：
使用 SLF4J 的 LoggerFactory 创建了一个 Logger 对象 log，用于记录日志信息。Logger 是用来输出调试、信息、错误等日志的。
2. JobExecutionListener 接口：
该类实现了 Spring Batch 的 JobExecutionListener 接口，表示该类会在作业执行的生命周期中进行监听。JobExecutionListener 主要有两个方法：
beforeJob(JobExecution jobExecution)：作业开始时调用。
afterJob(JobExecution jobExecution)：作业结束时调用，这里我们实现了 afterJob 方法。
3. afterJob 方法：
在作业执行结束时，Spring Batch 会调用 afterJob 方法。在此方法中：
首先判断作业执行的状态是否为 BatchStatus.COMPLETED，即作业是否成功完成。
如果作业完成（BatchStatus.COMPLETED），则打印日志 !!! JOB FINISHED! Time to verify the results，表示作业已成功完成。
然后，使用 JdbcTemplate 执行一个 SQL 查询来验证数据是否已经成功写入数据库。查询的是 people 表中的 first_name 和 last_name 列，表示我们要验证的字段。
DataClassRowMapper 被用来将查询结果映射成 Person 类的实例，每一行数据都会被转换成 Person 对象，并输出到日志中。
4. JdbcTemplate：
JdbcTemplate 是 Spring 提供的用于简化数据库操作的工具类。它封装了 JDBC 的常见操作，简化了数据库连接、执行 SQL 查询、获取结果等操作。在这里它被用于执行查询 SQL，并将结果映射为 Person 对象。
5. DataClassRowMapper：
DataClassRowMapper 是 Spring 提供的一个专门的 RowMapper，用于将查询结果自动映射成 Java 数据类（如 Person 类）。它使用 Java Bean 的属性名与数据库字段名进行自动匹配。
6. Person 类：
Person 类应该是你自己定义的 POJO 类，包含 firstName 和 lastName 属性，并且有相应的 getter 和 setter 方法。在此代码中，Person 类的实例用于接收数据库查询结果。
总结：
JobCompletionNotificationListener 类在作业执行完成后触发，可以用于执行一些作业完成后的后处理操作。这个类的主要作用是：

在作业完成后检查作业状态是否为完成。
如果作业成功完成，使用 JdbcTemplate 查询数据库，验证处理的数据是否已正确插入到数据库表中。
通过日志输出验证结果。
这样，当作业执行完毕后，您可以通过日志查看数据库中是否正确保存了数据。这种方式对于调试和监控批处理作业的结果非常有用。
 
 */