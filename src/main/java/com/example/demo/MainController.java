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
//        if(auth.name()==null)
//            return "redirect:/login";

        AppUser thisUser=appUserRepository.findAppUserByUsername(auth.getName());
        System.out.println(auth.getName());
        System.out.println(thisUser.getRoles());
      //thisUser.addRole(appRoleRepository.findAppRoleByRoleName("USER"));
//        appUserRepository.save(thisUser);
        model.addAttribute("appUserCriteriaform", thisUser);
        return "criteriaform";
    }

    @PostMapping("/criteria")
    public String processCriteriaForm(@Valid @ModelAttribute("appUser") AppUser appUser, Model model, BindingResult result
    ) {

        if(result.hasErrors())
            return "criteriaform";

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
        boolean techqual=false;
        boolean javaqual=false;
        for (int i = 0; i < appUser.techCriteria.length; i++) {
            techqual=appUser.techCriteria[i];
            if(techqual)
                break;
        }
        for (int i = 0; i < appUser.javaCriteria.length; i++) {
            javaqual=appUser.javaCriteria[i];
            if(javaqual)
                break;
        }

        if(!techqual&&!javaqual)
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

        if(javaCriteriaCounter==0&&techCriteriaCounter==0)
            return "redirect:/criteria";
        else {
            if (javaCriteriaCounter > 0 && techCriteriaCounter > 0)
                m.addAttribute("recomended", programsRepository.findAll());
                else if (javaCriteriaCounter > 0 && techCriteriaCounter == 0)
                m.addAttribute("recomended", programsRepository.findByCourseName("Java Boot Camp"));
                else if (techCriteriaCounter > 0 && javaCriteriaCounter == 0)
                m.addAttribute("recomended", programsRepository.findByCourseName("Tech Hire"));


//        int criteriano=2;
////        String errormessage="Please Select your skills!";
//        if(javaCriteriaCounter<criteriano&&techCriteriaCounter<criteriano)
//            return "redirect:/criteria";
//        else {
//            if (javaCriteriaCounter >= criteriano && techCriteriaCounter >= criteriano)
//                m.addAttribute("recomended", programsRepository.findAll());
//
//            else if (javaCriteriaCounter >= criteriano && techCriteriaCounter <criteriano)
//                m.addAttribute("recomended", programsRepository.findByCourseName("Java Boot Camp"));
//
//            else if (techCriteriaCounter >= criteriano && javaCriteriaCounter < criteriano)
//                m.addAttribute("recomended", programsRepository.findByCourseName("Tech Hire"));

            return "recommendedlist";
        }
    }

    @RequestMapping("/apply/{id}")
    public String confirmationPage(@PathVariable("id") long id, Model model, Principal p) {
        Programs prog= programsRepository.findOne(id);
        AppUser appUser=appUserRepository.findAppUserByUsername(p.getName());
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
        programsRepository.save(prog);
        model.addAttribute("program", prog);
        return "enrollementconfirmation";
    }

    @RequestMapping("/applicantlist")
    public String applicantList(  Model model) {
       model.addAttribute("courses",programsRepository.findAll() ) ;
       return "applicantlist";
    }

    @RequestMapping("/approve/{applicantid}/{courseid}")
    public String approvePage(@PathVariable("applicantid") long applicantid, @PathVariable("courseid") long courseid, Model model, Principal p) {
        AppUser applicant= appUserRepository.findOne(applicantid);
        Programs course = programsRepository.findOne(courseid);
        course.addUserApproved(applicant);
        programsRepository.save(course);
        model.addAttribute("program", course);
        System.out.println(applicant.getUsername());
        System.out.println(applicant.getUserEmail());

        try{
            sendEmailWithoutTemplating(applicant.getUserEmail());
        } catch (UnsupportedEncodingException e){
            System.out.println("unsupported Format");
        }
        return "approvalconfirmation";
    }

//    @RequestMapping("/approve/{applicantid}/{programid}")
//    public String approvePage(@PathVariable("applicantid") long applicantid, Model model, Principal p) {
//        AppUser applicant= appUserRepository.findOne(applicantid);
//        Programs course = programsRepository.findByUserApplied(applicant);
//        course.addUserApproved(applicant);
//        programsRepository.save(course);
//        model.addAttribute("program", course);
//        System.out.println(applicant.getUsername());
//        System.out.println(applicant.getUserEmail());
//
//        try{
//            sendEmailWithoutTemplating(applicant.getUserEmail());
//        } catch (UnsupportedEncodingException e){
//            System.out.println("unsupported Format");
//        }
//        return "approvalconfirmation";
//    }


    @RequestMapping("/qualification/{applicantid}")
    public String qualificationPage(@PathVariable("applicantid") long applicantid, Model model, Principal p) {
        AppUser appUser= appUserRepository.findOne(applicantid);
        StringBuilder javaString=new StringBuilder();
        StringBuilder techString= new StringBuilder();
        if(appUser.techCriteria[0])
            techString.append("English Language Learner \n");
            techString.append("Unemployed with barriers to employment \n");
        if(appUser.techCriteria[2])
            techString.append("Underemployed with barriers to better employment \n");
        if(appUser.techCriteria[3])
            techString.append("Be comfortable using computers for everyday purposes \n");
        if(appUser.techCriteria[4])
            techString.append("Have a strong interest in an IT career \n");
        if(appUser.techCriteria[5])
            techString.append("Have a high school diploma or GED \n");
        if(appUser.techCriteria[6])
            techString.append("Be able to work in the United States \n").append("\n");

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

        model.addAttribute("newLineChar", '\n');
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

    @RequestMapping("/adminapprovedlist")
    public String adminApprovedList(Model model){
        Programs java= programsRepository.findByCourseName("Java Boot Camp");

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
        model.addAttribute("techstudent",techstudent);

        return "adminapprovedlist";
    }
    @RequestMapping("/userapprovedlist")
    public String userApprovedList(Model model, Authentication auth){
        AppUser currentUser=appUserRepository.findAppUserByUsername(auth.getName());
        Iterable<Programs> approvedfor=programsRepository.findByUserApproved(currentUser);
        //  System.out.println(approvedfor.getUserApproved());
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
                .subject("Laelius de amicitia")
                .body("You are approved for the course you applied... " +
                        "reply soon if u want to enroll!!!")
                .encoding("UTF-8").build();

        emailService.send(email);
    }

}