package jp.co.saisk._13_flow_step;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
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
public class FlowStepJobApplication {

	@Autowired
	public JobRepository jobRepository;

	@Autowired
	public JdbcTransactionManager transactionManager;

	public static void main(String[] args) {
		// 使用 SpringApplication.run 启动 Spring Boot 应用
		// SpringApplication.exit() 用于退出应用程序并返回一个状态码
		// SpringApplication.exit() 返回应用程序的退出状态，以便传递给操作系统或调用者
		System.exit(SpringApplication.exit(SpringApplication.run(FlowStepJobApplication.class, args)));
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
		return new JobBuilder("flow-step-job", jobRepository) // 创建一个 Job 构建器
				//满足xxx条件执行后续逻辑--当前意思：满足firstStep执行返回状态为失败状态：FAILED执行failStep
				.start(stepA())
				.next(stepB())
				.next(stepC())
				.incrementer(new RunIdIncrementer())
				.build(); // 构建作业
	}

	@Bean
	public Step stepA() {
		return new StepBuilder("stepA", jobRepository).tasklet(taskletA(), transactionManager).build();
	}

	@Bean
	public Step stepB1() {
		return new StepBuilder("stepB1", jobRepository).tasklet(taskletB1(), transactionManager).build();

	}

	@Bean
	public Step stepB2() {
		return new StepBuilder("stepB2", jobRepository).tasklet(taskletB2(), transactionManager).build();

	}

	@Bean
	public Step stepB3() {
		return new StepBuilder("stepB3", jobRepository).tasklet(taskletB3(), transactionManager).build();

	}

	//构造一个流式步骤
	@Bean
	public Flow flowB() {
		return new FlowBuilder<Flow>("flowB")
				.start(stepB1())
				.next(stepB2())
				.next(stepB3())
				.build();
	}

	//job 没有现有的flowStep步骤操作方法， 必须使用step进行封装之后再执行
	@Bean
	public Step stepB() {
		//tasklet 执行step逻辑， 类似 Thread()--->可以执行runable接口
		return new StepBuilder("stepB", jobRepository).flow(flowB()).build();

	}

	@Bean
	public Step stepC() {
		return new StepBuilder("stepC", jobRepository).tasklet(taskletC(), transactionManager).build();
	}

	//构造一个step对象执行的任务（逻辑对象）
	@Bean
	public Tasklet taskletA() {
		return new Tasklet() {
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("----------------taskletA---------------");
				return RepeatStatus.FINISHED;
			}
		};
	}

	@Bean
	public Tasklet taskletB1() {
		return new Tasklet() {
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("------------stepB----taskletB1---------------");
				return RepeatStatus.FINISHED;
			}
		};
	}

	@Bean
	public Tasklet taskletB2() {
		return new Tasklet() {
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("------------stepB----taskletB2---------------");
				return RepeatStatus.FINISHED;
			}
		};
	}

	@Bean
	public Tasklet taskletB3() {
		return new Tasklet() {
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("------------stepB----taskletB3---------------");
				return RepeatStatus.FINISHED;
			}
		};
	}

	@Bean
	public Tasklet taskletC() {
		return new Tasklet() {
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("----------------taskletC---------------");
				return RepeatStatus.FINISHED;
			}
		};
	}

}
