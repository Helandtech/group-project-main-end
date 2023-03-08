package com.mocha.shopwebsite.controllers;

import com.mocha.shopwebsite.data.*;

import jakarta.servlet.http.HttpSession;
import org.hibernate.annotations.NotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@CrossOrigin
@Controller

//no autowired use constructor
public class ItemsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(com.mocha.shopwebsite.controllers.ItemsController.class);

    private final ItemRepository itemRepository;
    private final BasketRepository basketRepository;
    private final UserRepository userRepository;

    public ItemsController(ItemRepository itemRepository,
                           BasketRepository basketRepository,
                           UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.basketRepository = basketRepository;
        this.userRepository = userRepository;

    }

    /**
     * Method returns all items in itemRepository to catalog.html page to be displayed on that page
     * @param model   the Model that is used to render dynamic data into the view
     * @param session the HttpSession of the request calling this interface
     * @return the catalog template name
     */
    @GetMapping("/items")
    public String showItemsPage(Model model, HttpSession session) {
        Iterable<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        model.addAttribute("loggedIn", Helper.getInstance().isLoggedIn(session));
        return "catalog";
    }


    /**
     * Retrieves the add item page based upon access control
     * @param model   holds application data
     * @param session holds current HttpSession data
     * @return the add item page template name or a redirect to the login if not authenticated
     */
    @GetMapping("/add")
    public String showAddItemPage(Model model, HttpSession session) {

        if (session.getAttribute("username") == null) {
            return "redirect:/login";
        }

        model.addAttribute("item", new Item());
        model.addAttribute("loggedIn", session.getAttribute("username") != null);
        return "add-listing";
    }
    /**
     * Adds an item object to itemRepository through JpaRepository method save().
     *
     * @param item    item object to be added
     * @param model   holds application data, item object in this instance
     * @param session holds session data, user credentials in this instance
     * @return redirects to showItemsPage method
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addItemSubmit(@ModelAttribute Item item, Model model, HttpSession session) {
        //model.addAttribute("item", item); // todo remove this line if it works without this
        User user = userRepository.findUserByUsername((String) session.getAttribute("username"));
        item.setUserId(user.getId());
        LOGGER.info("User ID:{}", item.getUserId());
        itemRepository.save(item);

        return "redirect:/items";
    }

    /**
     * Finds item object in itemRepository through its unique identifier
     *
     * @param id      unique identifier of item required
     * @param model   holds application data, item object in this instance
     * @param session holds session data, user credentials in this instance
     * @return detail_product.html
     */
    @RequestMapping("/item")
    public String getItem(@RequestParam long id, Model model, HttpSession session) {
        Optional<Item> foundItem = itemRepository.findById(id);

        if (foundItem.isPresent()) {
            Item item = foundItem.get();
            model.addAttribute("item", item);
            LOGGER.info("Item name:{}", item.getName());
        }

        model.addAttribute("loggedIn", Helper.getInstance().isLoggedIn(session));

        return "detail_product";
    }
    /**
     * Returns a link to itemsDelete.html containing all item objects in itemRepository
     *
     * @param model   holds application data
     * @param session holds HttpSession data
     * @return itemsDelete.html
     */

    @GetMapping("/itemsDelete")
    public String showItemsDeletePage(Model model, HttpSession session) {
        Iterable<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);

        model.addAttribute("loggedIn", Helper.getInstance().isLoggedIn(session));

        return "itemsDelete";
    }

    /**
     * Adds item object to itemRepository through JpaRepository method save()
     *
     * @param items object to be added
     * @return a redirect to method requesting /list
     */

    @PostMapping("/saveItem")
    public String saveEmployee(@ModelAttribute Item items) {
        itemRepository.save(items);
        return "redirect:/list";
    }

    /**
     * Shows form to update item list.
     *
     * @param id Unique identifier of item in items list.
     * @return detail_product.html || add-item-form.html
     */


    @GetMapping("/showUpdateForm")
    public ModelAndView showUpdateForm(@RequestParam Long id) {
        ModelAndView mav = new ModelAndView("add-item-form");
        Optional<Item> optionalItem = itemRepository.findById(id);
        if (optionalItem.isEmpty()) {
            return new ModelAndView("detail_product");
        }
        Item items = optionalItem.get();
        mav.addObject("items", items);
        return mav;
    }

    /**
     * Deletes item from itemRepository based on its unique identifier
     *
     * @param id unique identifier of item to be deleted
     * @return redirect to /itemsDelete
     */
    @PostMapping("/delete")
    public String deleteItem(@RequestParam Long id) {
        itemRepository.deleteById(id);
        return "redirect:/itemsDelete";
    }

    /**
     * Update item object based on its unique identifier.
     *
     * @param id        Unique identifier of item to be updated
     * @param string    Image URL string to update item Image property
     * @param stringone Name string to update item Name property
     * @return a redirect to /items
     */
    @PostMapping("/update")
    public String updateItem(@RequestParam Long id, @RequestParam String string,
                             @RequestParam String stringone) {
        Optional<Item> optionalItem = itemRepository.findById(id);
        if (optionalItem.isPresent()) {
            Item item = optionalItem.get();
            item.setName(stringone);
            item.setImage(string);
            itemRepository.save(item);
        }
        return "redirect:/items";
    }

    /**
     * Method to add i
     *
     * @param id      unique identifier to set to Basket Object
     * @param session Holds HttpSession data if one has been instanced
     * @return redirects to /login || /items
     */
    @GetMapping("/addToBasket")
    public String addToBasket(@RequestParam long id, HttpSession session) {
        String username = (String) session.getAttribute("username");

        if (username == null) {
            return "redirect:/login";
        }

        Basket basket = new Basket();
        User user = userRepository.findUserByUsername(username);
        basket.setItemId(id);
        basket.setUserId(user.getId());
        LOGGER.info("Basket item ID:{}", basket.getItemId());
        basketRepository.save(basket);

        return "redirect:/items";
    }

    /**
     * Returns number of items in itemRepository
     *
     * @return numOfRecords variable with a value of number of items
     */
    @GetMapping("/records")
    @ResponseBody
    public long getNumberOfRecords() {
        return itemRepository.count();
    }


    /**
     * Creates a list to add items based on a unique identifier supplied by basket object, adds item to list if item is present
     *
     * @param model   Holds application data, item objects in this instance
     * @param session Holds session data if one has been instanced
     * @return checkout
     */
    @GetMapping("/checkout")
    public String viewBasket(Model model, HttpSession session) {

        User user = getUser(session);

        List<Basket> orders = basketRepository.findByUserId(user.getId());
        List<Item> items = new ArrayList<>();

        for (Basket basket : orders) {
            Optional<Item> item = itemRepository.findById(basket.getId());
            item.ifPresent(items::add);
        }

        model.addAttribute("items", items); // adding to model
        model.addAttribute("loggedIn", Helper.getInstance().isLoggedIn(session));

        return "checkout"; // return the basket
    }

    private User getUser(HttpSession session) {
        return userRepository.findUserByUsername(
                (String) session.getAttribute("username"));
    }

    /**
     * deletes basket object from basketRepository
     *
     * @param id unique identifier for Basket object to be deleted
     * @return checkout
     */
    @PostMapping("/checkoutOne")
    public String deleteItem(@RequestParam Integer id) {
        basketRepository.deleteById(id);
        return "checkout";
    }

    /**
     * Deletes all objects from basketRepository when checking out
     *
     * @return checkingOut
     */
    @PostMapping("/checkingOut")
    public String checkingOut() {
        basketRepository.deleteAll();
        return "checkingOut";
    }
}