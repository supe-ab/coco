package com.assignment.product_service.controller;

import com.assignment.product_service.service.ItemService;
import com.assignment.product_service.vo.ItemVO;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {
    private final ItemService itemService;
    private final JobLauncher jobLauncher;
    private final Job importCsvToDbJob;
    private final Job exportItemsJob;
    private final Job taskletBasedJob;
    private final Job taskletBasedExportJob;

    public ItemController(
            ItemService itemService,
            JobLauncher jobLauncher,
            @Qualifier("importCsvToDbJob") Job importCsvToDbJob,
            @Qualifier("exportItemsJob") Job exportItemsJob,
            @Qualifier("taskletBasedJob") Job taskletBasedJob,
            @Qualifier("taskletBasedExportJob") Job taskletBasedExportJob) {
        this.itemService = itemService;
        this.jobLauncher = jobLauncher;
        this.importCsvToDbJob = importCsvToDbJob;
        this.exportItemsJob = exportItemsJob;
        this.taskletBasedJob = taskletBasedJob;
        this.taskletBasedExportJob = taskletBasedExportJob;
    }

    @PostMapping
    public ResponseEntity<ItemVO> createItem(@RequestBody ItemVO itemVO) throws InterruptedException {
        return ResponseEntity.ok(itemService.createItem(itemVO));
    }

    @GetMapping
    public ResponseEntity<List<ItemVO>> getAllItems() {
        return ResponseEntity.ok(itemService.getAllItems());
    }

    @GetMapping("/health-check")
    public ResponseEntity<String> healthCheck() {
        try {
            itemService.healthCheck();
            return ResponseEntity.ok("Health Check passed. Downstream system is healthy");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Health Check failed: " + ex.getMessage());
        }
    }

    @PostMapping("/batch-job")
    public ResponseEntity<String> runBatchJob(@RequestParam(name = "jobType") String jobType) {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            Job job;
            String jobDescription;
            
            switch (jobType.toLowerCase()) {
                case "import":
                    job = importCsvToDbJob;
                    jobDescription = "CSV import";
                    break;
                case "export":
                    job = exportItemsJob;
                    jobDescription = "items export";
                    break;
                case "tasklet":
                    job = taskletBasedJob;
                    jobDescription = "tasklet-based import";
                    break;
                case "tasklet-export":
                    job = taskletBasedExportJob;
                    jobDescription = "tasklet-based export";
                    break;
                default:
                    return ResponseEntity.badRequest()
                            .body("Invalid job type. Use 'import', 'export', 'tasklet', or 'tasklet-export'.");
            }
            jobLauncher.run(job, jobParameters);
            return ResponseEntity.ok(jobDescription + " batch job has been initiated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to run batch job: " + e.getMessage());
        }
    }
}