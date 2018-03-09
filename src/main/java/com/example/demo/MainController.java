package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
public class MainController {
    @Autowired
    AppRoleRepository appRoleRepository;

    @Autowired
    AppUserRepository appUserRepository;

    @Autowired
    CategoriesRepository categoriesRepository;


    @Autowired
    InterestsRepository interestsRepository;


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
}