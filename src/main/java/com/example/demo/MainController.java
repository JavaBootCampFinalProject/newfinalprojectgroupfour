package com.example.demo;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Controller
public class MainController {
    @Autowired
    AppRoleRepository appRoleRepository;

    @Autowired
    AppUserRepository appUserRepository;

    @Autowired
    ProgramsRepository programsRepository;


    @RequestMapping("/")
    public String index() {

        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/appuserform")
    public String getUserForm() {
        return "appuserform";
    }

    @PostMapping("/appuserform")
    public String processAppUserForm() {
        return "login";
    }

    @GetMapping("/criteria")
    public String getCriteriaForm(Model model, Principal auth) {
//        if(auth.name()==null)
//            return "redirect:/login";

        AppUser thisUser=appUserRepository.findAppUserByUsername(auth.getName());
        System.out.println(auth.getName());
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


        appUserRepository.save(appUser);

        return "redirect:/recommendedlist";
    }

    @RequestMapping("/recommendedlist")
    public String recomendedList(Principal p, Model m) {
        AppUser currentUser=appUserRepository.findAppUserByUsername(p.getName());
//        if(thisUser.isCriteriaEnglish() && thisUser.isCriteriaUnemployed()&&thisUser.isCriteriaCompSciMajor()&&
//                thisUser.isCriteriaComputerComfortable()&&thisUser.isCriteriaCurrentEarnings()&&thisUser.isCriteriaWorkInUs()
//                &&thisUser.isCriteriaDiploma()&&thisUser.isCriteriaExperienceOOP()&&thisUser.isCriteriaItInterest()&&
//
//         thisUser.isCriteriaRecentGraduate()&&thisUser.isCriteriaUnderstandOOP()&&thisUser.isCriteriaUnderEmployed())
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
            int criteriano=2;
        if(javaCriteriaCounter>criteriano&&techCriteriaCounter>criteriano)
            m.addAttribute("recomended",programsRepository.findAll() );

        else if(javaCriteriaCounter>criteriano&&techCriteriaCounter<=criteriano)
            m.addAttribute("recomended",programsRepository.findByCourseName("Java Boot Camp") );

        else if(techCriteriaCounter>criteriano&&javaCriteriaCounter<=criteriano)
            m.addAttribute("recomended",programsRepository.findByCourseName("Tech Hire") );


        return "recommendedlist";
    }

    @RequestMapping("/apply/{id}")
    public String confirmationPage(@PathVariable("id") long id, Model model, Principal p) {
        Programs prog= programsRepository.findOne(id);
        AppUser appUser=appUserRepository.findAppUserByUsername(p.getName());
        prog.addUserApplied(appUser);
        programsRepository.save(prog);
//        String msg=appUser.getUsername().toString()+ " has Applied for "+ prog.getCourseName();
        model.addAttribute("program", prog);
        return "confirmationpage";
    }


    @RequestMapping("/applicantlist")
    public String applicantLis(  Model model) {
        Programs java= programsRepository.findByCourseName("Java Boot Camp");

        ArrayList<AppUser> javaapplicant= new ArrayList<>();
        for(AppUser user:java.getUserApplied())
               javaapplicant.add(user);

        Programs tech= programsRepository.findByCourseName("Tech Hire");

        ArrayList<AppUser> techapplicant= new ArrayList<>();
        for(AppUser user:tech.getUserApplied())
            techapplicant.add(user);

////        for(Programs eachprog: prog){
////            for(AppUser user:eachprog.getUserApplied())
////                applicant.add(user);
////
////        }
//        model.addAttribute("program", prog);
//        model.addAttribute("applicant", applicant);
        model.addAttribute("java",java);
        model.addAttribute("javaapplicant",javaapplicant);

        model.addAttribute("tech",tech);
        model.addAttribute("techapplicant",techapplicant);

        return "applicantlist";
    }

    @RequestMapping("/approve/{applicantid}")
    public String approvePage(@PathVariable("applicantid") long applicantid, Model model, Principal p) {
        AppUser applicant= appUserRepository.findOne(applicantid);
        Programs course = programsRepository.findByUserApplied(applicant);
        course.addUserApproved(applicant);
        programsRepository.save(course);
        model.addAttribute("program", course);
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



    @RequestMapping("/approvedlist")
    public String approvedList(Model model){
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

        return "approvedlist";
    }


    @RequestMapping("/programslist")
    public String programList(Model model){

        Programs java= programsRepository.findByCourseName("Java Boot Camp");
        Programs tech= programsRepository.findByCourseName("Tech Hire");

        int javaapplicant=appUserRepository.countAllByApplied(java);
        int techapplicant=appUserRepository.countAllByApplied(tech);

        int javaapproved=appUserRepository.countAllByApproved(java);
        int techapproved=appUserRepository.countAllByApproved(tech);

        model.addAttribute("java",java);
        model.addAttribute("tech",tech);
        model.addAttribute("noOfJavaApplicants",javaapplicant );
        model.addAttribute("noOfTechApplicants",techapplicant );
        model.addAttribute("noOfJavaApproved",javaapproved );
        model.addAttribute("noOfTechApproved",techapproved );
        System.out.println(javaapplicant+ " " +techapproved );
        return "programslist";
    }










    //Everything under this line is for copy and paste/refrenece
    /*
    @RequestMapping("/")
    public String frontPage(Model model) {
        RestTemplate restTemplate = new RestTemplate();
        News news = restTemplate.getForObject("https://newsapi.org/v2/top-headlines?country=us&apiKey=895d727e71cf4321a9bbcf319375aa47", News.class);
        model.addAttribute("news", news);
        return "frontPage";
    }

    @RequestMapping("/topicnews")
    public String topicPage(Model model, Authentication authentication) {
        AppUser appUser = appUserRepository.findAppUserByUsername(authentication.getName());
        RestTemplate restTemplate = new RestTemplate();
        News newsStore = new News();
        for (Interests interests : appUser.getInterests()) { //Loops through the users to check for "user" Found Item, and upon finding it sets the item as a found item.
            News news = restTemplate.getForObject("https://newsapi.org/v2/top-headlines?q=" + interests.getInterestName() + "&sortBy=publishedAt&apiKey=895d727e71cf4321a9bbcf319375aa47", News.class);
            for (Articles articles : news.getArticles()) {
                newsStore.addArticles(articles);
            }
        }
        model.addAttribute("news", newsStore);
        return "frontPage";
    }

    @RequestMapping("/categorynews")
    public String catagoryPage(Model model, Authentication authentication) {
        AppUser appUser = appUserRepository.findAppUserByUsername(authentication.getName());
        RestTemplate restTemplate = new RestTemplate();
        News newsStore = new News();
        for (AppCatagory appCatagory : appUser.getAppCatagory()) {
            News news = restTemplate.getForObject("https://newsapi.org/v2/top-headlines?country=us&category=" + appCatagory.getCatagoryName() + "&apiKey=895d727e71cf4321a9bbcf319375aa47", News.class);
            for (Articles articles : news.getArticles()) {
                newsStore.addArticles(articles);
            }
        }
        model.addAttribute("news", newsStore);
        return "frontPage";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/userinfopage") //The admin page contains all items and several commands that only the admin has
    public String userInfoPage(Model model, Authentication authentication) {
        model.addAttribute("appUser", appUserRepository.findAppUserByUsername(authentication.getName()));
        return "userInfoPage";
    }

    @RequestMapping("/removeTopic/{id}")
    //This swaps the status of an item from lost to found via the admin page and then returns there (Find a way to save position on the page?)
    public String removeTopic(@PathVariable("id") long id, Model model, Authentication auth) {
        AppUser appUser = appUserRepository.findAppUserByUsername(auth.getName());
        appUser.removeIntrest(interestsRepository.findOne(id));
        appUserRepository.save(appUser);
        model.addAttribute("appUser", appUser);
        return "userInfoPage";
    }

    @RequestMapping("/removeCategory/{id}")
    //This swaps the status of an item from lost to found via the admin page and then returns there (Find a way to save position on the page?)
    public String removeCategory(@PathVariable("id") long id, Model model, Authentication auth) {
        AppUser appUser = appUserRepository.findAppUserByUsername(auth.getName());
        appUser.removeappCatagory(categoriesRepository.findOne(id));
        appUserRepository.save(appUser);
        model.addAttribute("appUser", appUser);
        return "userInfoPage";
    }

    @RequestMapping("/appuserform")  //For registration and creation of a new user
    public String userRegistration(Model model) {
        model.addAttribute("appuser", new AppUser());
        return "appuserform";
    }

    @RequestMapping(value = "/appuserform", method = RequestMethod.POST)
    //Retrieves the user information from the html page and processes it into the repository
    public String processRegistrationPage(@Valid @ModelAttribute("appuser") AppUser appuser, BindingResult result, Model model) {
        model.addAttribute("appuser", appuser);
        if (result.hasErrors()) {
            return "appuserform";
        } else {
            model.addAttribute("message", "User Account Successfully Created");
            appUserRepository.save(appuser);
        }
        return "redirect:/";
    }

    @GetMapping("/addusertopics")
    public String addusertopics(Model model) {
        model.addAttribute("item", new Interests());
        return "topic";
    }


    @PostMapping("/addusertopics")
    public String addusertopics(@Valid @ModelAttribute("item") Interests item,
                                BindingResult result, Authentication auth) {

        if (result.hasErrors())
            return "topic";
        interestsRepository.save(item);
        AppUser appUser = appUserRepository.findAppUserByUsername(auth.getName());
        appUser.addInterest(item);
        appUserRepository.save(appUser);
        return "redirect:/userinfopage";
    }

    @GetMapping("/addusercategories")
    public String addusercategories(Model model) {

        model.addAttribute("item", new AppCatagory());
        return "categories";
    }


    @PostMapping("/addusercategories")
    public String addusercategories(@Valid @ModelAttribute("item") AppCatagory item,
                                    BindingResult result, Authentication auth) {

        if (result.hasErrors())
            return "categories";
        categoriesRepository.save(item);
        AppUser appUser = appUserRepository.findAppUserByUsername(auth.getName());
        appUser.addappCatagory(item);
        appUserRepository.save(appUser);
        return "redirect:/userinfopage";
    }

    @PostMapping("/search")
    public String search(HttpServletRequest request, Model model) {

        String searchString = request.getParameter("search");
        RestTemplate restTemplate = new RestTemplate();
        News news = restTemplate.getForObject("https://newsapi.org/v2/top-headlines?q=" + searchString + "&sortBy=publishedAt&apiKey=895d727e71cf4321a9bbcf319375aa47", News.class);
        model.addAttribute("news", news);
        return "frontPage";
    }

    */
}