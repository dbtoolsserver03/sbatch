package jp.co.saisk._03_03_params_validator.copy;

import java.util.Arrays;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.support.JdbcTransactionManager;

import jp.co.saisk._03_params_validator.validator.NameParamValidator;

@SpringBootApplication
public class ParamsCheckApplication03 {

	public static void main(String[] args) {
		// 使用 SpringApplication.run 启动 Spring Boot 应用
		// SpringApplication.exit() 用于退出应用程序并返回一个状态码
		// SpringApplication.exit() 返回应用程序的退出状态，以便传递给操作系统或调用者
		System.exit(SpringApplication.exit(SpringApplication.run(ParamsCheckApplication03.class, args)));
	}

	@Bean
	public NameParamValidator nameParamValidator() {
		return new NameParamValidator();
	}

	@Bean
	public DefaultJobParametersValidator defaultJobParametersValidator() {
		DefaultJobParametersValidator validator = new DefaultJobParametersValidator();
		validator.setRequiredKeys(new String[] { "name", "sex" });
		validator.setOptionalKeys(new String[] { "age" });
		return validator;
	}

	@Bean
	public CompositeJobParametersValidator compositeJobParametersValidator() throws Exception {
		CompositeJobParametersValidator validator = new CompositeJobParametersValidator();

		validator.setValidators(Arrays.asList(defaultJobParametersValidator(), nameParamValidator()));

		validator.afterPropertiesSet();

		return validator;
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
	public Job job(JobRepository jobRepository, Step step1) throws Exception {
		return new JobBuilder("params-check", jobRepository) // 创建一个 Job 构建器
				.start(step1) // 定义作业的第一个步骤
				.incrementer(new RunIdIncrementer())
				.validator(compositeJobParametersValidator())
				.build(); // 构建作业
	}

	// 取参数方式chunkContext
	@Bean
	public Step step1(JobRepository jobRepository, JdbcTransactionManager transactionManager) {
		return new StepBuilder("step1", jobRepository).tasklet(tasklet1(null), transactionManager).build();
	}

	@Bean
	@StepScope // 懒加载
	public Tasklet tasklet1(@Value("#{jobParameters['name']}") String name) {
		return (contribution, chunkContext) -> {
			System.out.println("name-----> " + name);
			return RepeatStatus.FINISHED;
		};
	}

}
