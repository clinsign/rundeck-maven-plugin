package com.jstaormina.rundeckplugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.rundeck.api.RunJob;
import org.rundeck.api.RunJobBuilder;
import org.rundeck.api.RundeckClient;
import org.rundeck.api.domain.RundeckExecution;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Mojo(name = "run-job")
@Execute(phase = LifecyclePhase.VERIFY)
public class RunJobMojo extends AbstractRundeckMojo {

    private static final long POLLING_INTERVAL_SECONDS = 5L;
    private static final long POLLING_INTERVAL_MILLIS = 5000L;
    private static final TimeUnit INTERVAL_UNIT = TimeUnit.SECONDS;

    @Parameter
    private String jobUuid;

    @Parameter
    private Properties nodeFilters;

    @Parameter
    private Properties options;

    public String getJobUuid() {
        return jobUuid;
    }

    public void setJobUuid(String jobUuid) {
        this.jobUuid = jobUuid;
    }

    public Properties getNodeFilters() {
        return nodeFilters;
    }

    public void setNodeFilters(Properties nodeFilters) {
        this.nodeFilters = nodeFilters;
    }

    public Properties getOptions() {
        return options;
    }

    public void setOptions(Properties options) {
        this.options = options;
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        this.getLog().info("Rundeck Instance:\n\turl: " + this.getUrl() + "\n\ttoken: " + this.getToken());
        RundeckClient rundeckClient = this.getRundeckClient();

        this.getLog().info("Job : " + this.jobUuid + "\n\tnodes: " + this.nodeFilters + "\n\toptions: " + this.options);
        RunJob job =
            RunJobBuilder.builder().setJobId(this.jobUuid).setNodeFilters(this.nodeFilters).setOptions(this.options)
                .build();
        RundeckExecution execution = rundeckClient.runJob(job, POLLING_INTERVAL_SECONDS, INTERVAL_UNIT);

        StringBuilder sb = new StringBuilder().append("Job: ").append(job.getJobId()).append("\n\tStatus: ")
            .append(execution.getStatus()).append("\n\t").append(execution.getUrl());

        String executionStatus = sb.toString();
        if (!execution.getStatus().equals(RundeckExecution.ExecutionStatus.SUCCEEDED)) {
            this.getLog().error(executionStatus.toString());
            throw new MojoFailureException(executionStatus);
        } else {
            this.getLog().info(executionStatus);
        }
    }
}
