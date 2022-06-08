/**
 * Genaro Pelipas (c) 2020
 */
package com.pelipas.batch.tasklet;

import java.io.File;
import java.io.FilenameFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.env.Environment;

/**
 * [desc]
 * 
 * @author gpelipas
 *
 */
public class FileArchiveTasklet implements Tasklet {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private Environment env;

	/**
	 * @param env
	 */
	public FileArchiveTasklet(Environment env) {
		this.env = env;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		File outputPath = new File(env.getProperty("gmp.output.path"));
		File archivePath = new File(env.getProperty("gmp.archive.path"));

		if (!outputPath.isDirectory()) {
			logger.warn("Output directory is not valid directory");

			return null;
		}

		if (!archivePath.isDirectory()) {
			logger.warn("Archive directory is not valid directory");

			return null;
		}
		
		File[] files = outputPath.listFiles(fileFilter());
		for (File f : files) {
			boolean moved = f.renameTo(new File(archivePath.getAbsoluteFile(), f.getName()));

			if (moved) {
				logger.info("File has been archived - " + f.getAbsolutePath());
			} else {
				logger.warn("File could not be archived - " + f.getAbsolutePath());
			}
		}

		return RepeatStatus.FINISHED;
	}

	private FilenameFilter fileFilter() {
		return new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(runId() + ".txt");
			}
		};
	}

	private String runId() {
		return System.getProperty("appRunId", "default");
	}

}
