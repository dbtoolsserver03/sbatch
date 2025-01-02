package jp.co.saisk._24_itemreader_db_cursor;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.support.JdbcTransactionManager;


@SpringBootApplication
public class CursorDBReaderJob {

	@Autowired
	public JobRepository jobRepository;

	@Autowired
	public JdbcTransactionManager transactionManager;

    @Autowired
    private DataSource dataSource;
    
	public static void main(String[] args) {
		// 使用 SpringApplication.run 启动 Spring Boot 应用
		// SpringApplication.exit() 用于退出应用程序并返回一个状态码
		// SpringApplication.exit() 返回应用程序的退出状态，以便传递给操作系统或调用者
		System.exit(SpringApplication.exit(SpringApplication.run(CursorDBReaderJob.class, args)));
	}

    //job--->step---tasklet
    //job--->step-chunk----reader---writer

    @Bean
    public ItemWriter<User> itemWriter(){
        return new ItemWriter<User>() {

			@Override
			public void write(Chunk<? extends User> items) throws Exception {
				  items.forEach(System.err::println);
			}
        };
    }

    //将列数据与对象属性一一映射
    @Bean
    public UserRowMapper userRowMapper(){
        return new UserRowMapper();
    }

    //使用jdbc游标方式读数据
    @Bean
    public JdbcCursorItemReader<User> itemReader(){

        return new JdbcCursorItemReaderBuilder<User>()
                .name("userItemReader")
                //连接数据库， spring容器自己实现
                .dataSource(dataSource)
                //执行sql查询数据， 将返回的数据以游标形式一条一条读
                .sql("select * from user where age < ?")
                //拼接参数
                .preparedStatementSetter(new ArgumentPreparedStatementSetter(new Object[]{16}))
                //数据库读出数据跟用户对象属性一一映射
                .rowMapper(userRowMapper())
                .build();
    }


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
	public Job job() throws Exception {
		return new JobBuilder("cursor-db-reader-job0001", jobRepository) // 创建一个 Job 构建器
				.start(step())
				.build();
	}

	@Bean
	public Step step() {
		return new StepBuilder("step", jobRepository) // 创建一个步骤构建器，步骤名为 step1
				.<User, User> chunk(3, transactionManager) // 设置处理的每个批次的大小为 3，每次处理 3 条记录
				.reader(itemReader()) // 设置读取器
				.writer(itemWriter()) // 设置写入器
				.build(); // 构建步骤
	}

}
