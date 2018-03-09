package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


import javax.validation.Valid;
import java.util.Set;

@Controller
public class MainController {
    @Autowired
    AppRoleRepository appRoleRepository;

    @Autowired
    AppUserRepository appUserRepository;

/*
    @Autowired
    CategoriesRepository    categoriesRepository;
*/

    @Autowired
    IntrestsRepository intrestsRepository;


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
        News newsStore = new News();
        for(Interests interests : user.getInterests()) { //Loops through the users to check for "user" Found Item, and upon finding it sets the item as a found item.
            News news = restTemplate.getForObject("https://newsapi.org/v2/everything?q=" + interests.getInterestName() + "&sortBy=publishedAt&apiKey=895d727e71cf4321a9bbcf319375aa47", News.class);
            for (Articles articles : news.getArticles()) {
                newsStore.addArticles(articles);
            }
        }
        model.addAttribute("news", newsStore);
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
    @GetMapping("/addusercategories")
    public String addusercategories(Model model) {

        model.addAttribute("item", new Interests());

        return "categories";
    }


    @PostMapping("/addusercategories")
    public String addusercategories(@Valid @ModelAttribute("item") Interests item,
                                BindingResult result, Model model, Authentication auth) {

        if (result.hasErrors())
            return "categories";
        intrestsRepository.save(item);
        AppUser currentuser= appUserRepository.findAppUserByUsername(auth.getName());
        currentuser.addInterest(item);
        appUserRepository.save(currentuser);
        return "redirect:/";
    }
/*    @RequestMapping("/addusercategories")
    public String userCatagories(Model model, Authentication authentication){
        AppUser appUser = appUserRepository.findAppUserByUsername(authentication.getName());
        Interests interests = new Interests();
        appUser.addInterest(interests)
        model.addAttribute("appUser", appUser);
        return "categories";
    }

    @PostMapping("/process")
    public String processCatagories(@Valid AppUser appUser, BindingResult result) {
        if (result.hasErrors()) {
            return "categories";
        }
        appUserRepository.save(appUser);
        return "redirect:/";
    }*/

   /* @RequestMapping(value="/processusercatagories",method= RequestMethod.POST) //Retrieves the user information from the html page and processes it into the repository
    public String processUserCatagories(@Valid @ModelAttribute("appUser") AppUser appUser, BindingResult result, Model model){
        if(result.hasErrors()){
            return "appUserform";
        }else{
            appUserRepository.save(appUser);
        }
        return "redirect:/";
    }*/
}

