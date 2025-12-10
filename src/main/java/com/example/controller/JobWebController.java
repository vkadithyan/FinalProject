package com.example.controller;

import com.example.model.Company;
import com.example.model.Job;
import com.example.repository.CompanyRepository;
import com.example.repository.JobRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/")
public class JobWebController {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;

    public JobWebController(JobRepository jobRepository, CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
    }

    // MAIN PAGE (only logged in users)
    @GetMapping
    public String listJobs(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        model.addAttribute("jobs", jobRepository.findAll());
        return "job-list";
    }

    @GetMapping("/companies/new")
    @PreAuthorize("hasAnyRole('ADMIN','COMPANY')")
    public String showCompanyForm(Model model) {
        model.addAttribute("company", new Company());
        return "company-form";
    }

    @PostMapping("/companies")
    @PreAuthorize("hasAnyRole('ADMIN','COMPANY')")
    public String saveCompany(@ModelAttribute Company company, RedirectAttributes redirectAttributes) {
        companyRepository.save(company);
        redirectAttributes.addFlashAttribute("message", "Company created successfully!");
        return "redirect:/jobs/new";
    }

    @GetMapping("/jobs/new")
    @PreAuthorize("hasAnyRole('ADMIN','COMPANY')")
    public String showNewJobForm(Model model) {
        model.addAttribute("job", new Job());
        model.addAttribute("companies", companyRepository.findAll());
        return "job-form";
    }

    @PostMapping("/jobs/save")
    @PreAuthorize("hasAnyRole('ADMIN','COMPANY')")
    public String saveJob(@ModelAttribute Job job, RedirectAttributes redirectAttributes) {

        companyRepository.findById(job.getCompany().getId()).ifPresent(job::setCompany);

        jobRepository.save(job);

        redirectAttributes.addFlashAttribute("message", "Job posted successfully!");
        return "redirect:/";
    }
}
