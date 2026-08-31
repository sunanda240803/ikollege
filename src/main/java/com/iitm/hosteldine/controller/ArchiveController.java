package com.iitm.hosteldine.controller;

import com.iitm.hosteldine.form.common.ArchiveProgress;
import com.iitm.hosteldine.service.ArchiveService;
import com.iitm.hosteldine.service.InMemoryLogService;
import com.iitm.hosteldine.util.HTMLPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("${url.archive}")
public class ArchiveController {

    private final ArchiveService archiveService;
    private final InMemoryLogService logService;

    @Value("${url.student}")
    private String studentUrl;

    @Value("${url.candidate}")
    private String candidateUrl;

    @Value("${url.archive.settled.students}")
    private String settledStudentsUrl;

    @GetMapping
    public String archivePhotos() {
        return HTMLPage.ARCHIVE_PHOTOS;
    }

    @PostMapping("${url.student}")
    public @ResponseBody String archiveStudentPhotos() {
        String tag = studentUrl.substring(1);
        logService.addLog(tag, "Student Photo archive triggered.", false);
        archiveService.archiveStudentPhotos(tag);
        return "success";
    }

    @PostMapping("${url.candidate}")
    public @ResponseBody String archiveCandidatePhotos() {
        String tag = candidateUrl.substring(1);
        logService.addLog(tag, "Candidate Photo archive triggered.", false);
        archiveService.archiveCandidatePhotos(tag);
        return "success";
    }

    @PostMapping("${url.archive.settled.students}")
    public @ResponseBody String archiveSettledStudents() {
        String tag = settledStudentsUrl.substring(1);
        logService.addLog(tag, "Settled Student details archive triggered.", false);
        archiveService.archiveSettledStudents(tag);
        return "success";
    }

    @GetMapping("/getProgress/{type}")
    public @ResponseBody ArchiveProgress getProgress(@PathVariable String type) {
        return archiveService.getProgress(type);
    }
}
