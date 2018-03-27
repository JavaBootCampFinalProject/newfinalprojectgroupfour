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

import javax.mail.internet.InternetAddress;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.security.Principal;

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

    @RequestMapping("/") //Home page
    public String index() {

        return "index";
    }

    @GetMapping("/login") //Login page for all Users
    public String login() {

        return "login";
    }

    @GetMapping("/appuserform") //Creating a new user accountt
    public String getUserForm(Model model) {
        model.addAttribute("appuser", new AppUser());
        return "appuserform";
    }

    @PostMapping("/appuserform") //Takes data from html form and saves it to user (gives the authority of user)
    public String processUser(@Valid @ModelAttribute("appuser") AppUser appUser, BindingResult result) {
        if (result.hasErrors()) {
            return "appuserform";
        }
        appUser.addRole(appRoleRepository.findAppRoleByRoleName("USER"));
        appUserRepository.save(appUser);
        return "redirect:/login";
    }

    @GetMapping("/addadmin") //Creating a new admin account, needs a logged in admin to start
    public String getadminUserForm(Model model) {
        model.addAttribute("appuser", new AppUser());
        return "adminform";
    }

    @PostMapping("/addadmin")// Takes data from html form and saves an Admin User
    public String processadminUserForm(@Valid @ModelAttribute("appuser") AppUser appUser, BindingResult result) {
        if (result.hasErrors()) {
            return "appuserform";
        }
      //  appUserRepository.save(appUser);
        appUser.addRole(appRoleRepository.findAppRoleByRoleName("ADMIN"));
        appUserRepository.save(appUser);
        return "redirect:/";
    }


    @GetMapping("/criteria") //Gets user criteria and passes it to a criteria form
    public String getCriteriaForm(Model model, Authentication auth) {
        AppUser thisUser=appUserRepository.findAppUserByUsername(auth.getName());
        model.addAttribute("appUserCriteriaform", thisUser);
        return "criteriaform";
    }

    @PostMapping("/criteria") //Checks criteria data data for errors or lack of information and pass it to the correct place in response
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

    @RequestMapping("/selectcriteria") //Creates a customised form asking the user to submit at least something into criteria form
    public String selectCriteria(Model m, Authentication auth) {
        AppUser appUser= appUserRepository.findAppUserByUsername(auth.getName());
        m.addAttribute("user", appUser);
        return "selectcriteria";
    }

    @RequestMapping("/recommendedlist") //Checks to see which courses current user should be reccomended
    public String recomendedList(Principal p, Model m) {
        AppUser currentUser=appUserRepository.findAppUserByUsername(p.getName());
        
//        if(!currentUser.isCheckJavaCriteria()&&!currentUser.isCheckTechCriteria())
//            return "redirect:/selectcriteria";

            m.addAttribute("user", currentUser);
            if (currentUser.isCheckJavaCriteria() && currentUser.isCheckTechCriteria())  //If they meet the criteria for both courses add both
                m.addAttribute("recomended", programsRepository.findAll());
            else if (!currentUser.isCheckJavaCriteria()&& currentUser.isCheckTechCriteria())  //Otherwise only recommend the one they met the criteria for
                m.addAttribute("recomended", programsRepository.findByCourseName("Tech Hire"));
            else
                 m.addAttribute("recomended", programsRepository.findByCourseName("Java Boot Camp"));
            return "recommendedlist";

    }

    @RequestMapping("/myqualifications")  //Creates a description of the users qualifications based upon their criteria and passes it to a display page
    public String myQualification(Principal p, Model model) {
        AppUser appUser=appUserRepository.findAppUserByUsername(p.getName());
        model.addAttribute("appUserCriteriaform",appUser);
        model.addAttribute("javaqualification", createjavastring(appUser));
        model.addAttribute("techqualification", createtechstring(appUser));

        return "userqualification";
    }

    @RequestMapping("/apply/{id}") //Takes in a course id and adds the user the set of applied students for that course
    public String confirmationPage(@PathVariable("id") long id, Model model, Authentication auth) {
        Programs prog= programsRepository.findOne(id);
        AppUser appUser=appUserRepository.findAppUserByUsername(auth.getName());
        prog.addUserApplied(appUser);
        prog.setNoofapplications(prog.getNoofapplications()+1); //Incriments a counter so that the total number of applications for a course can be displayed
        programsRepository.save(prog);
        model.addAttribute("course",prog);
        model.addAttribute("user", appUser);
        return "confirmationpage";
    }

    @GetMapping("/approve/{courseid}/{applicantid}")  //Passes in a course and user, so that an admin can approve their application for a course (moves them into and from sets)
    public String approvePage(Model model, @PathVariable("courseid") long courseid,@PathVariable("applicantid") long applicantid){
        Programs course = programsRepository.findOne(courseid);
        AppUser applicant= appUserRepository.findOne(applicantid);

        course.addUserApproved(applicant);
        course.removeUserApplied(applicant);
        programsRepository.save(course);
        model.addAttribute("user",applicant);
        model.addAttribute("course", course);

        try{ //Sends out an e-mail to the student so that they know their application has been accepted
            sendEmailWithoutTemplating(applicant);
        } catch (UnsupportedEncodingException e){
            System.out.println("unsupported Format");
        }
        return "approvalconfirmation";
    }
    @RequestMapping("/enroll/{courseid}")  //The user officially becomes enrolled in the course as they are moved into the final set
    public String enrolledlist(@PathVariable("courseid") long courseid, Model model, Authentication auth) {
        Programs prog= programsRepository.findOne(courseid);
        AppUser appUser=appUserRepository.findAppUserByUsername(auth.getName());
        prog.setNoofstudents(prog.getNoofstudents()+1); //A counter for total number of students enrolled in the course
        prog.addUserInCourse(appUser);
        prog.removeUserApproved(appUser);
        programsRepository.save(prog);
        model.addAttribute("program", prog);
        return "enrollementconfirmation";
    }

    @RequestMapping("/applicantlist")  //List of all applied students with admin buttons for seeing qualifications and accepting their application
    public String applicantLis(  Model model) {
        model.addAttribute("courses", programsRepository.findAll());
        return "applicantlist";
    }

    @RequestMapping("/qualification/{applicantid}") //Gets a user id and uses it to display a user qualifications
    public String qualificationPage(@PathVariable("applicantid") long applicantid, Model model) {
        AppUser appUser= appUserRepository.findOne(applicantid);
        model.addAttribute("appUserCriteriaform", appUser);
        model.addAttribute("javaqualification", createjavastring(appUser));
        model.addAttribute("techqualification", createtechstring(appUser));

        return "userqualification";
    }

    @RequestMapping("/adminapprovedlist") //List of approved student, no button here
    public String adminApprovedList(Model model){
        model.addAttribute("courses", programsRepository.findAll());
        return "adminapprovedlist";
    }

    @RequestMapping("/enrolledlist") //List of all enrolled students
    public String enrolledList(Model model){
        model.addAttribute("courses", programsRepository.findAll());
        return "enrolledlist";
    }

    @RequestMapping("/userapprovedlist") //List of courses approved for current user + button to enroll
    public String userApprovedList(Model model, Authentication auth){
        AppUser currentUser=appUserRepository.findAppUserByUsername(auth.getName());
        Iterable<Programs> approvedfor=programsRepository.findByUserApproved(currentUser);
        model.addAttribute("approvedfor",approvedfor);
        return "userapprovedlist";
    }

    @RequestMapping("/programslist") //List of all programs and their enrolled/applied student counts
    public String programList(Model model){
        model.addAttribute("courses", programsRepository.findAll());
        return "programslist";
    }

    public void sendEmailWithoutTemplating(AppUser appUser)throws UnsupportedEncodingException { //Code for sending an Email to users when they enroll
        final Email email =  DefaultEmail.builder()
                .from(new InternetAddress("mcjavabootcamp@gmail.com", "Program Admin"))
                .to(Lists.newArrayList(new InternetAddress(appUser.getUserEmail(), appUser.getFullName())))
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

    public StringBuilder createjavastring(AppUser appUser) {
        StringBuilder javaString = new StringBuilder();
        if (appUser.isCriteriaUnderstandOOP())
            javaString.append("Basic understanding of object oriented language <br/>");
        if (appUser.isCriteriaExperienceOOP())
            javaString.append("Previous experience with an object-oriented language <br/>");
        if (appUser.isCriteriaCompSciMajor())
            javaString.append("Major in Computer Science/Information Systems <br/>");
        if (appUser.isCriteriaRecentGraduate())
            javaString.append("Graduated within the last 6 years <br/>");
        if (appUser.isCriteriaCurrentEarnings())
            javaString.append("Currently earning $42,000 or less <br/>");
        if (appUser.isCriteriaWorkInUs())
            javaString.append("Be able to work in the United States <br/>");
        return javaString;
    }

    public StringBuilder createtechstring(AppUser appUser) {
        StringBuilder techString= new StringBuilder();
        if(appUser.isCriteriaEnglish())
            techString.append("English Language Learner <br/>");
        if(appUser.isCriteriaUnemployed())
            techString.append("Unemployed with barriers to employment <br/>");
        if(appUser.isCriteriaUnderEmployed())
            techString.append("Underemployed with barriers to better employment <br/>");
        if(appUser.isCriteriaComputerComfortable())
            techString.append("Be comfortable using computers for everyday purposes <br/>");
        if(appUser.isCriteriaItInterest())
            techString.append("Have a strong interest in an IT career <br/>");
        if(appUser.isCriteriaDiploma())
            techString.append("Have a high school diploma or GED <br/>");
        if(appUser.isCriteriaWorkInUs())
            techString.append("Be able to work in the United States <br/>");
        return techString;
    }
}