import java.io.*;
import java.util.*;

public class Project3 {

    // Job class representing a single job's attributes
    static class Job {
        String name;         // Job name
        int startTime;       // Time when the job can start being processed
        int duration;        // Total duration the job needs to be processed
        int remainingTime;   // Remaining time for the job to be processed

        // Constructor for the Job class
        public Job(String name, int startTime, int duration) {
            this.name = name;
            this.startTime = startTime;
            this.duration = duration;
            this.remainingTime = duration; // Initially, remaining time is the same as the total duration
        }
    }

    // Main method of the Project3 class
    public static void main(String[] args) throws IOException {
        // Check if the file name is provided
        if (args.length < 1) {
            System.out.println("Usage: java Project3 jobs.txt");
            return;
        }

        // Load jobs from the specified file
        Job[] jobs = loadJobs(args[0]);

        // Schedule jobs using the First-Come-First-Served algorithm
        System.out.println("FCFS");
        scheduleFCFS(jobs);

        // Schedule jobs using the Round Robin algorithm with a quantum of 1
        System.out.println("Round-Robin");
        scheduleRR(jobs);
    }

    // Method to load jobs from the given file name
    private static Job[] loadJobs(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        ArrayList<Job> jobList = new ArrayList<>(); // List to store the jobs

        // Read the file line by line
        String line;
        while ((line = reader.readLine()) != null) {
            // Split the line by tab to get job attributes
            String[] parts = line.split("\t");
            // Check if the line has the correct format
            if (parts.length != 3) {
                System.out.println("Invalid job format: " + line);
                continue;  // Skip malformed lines
            }
            String name = parts[0];
            int startTime;
            int duration;
            // Try to parse the start time and duration, skipping invalid formats
            try {
                startTime = Integer.parseInt(parts[1]);
                duration = Integer.parseInt(parts[2]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format in line: " + line);
                continue;  // Skip lines with invalid number formats
            }
            // Add the job to the list
            jobList.add(new Job(name, startTime, duration));
        }

        reader.close();  // Close the BufferedReader

        // Convert ArrayList to an array and return it
        Job[] jobsArray = new Job[jobList.size()];
        jobsArray = jobList.toArray(jobsArray);
        return jobsArray;
    }

    // Method to schedule jobs using the FCFS algorithm
    private static void scheduleFCFS(Job[] jobs) {
        int currentTime = 0; // Tracks the current time

        // Iterate through each job
        for (Job job : jobs) {
            // Wait for the job's start time if it hasn't arrived yet
            int startTime = Math.max(currentTime, job.startTime);

            // Print the job's name and initial spaces
            System.out.print(job.name + "  ");
            for (int i = 0; i < startTime; i++) {
                System.out.print(" ");
            }
            // Print 'X' for each time unit the job is being processed
            for (int i = 0; i < job.duration; i++) {
                System.out.print("X");
            }
            System.out.println(); // Move to the next line for the next job

            // Update current time to when this job will finish
            currentTime = startTime + job.duration;
        }
    }

    // Method to schedule jobs using the Round Robin algorithm with quantum 1
    private static void scheduleRR(Job[] jobs) {
        int currentTime = 0; // Tracks the current time
        boolean jobInProgress; // Flag to track if any job is currently being processed
        ArrayList<Job> jobQueue = new ArrayList<>(); // Queue to hold jobs waiting to be processed
        StringBuilder[] schedules = new StringBuilder[jobs.length]; // Store the schedules for each job

        // Initialize StringBuilder for each job's schedule
        for (int i = 0; i < schedules.length; i++) {
            schedules[i] = new StringBuilder();
        }

        // Populate the queue with jobs that can start initially
        for (Job job : jobs) {
            if (job.startTime <= currentTime) {
                jobQueue.add(job);
            }
        }

        // Keep processing jobs until all are completed
        while (!jobQueue.isEmpty()) {
            jobInProgress = false;
            Job currentJob = jobQueue.remove(0); // Remove the first job from the queue

            // If current time is before the job's start time, just go to the start time
            if (currentTime < currentJob.startTime) {
                currentTime = currentJob.startTime;
            }

            // If the job has remaining time, process it for one quantum
            if (currentJob.remainingTime > 0) {
                jobInProgress = true;
                currentJob.remainingTime--;
                currentTime++; // Advance the time
                // Add 'X' to indicate processing for the current time unit
                schedules[currentJob.name.charAt(0) - 'A'].append("X");
            }

            // Check if any jobs have arrived to be added to the queue
            for (Job job : jobs) {
                if (job.startTime == currentTime && !jobQueue.contains(job)) {
                    jobQueue.add(job);
                }
            }

            // If the job is not finished, add it back to the queue
            if (currentJob.remainingTime > 0) {
                jobQueue.add(currentJob);
            }

            // If no job was processed in the current time, move to the next time unit
            if (!jobInProgress) {
                currentTime++;
            }

            // Ensure each job's schedule is as long as the current time
            for (StringBuilder schedule : schedules) {
                while (schedule.length() < currentTime) {
                    schedule.append(" ");
                }
            }
        }

        // Print the schedules for each job
        for (int i = 0; i < schedules.length; i++) {
            System.out.println(jobs[i].name + " " + schedules[i]);
        }
    }
}
