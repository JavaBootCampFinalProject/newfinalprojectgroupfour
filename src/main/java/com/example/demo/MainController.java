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
        appUser.isCheckTechCriteria();
        appUser.isCheckJavaCriteria();
        if(!appUser.isCheckJavaCriteria()&&!appUser.isCheckJavaCriteria())
            return "redirect:/criteria";
        appUserRepository.save(appUser);

        return "redirect:/recommendedlist";
    }

    @RequestMapping("/recommendedlist")
    public String recomendedList(Principal p, Model m) {
        AppUser currentUser=appUserRepository.findAppUserByUsername(p.getName());

        int javaCriteriaCounter=0;
        int techCriteriaCounter=0;
        for (int i = 0; i < currentUser.javaCriteria.length; i++) {
            if(currentUser.javaCriteria[i])
                javaCriteriaCounter++;
        }

        for (int i = 0; i < currentUser.techCriteria.length; i++) {
            if(currentUser.techCriteria[i])
                techCriteriaCounter++;
        }
        int criteriano=3;
        if(javaCriteriaCounter<criteriano&&techCriteriaCounter<criteriano)
            return "redirect:/criteria";
        else {
            if (javaCriteriaCounter >= criteriano && techCriteriaCounter >= criteriano)
                m.addAttribute("recomended", programsRepository.findAll());

            else if (javaCriteriaCounter >= criteriano && techCriteriaCounter <criteriano)
                m.addAttribute("recomended", programsRepository.findByCourseName("Java Boot Camp"));

            else if (techCriteriaCounter >= criteriano && javaCriteriaCounter < criteriano)
                m.addAttribute("recomended", programsRepository.findByCourseName("Tech Hire"));

            return "recommendedlist";
        }
    }

    @RequestMapping("/apply/{id}")
    public String confirmationPage(@PathVariable("id") long id, Model model, Authentication auth) {
        Programs prog= programsRepository.findOne(id);
        AppUser appUser=appUserRepository.findAppUserByUsername(auth.getName());
        prog.addUserApplied(appUser);
        programsRepository.save(prog);
        model.addAttribute("program", prog);
        return "confirmationpage";
    }
    @RequestMapping("/enroll/{courseid}")
    public String enrolledlist(@PathVariable("courseid") long courseid, Model model, Authentication auth) {
        Programs prog= programsRepository.findOne(courseid);
        AppUser appUser=appUserRepository.findAppUserByUsername(auth.getName());
        prog.addUserInCourse(appUser);
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
        model.addAttribute("program", course);

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
        if(appUser.techCriteria[0])
            techString.append("English Language Learner ").append("\n");
        if(appUser.techCriteria[1])
            techString.append("Unemployed with barriers to employment ").append("\n");
        if(appUser.techCriteria[2])
            techString.append("Underemployed with barriers to better employment ").append("\n");
        if(appUser.techCriteria[3])
            techString.append("Be comfortable using computers for everyday purposes \n").append("\n");
        if(appUser.techCriteria[4])
            techString.append("Have a strong interest in an IT career ").append("\n");
        if(appUser.techCriteria[5])
            techString.append("Have a high school diploma or GED ").append("\n");
        if(appUser.techCriteria[6])
            techString.append("Be able to work in the United States ").append("\n");

        if(appUser.javaCriteria[0])
            javaString.append("Basic understanding of object oriented language ").append("\n");
        if(appUser.javaCriteria[1])
            javaString.append("Previous experience with an object-oriented language ").append("\n");
        if(appUser.javaCriteria[2])
            javaString.append("Major in Computer Science/Information Systems ").append("\n");
        if(appUser.javaCriteria[3])
            javaString.append("Graduated within the last 6 years ").append("\n");
        if(appUser.javaCriteria[4])
            javaString.append("Currently earning $42,000 or less ").append("\n");
        if(appUser.javaCriteria[5])
            javaString.append("Be able to work in the United States ").append("\n");

        System.out.println(javaString);
        System.out.println(techString);

        model.addAttribute("javaqualification", javaString);
        model.addAttribute("techqualification", techString);

        return "userqualification";
    }

    @RequestMapping("/enrolledlist")
    public String enrolledList(Model model){
        Programs java= programsRepository.findByCourseName("Java Boot Camp");

        ArrayList<AppUser> javastudent= new ArrayList<>();
        for(AppUser user:java.getUserInCourse())
            javastudent.add(user);

        Programs tech= programsRepository.findByCourseName("Tech Hire");

        ArrayList<AppUser> techstudent= new ArrayList<>();
        for(AppUser user:tech.getUserInCourse())
            techstudent.add(user);

        model.addAttribute("java",java);
        model.addAttribute("javastudent",javastudent);

        model.addAttribute("tech",tech);
        model.addAttribute("techstudent",techstudent);

        return "enrolledlist";
    }

    @RequestMapping("/adminapprovedlist") //List of approved student, no button here
    public String adminApprovedList(Model model){
   /*     Programs java= programsRepository.findByCourseName("Java Boot Camp");

        ArrayList<AppUser> javastudent= new ArrayList<>();
        for(AppUser user:java.getUserApproved())
            javastudent.add(user);

        Programs tech= programsRepository.findByCourseName("Tech Hire");

        ArrayList<AppUser> techstudent= new ArrayList<>();
        for(AppUser user:tech.getUserApproved())
            techstudent.add(user);


        model.addAttribute("java",java);
        model.addAttribute("javastudent",javastudent);

        model.addAttribute("tech",tech);
        model.addAttribute("techstudent",techstudent);*/
        model.addAttribute("courses", programsRepository.findAll());

        return "adminapprovedlist";
    }
    @RequestMapping("/userapprovedlist") //List of courses approved for current user + button to enroll
    public String userApprovedList(Model model, Authentication auth){
        Programs approvedfor=programsRepository.findByUserApproved(appUserRepository.findAppUserByUsername(auth.getName()));
        model.addAttribute("approvedfor",approvedfor);
        return "userapprovedlist";
    }

    @RequestMapping("/programslist")
    public String programList(Model model){

        Programs java= programsRepository.findByCourseName("Java Boot Camp");
        Programs tech= programsRepository.findByCourseName("Tech Hire");

        int javaapplicant=appUserRepository.countAllByApplied(java);
        int techapplicant=appUserRepository.countAllByApplied(tech);

        int javaenrolled=appUserRepository.countAllByInCourse(java);
        int techenrolled=appUserRepository.countAllByInCourse(tech);

        model.addAttribute("java",java);
        model.addAttribute("tech",tech);
        model.addAttribute("noOfJavaApplicants",javaapplicant );
        model.addAttribute("noOfTechApplicants",techapplicant );
        model.addAttribute("noOfJavaEnrolled",javaenrolled );
        model.addAttribute("noOfTechEnrolled",techenrolled );

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
}
