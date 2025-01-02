/*
 
方案1：Step 步骤监听器方式
第一次执行：tasklet1 中readCount 默认执行50次，不满足条件， 
stopStepListener() afterStep 返回STOPPED, 
job进行条件控制走**.on("STOPPED").stopAndRestart(step1())**  分支，
停止并允许重启--下次重启，从step1步骤开始执行

第二次执行， 修改readCount = 100， 再次启动作业，task1遍历100次，
满足条件， stopStepListener() afterStep 正常返回，
job条件控制走**.from(step1()).on("*").to(step2()).end()**分支，正常结束。

注意：step1() 方法中**.allowStartIfComplete(true)**  代码必须添加，
因为第一次执行step1步骤，虽然不满足条件，但是它仍属于正常结束(正常执行完tasklet1的流程)，状态码：COMPLETED，
 第二次重启，默认情况下正常结束的step1步骤是不允许再执行的，
所以必须设置：**.allowStartIfComplete(true)**  允许step1即使完成也可以重启。

*/

package jp.co.saisk._18_job_restart_forbid;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.JobBuilder;
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
public class JobForBidRestartJob {

	@Autowired
	public JobRepository jobRepository;

	@Autowired
	public JdbcTransactionManager transactionManager;

	public static void main(String[] args) {
		// 使用 SpringApplication.run 启动 Spring Boot 应用
		// SpringApplication.exit() 用于退出应用程序并返回一个状态码
		// SpringApplication.exit() 返回应用程序的退出状态，以便传递给操作系统或调用者
		System.exit(SpringApplication.exit(SpringApplication.run(JobForBidRestartJob.class, args)));
	}

    @Bean
    public Tasklet tasklet1(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.err.println("-------------tasklet1-------------");

                chunkContext.getStepContext().getStepExecution().setTerminateOnly(); //停止步骤
                return RepeatStatus.FINISHED;

            }
        };
    }

    @Bean
    public Tasklet tasklet2(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.err.println("-------------tasklet2-------------");
                return RepeatStatus.FINISHED;
            }
        };
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
		return new JobBuilder("job-forbid-restart-job1", jobRepository) // 创建一个 Job 构建器
				.preventRestart()  //禁止重启
				.start(step1())
				.next(step2())
				.build();
	}

	@Bean
	public Step step1() {
		return new StepBuilder("step1", jobRepository)
				.tasklet(tasklet1(), transactionManager)
					.allowStartIfComplete(true) //运行step从新执行
				.build();
	}

	@Bean
	public Step step2() {
		return new StepBuilder("step2", jobRepository)
				.tasklet(tasklet2(), transactionManager)
				.build();
	}
}
