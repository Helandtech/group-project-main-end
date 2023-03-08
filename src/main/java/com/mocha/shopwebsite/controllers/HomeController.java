package com.mocha.shopwebsite.controllers;

import com.mocha.shopwebsite.data.Item;
import com.mocha.shopwebsite.data.ItemRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ItemRepository itemRepository;

    public HomeController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }


    /**
     * Shows homepage with session data if one is instanced
     * @param model Holds application data, session username and Logged in status
     * @param session Holds session data
     * @return home.html
     */
    @GetMapping(value = {"/", "/home"})
    public String showHomePage(Model model, HttpSession session) {

        model.addAttribute("items", itemRepository.findAll());

        if(Helper.getInstance().isLoggedIn(session)) {
            model.addAttribute("username", session.getAttribute("username"));
            model.addAttribute("loggedIn", true);
        } else {
            model.addAttribute("loggedIn", false);
        }

        return "home";
    }

    @GetMapping("/account")
    public String showAccountPage(Model model, HttpSession session) {
        if(Helper.getInstance().isLoggedIn(session)) {
            return "redirect:/login";
        }

        model.addAttribute("loggedIn", true);
        return "my_account";
    }

    @GetMapping("/about")
    public String showAboutPage(Model model, HttpSession session) {
        model.addAttribute("loggedIn", Helper.getInstance().isLoggedIn(session));

        return "about";
    }
}
