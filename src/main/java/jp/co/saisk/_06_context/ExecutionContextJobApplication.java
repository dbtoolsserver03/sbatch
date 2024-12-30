package jp.co.saisk._06_context;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.support.JdbcTransactionManager;

@SpringBootApplication
public class ExecutionContextJobApplication {

	@Autowired
	public JobRepository jobRepository;
	
	@Autowired
	public JdbcTransactionManager transactionManager;

	public static void main(String[] args) {
		// 使用 SpringApplication.run 启动 Spring Boot 应用
		// SpringApplication.exit() 用于退出应用程序并返回一个状态码
		// SpringApplication.exit() 返回应用程序的退出状态，以便传递给操作系统或调用者
		System.exit(SpringApplication.exit(SpringApplication.run(ExecutionContextJobApplication.class, args)));
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
		return new JobBuilder("Hello", jobRepository) // 创建一个 Job 构建器
				.start(step1())
				.next(step2())
				.incrementer(new RunIdIncrementer())
				.build(); // 构建作业
	}

	@Bean
	public Step step1() {
		return new StepBuilder("step2", jobRepository).tasklet(tasklet1(), transactionManager).build();

	}

	@Bean
	public Step step2() {
		return new StepBuilder("step2", jobRepository).tasklet(tasklet2(), transactionManager).build();
	}

	//构造一个step对象执行的任务（逻辑对象）
	@Bean
	public Tasklet tasklet1() {
		return new Tasklet() {
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				//步骤
				//可以获取共享数据，但是不允许修改
				//Map<String, Object> stepExecutionContext = chunkContext.getStepContext().getStepExecutionContext();
				//通过执行上下文对象获取跟设置参数
				ExecutionContext stepEC = chunkContext.getStepContext().getStepExecution().getExecutionContext();
				stepEC.put("key-step1-step", "value-step1-step");

				System.out.println("----------------1---------------");
				//作业
				ExecutionContext jobEC = chunkContext.getStepContext().getStepExecution().getJobExecution()
						.getExecutionContext();
				jobEC.put("key-step1-job", "value-step1-job");

				return RepeatStatus.FINISHED; //执行完了
			}
		};
	}

	@Bean
	public Tasklet tasklet2() {
		return new Tasklet() {
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				//步骤
				ExecutionContext stepEC = chunkContext.getStepContext().getStepExecution().getExecutionContext();
				System.err.println(stepEC.get("key-step1-step"));
				System.out.println("----------------2---------------");
				//作业
				ExecutionContext jobEC = chunkContext.getStepContext().getStepExecution().getJobExecution()
						.getExecutionContext();
				System.err.println(jobEC.get("key-step1-job"));
				return RepeatStatus.FINISHED; //执行完了
			}
		};
	}
}
