package jp.co.saisk._01_hello;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = HelloBatchApplication.class)
class HelloBatchApplicationTests {
	@Autowired
	private JobLauncher jobLauncher;

	
	@Autowired
	private Job job;
	
	@Test
	public void testStart() throws Exception {
		jobLauncher.run(job, new JobParameters());
	}
}
