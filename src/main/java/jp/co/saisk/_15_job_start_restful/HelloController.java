package jp.co.saisk._15_job_start_restful;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;

// WEB開発の場合
//@RestController
public class HelloController {
    @Autowired
    private JobLauncher launcher;

    @Autowired
    private Job job;

    @Autowired
    private JobExplorer jobExplorer;  //job相关对象的-展示对象

 // WEB開発の場合
//    @GetMapping("/job/start")
    public ExitStatus startJob(String name) throws Exception {

        //run.id 自增前提：先获取到之前jobparameter中run.id 才能进行自增
        // 也就是说， 当前请求想要让run.id 自增，需要获取之前jobparameter才能加-

    	// http://localhost:8080/job/start
        //启动job作业
        JobParameters parameters = new JobParametersBuilder(jobExplorer)
                .getNextJobParameters(job)
                .addString("name", name)
                .toJobParameters();

        JobExecution jobExet = launcher.run(job, parameters);
        return jobExet.getExitStatus();
    }
}