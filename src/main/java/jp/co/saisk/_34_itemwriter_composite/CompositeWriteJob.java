package jp.co.saisk._34_itemwriter_composite;

import java.util.Arrays;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.support.builder.CompositeItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;
import org.springframework.jdbc.support.JdbcTransactionManager;



@SpringBootApplication
public class CompositeWriteJob {

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
		System.exit(SpringApplication.exit(SpringApplication.run(CompositeWriteJob.class, args)));
	}

	//job--->step---tasklet
	//job--->step-chunk----reader---writer
	@Bean
	public ItemWriter<User> consoleItemWriter() {
		return new ItemWriter<User>() {

			@Override
			public void write(Chunk<? extends User> items) throws Exception {
				items.forEach(System.err::println);
			}
		};
	}
	

    //输出到outUser.txt文件
    @Bean
    public FlatFileItemWriter<User> flatItemWriter(){
        return new FlatFileItemWriterBuilder<User>()
                .name("userFlatItemWriter")
                //输出位置
                .resource(new PathResource("C:/work/file/outUser.txt"))
                .formatted()  //要进行格式输出
                .format("id: %s,姓名：%s,年龄：%s")  //输出数据格式
                .names("id", "name", "age")
                .build();
    }

    //json对象的调度器
    @Bean
    public JacksonJsonObjectMarshaller<User> jsonObjectMarshaller(){
        return new JacksonJsonObjectMarshaller<>();
    }
    //输出到outUser.json文件
    @Bean
    public JsonFileItemWriter<User> jsonItemWriter(){
        return new JsonFileItemWriterBuilder<User>()
                .name("userJsonItemWriter")
                //输出位置
                .resource(new PathResource("C:/work/file/outUser.json"))
                //json对象调度器--将user对象缓存json格式，输出文档中
                .jsonObjectMarshaller(jsonObjectMarshaller())
                .build();
    }

    @Bean
    public UserPreStatementSetter userPreStatementSetter(){
        return new UserPreStatementSetter();
    }
    //数据库输出
    @Bean
    public JdbcBatchItemWriter<User> dbItemWriter(){
        return new JdbcBatchItemWriterBuilder<User>()
                .dataSource(dataSource)
                .sql("insert into user(id, name, age) values(?,?,?)")
                //设置sql中占位符参数
                .itemPreparedStatementSetter(userPreStatementSetter())
                .build();
    }


    @Bean
    public FlatFileItemReader<User> itemReader(){
        return new FlatFileItemReaderBuilder<User>()
                .name("userDBItemReader")
                //获取文件
                .resource(new ClassPathResource("user.txt"))
                //解析数据--指定解析器使用# 分割--默认是 ，号
                .delimited().delimiter("#")
                //按照 # 截取数据之后， 数据怎么命名
                .names("id", "name", "age")
                //封装数据--将读取的数据封装到对象：User对象
                .targetType(User.class)
                .build();
    }


    @Bean
    public CompositeItemWriter<User> compositeItemWriter(){
        return new CompositeItemWriterBuilder<User>()
                .delegates(Arrays.asList(consoleItemWriter(),flatItemWriter(), jsonItemWriter(), dbItemWriter()))
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
		return new JobBuilder("composite-writer-job01", jobRepository) // 创建一个 Job 构建器
				.start(step())
				.build();
	}

	@Bean
	public Step step() throws Exception {
		return new StepBuilder("step", jobRepository) // 创建一个步骤构建器，步骤名为 step1
				.<User, User> chunk(3, transactionManager) // 设置处理的每个批次的大小为 3，每次处理 3 条记录
				.reader(itemReader())
				.writer(compositeItemWriter())
				.build(); // 构建步骤
	}

}
