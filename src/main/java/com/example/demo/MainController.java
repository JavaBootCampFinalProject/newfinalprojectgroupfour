package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

@Controller
public class MainController {
    @Autowired
    AppRoleRepository appRoleRepository;

    @Autowired
    AppUserRepository appUserRepository;

    @Autowired
    UserCatagoriesRepository userCatagoriesRepository;

    @RequestMapping("/")
    public String  frontPage(Model model) {
        RestTemplate restTemplate = new RestTemplate();
        News news = restTemplate.getForObject("https://newsapi.org/v2/top-headlines?country=us&apiKey=895d727e71cf4321a9bbcf319375aa47", News.class);
        model.addAttribute("news", news);
        return "frontPage";
    }

    @RequestMapping("/userpage")
    public String catagoryPage(Model model, Authentication authentication) {
        AppUser user = appUserRepository.findAppUserByUsername(authentication.getName());
        RestTemplate restTemplate = new RestTemplate();
        News news = restTemplate.getForObject("https://newsapi.org/v2/everything?" + user.search + "apiKey=895d727e71cf4321a9bbcf319375aa47", News.class);
        model.addAttribute("news", news);
        return "frontPage";
    }

    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }


    @RequestMapping("/appuserform")  //For registration and creation of a new user
    public String userRegistration(Model model){
        model.addAttribute("appuser",new AppUser());
        return "appuserform";
    }

    @RequestMapping(value="/appuserform",method= RequestMethod.POST) //Retrieves the user information from the html page and processes it into the repository
    public String processRegistrationPage(@Valid @ModelAttribute("appuser") AppUser appuser, BindingResult result, Model model){
        model.addAttribute("appuser",appuser);
        if(result.hasErrors()){
            return "appuserform";
        }else{
            model.addAttribute("message","User Account Successfully Created");
            appUserRepository.save(appuser);
        }
        return "redirect:/";
    }

    @RequestMapping("/addusercategories")  //For registration and creation of a new user
    public String userCatagories(Model model){
        return "addUserCategories";
    }

    @RequestMapping(value="/addusercategories",method= RequestMethod.POST) //Retrieves the user information from the html page and processes it into the repository
    public String processUserCatagories(@Valid @ModelAttribute("appuser") AppUser appuser, BindingResult result, Model model){
        model.addAttribute("appuser",appuser);
        if(result.hasErrors()){
            return "appuserform";
        }else{
            model.addAttribute("message","User Account Successfully Created");
            appUserRepository.save(appuser);
        }
        return "redirect:/";
    }
}

