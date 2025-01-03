package jp.co.saisk._12_step_status;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.support.JdbcTransactionManager;

@SpringBootApplication
public class StatusStepJobApplication {

	@Autowired
	public JobRepository jobRepository;

	@Autowired
	public JdbcTransactionManager transactionManager;

	public static void main(String[] args) {
		// 使用 SpringApplication.run 启动 Spring Boot 应用
		// SpringApplication.exit() 用于退出应用程序并返回一个状态码
		// SpringApplication.exit() 返回应用程序的退出状态，以便传递给操作系统或调用者
		System.exit(SpringApplication.exit(SpringApplication.run(StatusStepJobApplication.class, args)));
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
		return new JobBuilder("status-step-job", jobRepository) // 创建一个 Job 构建器
	               //满足xxx条件执行后续逻辑--当前意思：满足firstStep执行返回状态为失败状态：FAILED执行failStep
                .start(firstStep())
                //表示将当前本应该是失败结束的步骤直接转成正常结束--COMPLETED
                //.on("FAILED").end()
                //表示将当前本应该是失败结束的步骤直接转成失败结束：FAILED
                //.on("FAILED").fail()
                //表示将当前本应该是失败结束的步骤直接转成停止结束：STOPPED   里面参数表示后续要重启时， 从successStep位置开始
                .on("FAILED").stopAndRestart(successStep())
                //.from(firstStep()).on("FAILED").to(successStep())
                .from(firstStep()).on("*").to(successStep())
                .end()
				.incrementer(new RunIdIncrementer())
				.build(); // 构建作业
	}

	@Bean
	public Step firstStep() {
		return new StepBuilder("firstStep", jobRepository).tasklet(firstStepTasklet(), transactionManager).build();

	}

	@Bean
	public Step successStep() {
		return new StepBuilder("successStep", jobRepository).tasklet(successStepTasklet(), transactionManager).build();
	}

	@Bean
	public Step failStep() {
		return new StepBuilder("failStep", jobRepository).tasklet(failStepTasklet(), transactionManager).build();
	}

	//构造一个step对象执行的任务（逻辑对象）
	@Bean
	public Tasklet firstStepTasklet() {
		return new Tasklet() {
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

				System.out.println("----------------firstStep---------------");

				throw new RuntimeException("假装失败了");
				//return RepeatStatus.FINISHED; //执行完了
			}
		};
	}

	@Bean
	public Tasklet successStepTasklet() {
		return new Tasklet() {
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("----------------successStep---------------");
				return RepeatStatus.FINISHED; //执行完了
			}
		};
	}

	@Bean
	public Tasklet failStepTasklet() {
		return new Tasklet() {
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("----------------failStep---------------");
				return RepeatStatus.FINISHED; //执行完了
			}
		};
	}

}
