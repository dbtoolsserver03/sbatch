package jp.co.saisk._22_itemreader_flat_mapper;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.support.JdbcTransactionManager;

@SpringBootApplication
public class MapperFlatReaderJob {

	@Autowired
	public JobRepository jobRepository;

	@Autowired
	public JdbcTransactionManager transactionManager;

	public static void main(String[] args) {
		// 使用 SpringApplication.run 启动 Spring Boot 应用
		// SpringApplication.exit() 用于退出应用程序并返回一个状态码
		// SpringApplication.exit() 返回应用程序的退出状态，以便传递给操作系统或调用者
		System.exit(SpringApplication.exit(SpringApplication.run(MapperFlatReaderJob.class, args)));
	}

	//job--->step---tasklet
	//job--->step-chunk----reader---writer

	@Bean
	public ItemWriter<User> itemWriter() {
		return new ItemWriter<User>() {

			@Override
			public void write(Chunk<? extends User> chunk) throws Exception {
				chunk.forEach(System.err::println);
			}
		};
	}

	@Bean
	public UserFieldMapper userFieldMapper() {
		return new UserFieldMapper();
	}

	// 1    dafei    18
	//id    name     age
	@Bean
	public FlatFileItemReader<User> itemReader() {
		return new FlatFileItemReaderBuilder<User>()
				.name("userItemReader")
				//获取文件
				.resource(new ClassPathResource("users2.txt"))
				//解析数据--指定解析器使用# 分割--默认是 ，号
				.delimited().delimiter("#")
				//按照 # 截取数据之后， 数据怎么命名
				.names("id", "name", "age", "province", "city", "area")
				//封装数据--将读取的数据封装到对象：User对象
				//.targetType(User.class)  自定封装
				.fieldSetMapper(userFieldMapper())
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
		return new JobBuilder("mapper-flat-reader-job", jobRepository) // 创建一个 Job 构建器

				.start(step())
				.build();
	}

	@Bean
	public Step step() {
		return new StepBuilder("step1", jobRepository) // 创建一个步骤构建器，步骤名为 step1
				.<User, User> chunk(3, transactionManager) // 设置处理的每个批次的大小为 3，每次处理 3 条记录
				.reader(itemReader()) // 设置读取器
				.writer(itemWriter()) // 设置写入器
				.build(); // 构建步骤
	}

}
