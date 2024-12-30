package jp.co.saisk._11_step_condition_decider;
import java.util.Random;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

//指定step返回状态值决策器
public class MyStatusDecider implements JobExecutionDecider {

    //执行过程返回指定状态值--代码设计： 有1/3机会返回A, B, C
    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        long ret = new Random().nextInt(3);

        System.out.println("------------MyStatusDecider-----------");
        if(ret == 0){
            return new FlowExecutionStatus("A");
        }else if(ret == 1){
            return new FlowExecutionStatus("B");
        }else{
            return new FlowExecutionStatus("C");
        }
    }
}