package de.abas.custom.spareParts.catalogue.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public interface CommandRunner {

	public default String runCommand(File directory, String... commandAndArgs) throws IOException {
		try {
			ProcessBuilder pb = new ProcessBuilder();
			if (directory != null) {
				pb = pb.directory(directory);
			}
			pb.command(commandAndArgs);
			final Process process = pb.start();
			if (process != null) {
				final int exitCode = process.waitFor();
				if (exitCode == 0) {
					return getResult(process);
				} else {
					throw new IOException("Command execution of " + Arrays.toString(commandAndArgs)
							+ " failed with exit code " + exitCode);
				}
			}
		} catch (final InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Command execution interrupted");
		}
		return null;
	}

	public default String runCommand(String... commandAndArgs) throws IOException {
		return runCommand(null, commandAndArgs);
	}

	default String getResult(Process process) throws IOException {
		final BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;
		String output = "";
		while ((line = br.readLine()) != null) {
			output = output + line + "\n";
		}
		return output;
	}

}
