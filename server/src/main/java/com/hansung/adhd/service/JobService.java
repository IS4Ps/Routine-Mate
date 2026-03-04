package com.hansung.adhd.service;

import com.hansung.adhd.domain.Jobs;
import com.hansung.adhd.dto.JobResponseDto;
import com.hansung.adhd.repository.JobsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobsRepository jobsRepository;

    @Transactional(readOnly = true)
    public List<JobResponseDto> getAllJobs() {
        return jobsRepository.findAll().stream()
                .map(job -> new JobResponseDto(
                        job.getId(),
                        job.getJobCode(),
                        job.getJobName(),
                        job.getDescription(),
                        job.getBaseStrength(),
                        job.getBaseIntelligence(),
                        job.getBaseCreativity()
                ))
                .collect(Collectors.toList());
    }
}