package com.mocha.shopwebsite.controllers;
import com.mocha.shopwebsite.data.User;
import com.mocha.shopwebsite.data.UserRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

 @Autowired
 UserRepository userRepository;


 /**
  * Log in existent user and redirect to home page
  *
  *
  * @return home.html
  */
 @GetMapping("/login")
 public String showLogin(Model model, HttpSession session) {
  boolean loggedIn = session.getAttribute("username") != null;

  if(loggedIn) {
   return "redirect:/home";
  }

  model.addAttribute("loginForm", new User());
  model.addAttribute("loggedIn", Helper.getInstance().isLoggedIn(session));

  return "sign-in";
 }

 /**
  * Check for Credentials of users to log in
  *
  * @param session Holds session data
  * @return home.html or sign-in.html
  */

 @PostMapping("/login")
 public String login(@ModelAttribute(name="loginForm") User user, Model m, HttpSession session) {
  User foundUser = userRepository.findUserByUsername(user.getUsername());

  String uname = user.getUsername();
  String pass = user.getPassword();

  if(foundUser != null) {
   if(uname.equals(foundUser.getUsername()) && pass.equals(foundUser.getPassword())) {
    session.setAttribute("username", uname);
    return "redirect:home";
   } else {
    m.addAttribute("error", "Invalid username");
    return "sign-in";
   }
  } else {
   m.addAttribute("error", "Invalid username");
   return "sign-in";
  }
 }


 /**
  * Allow user to create a new account
  *
  * @return home.html or register
  */
 @GetMapping("/register")
 public String getRegisterPage(HttpSession session, Model model) {
  boolean loggedIn = session.getAttribute("username") != null;
  model.addAttribute("loggedIn", Helper.getInstance().isLoggedIn(session));

  if(loggedIn) {
   return "home";
  }

  model.addAttribute("newUser", new User());
  return "register";
 }




 /**
  * Save the new user credentials and then redirect to home page
  *
  * @return home.html
  */
 @PostMapping("/register")
 public String registerAccount(@ModelAttribute User user, Model m, HttpSession session) {
  userRepository.save(user);
  session.setAttribute("username", user.getUsername());
  return "redirect:/home";
 }



 /**
  * log out of an account
  *
  *
  * @return home.html
  */
 @GetMapping("/signOut")
 public String signOut(HttpSession session) {
  session.removeAttribute("username");
  return "redirect:/home";
 }
}