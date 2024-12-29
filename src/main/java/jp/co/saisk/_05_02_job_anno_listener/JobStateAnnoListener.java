package jp.co.saisk._05_02_job_anno_listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;

public class JobStateAnnoListener  {
	
	@BeforeJob
	public void beforeJob(JobExecution jobExecution) {
		System.err.println("作业执行前的状态 anno：" + jobExecution.getStatus());
	}
	@AfterJob
	public void afterJob(JobExecution jobExecution) {
		System.err.println("作业执行后的状态 anno：" + jobExecution.getStatus());
	}
}