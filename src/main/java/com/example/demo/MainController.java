package com.example.demo;

import com.google.common.collect.Lists;
import it.ozimov.springboot.mail.model.Email;
import it.ozimov.springboot.mail.model.defaultimpl.DefaultEmail;
import it.ozimov.springboot.mail.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;

@Controller
public class MainController {

    @Autowired
    AppRoleRepository appRoleRepository;

    @Autowired
    AppUserRepository appUserRepository;

    @Autowired
    ProgramsRepository programsRepository;
    @Autowired
    public EmailService emailService;

    @RequestMapping("/")
    public String index() {

        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/appuserform")
    public String getUserForm(Model model) {
        model.addAttribute("appuser", new AppUser());
        return "appuserform";
    }

    @PostMapping("/appuserform")
    public String processUser(@Valid @ModelAttribute("appuser") AppUser appUser, BindingResult result) {
        if (result.hasErrors()) {
            return "appuserform";
        }
        appUser.addRole(appRoleRepository.findAppRoleByRoleName("USER"));
        appUserRepository.save(appUser);
        return "redirect:/login";
    }

    @GetMapping("/addadmin")
    public String getadminUserForm(Model model) {
        model.addAttribute("appuser", new AppUser());
        return "adminform";
    }

    @PostMapping("/addadmin")
    public String processadminUserForm(@Valid @ModelAttribute("appuser") AppUser appUser, BindingResult result) {
        if (result.hasErrors()) {
            return "appuserform";
        }
        appUserRepository.save(appUser);
        appUser.addRole(appRoleRepository.findAppRoleByRoleName("ADMIN"));
        appUserRepository.save(appUser);
        return "redirect:/";
    }


    @GetMapping("/criteria")
    public String getCriteriaForm(Model model, Authentication auth) {
        AppUser thisUser=appUserRepository.findAppUserByUsername(auth.getName());
        model.addAttribute("appUserCriteriaform", thisUser);
        return "criteriaform";
    }

    @PostMapping("/criteria")
    public String processCriteriaForm(@Valid @ModelAttribute("appUser") AppUser appUser, Model model, BindingResult result
    ) {
        if(result.hasErrors())
            return "criteriaform";

        if(!appUser.isCheckTechCriteria()&&!appUser.isCheckJavaCriteria())
            return "redirect:/selectcriteria";
        saveappusercriteria(appUser);
       appUserRepository.save(appUser);

        return "redirect:/recommendedlist";
    }

    @RequestMapping("/selectcriteria")
    public String selectCriteria() {
        return "selectcriteria";
    }

    @RequestMapping("/recommendedlist")
    public String recomendedList(Principal p, Model m) {
        System.out.println("hi");
        AppUser currentUser=appUserRepository.findAppUserByUsername(p.getName());


        if(!currentUser.isCheckJavaCriteria()&&!currentUser.isCheckTechCriteria())
            return "redirect:/selectcriteria";
        else {
            if (currentUser.isCheckJavaCriteria() && currentUser.isCheckTechCriteria())
                m.addAttribute("recomended", programsRepository.findAll());
            else if (!currentUser.isCheckJavaCriteria()&& currentUser.isCheckTechCriteria())
                m.addAttribute("recomended", programsRepository.findByCourseName("Tech Hire"));
            else
                 m.addAttribute("recomended", programsRepository.findByCourseName("Java Boot Camp"));
            return "recommendedlist";
        }
    }


    @RequestMapping("/myqualifications")
    public String myQualification(Principal p, Model model) {
        AppUser appUser=appUserRepository.findAppUserByUsername(p.getName());

        StringBuilder javaString=new StringBuilder();
        StringBuilder techString= new StringBuilder();
        if(appUser.isCriteriaEnglish())
            techString.append("English Language Learner \n");
        if(appUser.isCriteriaUnemployed())
            techString.append("Unemployed with barriers to employment \n");
        if(appUser.isCriteriaUnderEmployed())
            techString.append("Underemployed with barriers to better employment \n");
        if(appUser.isCriteriaComputerComfortable())
            techString.append("Be comfortable using computers for everyday purposes \n");
        if(appUser.isCriteriaItInterest())
            techString.append("Have a strong interest in an IT career \n");
        if(appUser.isCriteriaDiploma())
            techString.append("Have a high school diploma or GED \n");
        if(appUser.isCriteriaWorkInUs())
            techString.append("Be able to work in the United States \n").append("\n");

        if(appUser.isCriteriaUnderstandOOP())
            javaString.append("Basic understanding of object oriented language ").append("\n");
        if(appUser.isCriteriaExperienceOOP())
            javaString.append("Previous experience with an object-oriented language ").append("\n");
        if(appUser.isCriteriaCompSciMajor())
            javaString.append("Major in Computer Science/Information Systems ").append("\n");
        if(appUser.isCriteriaRecentGraduate())
            javaString.append("Graduated within the last 6 years ").append("\n");
        if(appUser.isCriteriaCurrentEarnings())
            javaString.append("Currently earning $42,000 or less ").append("\n");
        if(appUser.isCriteriaWorkInUs())
            javaString.append("Be able to work in the United States ").append("\n");
        model.addAttribute("appUserCriteriaform",appUser);
        model.addAttribute("newLineChar", '\n');
        model.addAttribute("javaqualification", javaString);
        model.addAttribute("techqualification", techString);

        return "userqualification";

    }


    @RequestMapping("/apply/{id}")
    public String confirmationPage(@PathVariable("id") long id, Model model, Authentication auth) {
        Programs prog= programsRepository.findOne(id);
        AppUser appUser=appUserRepository.findAppUserByUsername(auth.getName());
        prog.addUserApplied(appUser);
        prog.setNoofapplications(prog.getNoofapplications()+1);
        programsRepository.save(prog);
        model.addAttribute("course",prog);
        model.addAttribute("user", appUser);
        return "confirmationpage";
    }
    @RequestMapping("/enroll/{courseid}")
    public String enrolledlist(@PathVariable("courseid") long courseid, Model model, Authentication auth) {
        Programs prog= programsRepository.findOne(courseid);
        AppUser appUser=appUserRepository.findAppUserByUsername(auth.getName());
        prog.addUserInCourse(appUser);
        prog.setNoofstudents(prog.getNoofstudents()+1);
        prog.removeUserApproved(appUser);
        programsRepository.save(prog);
        model.addAttribute("program", prog);
        return "enrollementconfirmation";
    }

    @RequestMapping("/applicantlist")
    public String applicantLis(  Model model) {
        model.addAttribute("courses", programsRepository.findAll());
        return "newapplicantlist";
    }

    @GetMapping("/approve/{courseid}/{applicantid}")
    public String approvePage(Model model, final RedirectAttributes redirectAttributes, @PathVariable("courseid") long courseid,@PathVariable("applicantid") long applicantid){
        Programs course = programsRepository.findOne(courseid);
        AppUser applicant= appUserRepository.findOne(applicantid);

        course.addUserApproved(applicant);
        course.removeUserApplied(applicant);
        programsRepository.save(course);
        model.addAttribute("user",applicant);
        model.addAttribute("course", course);

        try{
            sendEmailWithoutTemplating(applicant.getUserEmail());
        } catch (UnsupportedEncodingException e){
            System.out.println("unsupported Format");
        }
        return "approvalconfirmation";
    }


    @RequestMapping("/qualification/{applicantid}")
    public String qualificationPage(@PathVariable("applicantid") long applicantid, Model model, Principal p) {
        AppUser appUser= appUserRepository.findOne(applicantid);
        StringBuilder javaString=new StringBuilder();
        StringBuilder techString= new StringBuilder();
        if(appUser.isCriteriaEnglish())
            techString.append("English Language Learner \n");
        if(appUser.isCriteriaUnemployed())
            techString.append("Unemployed with barriers to employment \n");
        if(appUser.isCriteriaUnderEmployed())
            techString.append("Underemployed with barriers to better employment \n");
        if(appUser.isCriteriaComputerComfortable())
            techString.append("Be comfortable using computers for everyday purposes \n");
        if(appUser.isCriteriaItInterest())
            techString.append("Have a strong interest in an IT career \n");
        if(appUser.isCriteriaDiploma())
            techString.append("Have a high school diploma or GED \n");
        if(appUser.isCriteriaWorkInUs())
            techString.append("Be able to work in the United States \n").append("\n");

        if(appUser.isCriteriaUnderstandOOP())
            javaString.append("Basic understanding of object oriented language ").append("\n");
        if(appUser.isCriteriaExperienceOOP())
            javaString.append("Previous experience with an object-oriented language ").append("\n");
        if(appUser.isCriteriaCompSciMajor())
            javaString.append("Major in Computer Science/Information Systems ").append("\n");
        if(appUser.isCriteriaRecentGraduate())
            javaString.append("Graduated within the last 6 years ").append("\n");
        if(appUser.isCriteriaCurrentEarnings())
            javaString.append("Currently earning $42,000 or less ").append("\n");
        if(appUser.isCriteriaWorkInUs())
            javaString.append("Be able to work in the United States ").append("\n");
        model.addAttribute("appUserCriteriaform", appUser);
        model.addAttribute("newLineChar", '\n');
        model.addAttribute("javaqualification", javaString);
        model.addAttribute("techqualification", techString);

        return "userqualification";
    }

    @RequestMapping("/enrolledlist")
    public String enrolledList(Model model){
        Programs java= programsRepository.findByCourseName("Java Boot Camp");

        model.addAttribute("courses", programsRepository.findAll());
        return "enrolledlist";
    }

    @RequestMapping("/adminapprovedlist") //List of approved student, no button here
    public String adminApprovedList(Model model){
        model.addAttribute("courses", programsRepository.findAll());
        return "adminapprovedlist";
    }
    @RequestMapping("/userapprovedlist") //List of courses approved for current user + button to enroll
    public String userApprovedList(Model model, Authentication auth){
        AppUser currentUser=appUserRepository.findAppUserByUsername(auth.getName());
        Iterable<Programs> approvedfor=programsRepository.findByUserApproved(currentUser);
        model.addAttribute("approvedfor",approvedfor);
        return "userapprovedlist";
    }

    @RequestMapping("/programslist")
    public String programList(Model model){
        model.addAttribute("courses", programsRepository.findAll());
        return "programslist";
    }

    public void sendEmailWithoutTemplating(String useremail)throws UnsupportedEncodingException {
        final Email email =  DefaultEmail.builder()
                .from(new InternetAddress("mcjavabootcamp@gmail.com", "ba bute "))
                .to(Lists.newArrayList(new InternetAddress(useremail, "Pomponius Attĭcus")))
                .subject("Admission")
                .body("Hello, you have been approved for the course you applied to... " +
                        "please confirm back as soon as possible to enroll in the course!!!")
                .encoding("UTF-8").build();

        emailService.send(email);
    }

    public void saveappusercriteria(AppUser appUser){

        appUser.techCriteria[0]=appUser.isCriteriaEnglish();
        appUser.techCriteria[1]=appUser.isCriteriaUnemployed();
        appUser.techCriteria[2]=appUser.isCriteriaUnderEmployed();
        appUser.techCriteria[3]=appUser.isCriteriaComputerComfortable();
        appUser.techCriteria[4]=appUser.isCriteriaItInterest();
        appUser.techCriteria[5]=appUser.isCriteriaDiploma();
        appUser.techCriteria[6]=appUser.isCriteriaWorkInUs();

        appUser.javaCriteria[0]=appUser.isCriteriaUnderstandOOP();
        appUser.javaCriteria[1]=appUser.isCriteriaExperienceOOP();
        appUser.javaCriteria[2]=appUser.isCriteriaCompSciMajor();
        appUser.javaCriteria[3]=appUser.isCriteriaRecentGraduate();
        appUser.javaCriteria[4]=appUser.isCriteriaCurrentEarnings();
        appUser.javaCriteria[5]=appUser.isCriteriaWorkInUs();


    }
    @RequestMapping("/edit/{id}")
    public String editlostItem(@PathVariable("id") long id, Model model){

         AppUser appUser = appUserRepository.findOne(id);
        model.addAttribute("appUserCriteriaform", appUser);

        return "criteriaform";
    }
}