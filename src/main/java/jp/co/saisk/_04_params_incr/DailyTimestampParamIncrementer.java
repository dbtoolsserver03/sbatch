package jp.co.saisk._04_params_incr;

import java.util.Date;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;


// 以时间为增量
public class DailyTimestampParamIncrementer implements JobParametersIncrementer {

	@Override
	public JobParameters getNext(JobParameters parameters) {

		return new JobParametersBuilder(parameters).addLong("daily", new Date().getTime()).toJobParameters();
	}

}
