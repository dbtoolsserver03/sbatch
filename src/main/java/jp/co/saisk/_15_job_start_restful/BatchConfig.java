package jp.co.saisk._15_job_start_restful;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.support.JdbcTransactionManager;

@Configuration
public class BatchConfig {
	
	@Autowired
	public JobRepository jobRepository;

	@Autowired
	public JdbcTransactionManager transactionManager;

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
		return new JobBuilder("hello-job", jobRepository) // 创建一个 Job 构建器
				.start(step()) // 定义作业的第一个步骤
				.incrementer(new RunIdIncrementer())
				.build(); // 构建作业
	}

	// 取参数方式chunkContext
	@Bean
	public Step step() {
		return new StepBuilder("step", jobRepository).tasklet((contribution, chunkContext) -> {
			System.out.println("---->"+chunkContext.getStepContext());
            System.out.println("hello spring  batch！--->" + chunkContext.getStepContext().getJobParameters().get("name"));
			return RepeatStatus.FINISHED;
		}, transactionManager).build();
	}

}
