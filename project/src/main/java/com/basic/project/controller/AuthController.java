package com.basic.project.controller;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.basic.project.TimeConversionUtil;
import com.basic.project.entity.*;
import com.basic.project.repository.*;
import com.basic.project.service.ProfileService;
import com.basic.project.service.TransactionService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {
    
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionService transactionService;

    @GetMapping("/")
    public String index(HttpSession session) {
        String sessionToken = (String) session.getAttribute("sessionToken");
        if (sessionToken != null) {
            User user = userRepository.findBySessionToken(sessionToken);
            if (user != null) {
                return "dashboard";
            }
        }
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        String sessionToken = (String) session.getAttribute("sessionToken");
        if (sessionToken != null) {
            User user = userRepository.findBySessionToken(sessionToken);
            if (user != null) {
                List<Transaction> transactions = user.getTransactions();

                double totalIncome = transactions.stream()
                    .filter(transaction -> transaction.getType().equals("Income"))
                    .mapToDouble(Transaction::getAmount)
                    .sum();

                double totalExpenses = transactions.stream()
                    .filter(transaction -> transaction.getType().equals("Expense"))
                    .mapToDouble(Transaction::getAmount)
                    .sum();

                double cashOnHand = totalIncome - totalExpenses;

                model.addAttribute("totalIncome", totalIncome);
                model.addAttribute("totalExpenses", totalExpenses);
                model.addAttribute("cashOnHand", cashOnHand);
                model.addAttribute("totalRecords", transactions.size());
                
                List<Transaction> latestTransactions = transactionService.getLatestTransactions(user, 3);
                model.addAttribute("latestTransactions", latestTransactions);
                

                return "dashboard";
            }
        }
        return "login";
    }

    @GetMapping("/edit-transaction/{id}")
    public String editTransaction(@PathVariable int id, Model model) {
        Transaction transaction = transactionService.getTransactionById(id);
        
        model.addAttribute("transaction", transaction);
        
        return "edit-transaction";
    }
    @PostMapping("/edit-transaction/{id}")
    public String saveEditedTransaction(@PathVariable int id, @ModelAttribute Transaction editedTransaction) {
        Transaction originalTransaction = transactionService.getTransactionById(id);
        originalTransaction.setType(editedTransaction.getType());
        originalTransaction.setAmount(editedTransaction.getAmount());
        originalTransaction.setDate(editedTransaction.getDate());
        originalTransaction.setTime(editedTransaction.getTime());
        originalTransaction.setDescription(editedTransaction.getDescription());
        
        transactionService.saveTransaction(originalTransaction);
        
        return "redirect:/records";
    }

    @GetMapping("/records")
    public String records(Model model, HttpSession session) {
        String sessionToken = (String) session.getAttribute("sessionToken");
        if (sessionToken != null) {
            User user = userRepository.findBySessionToken(sessionToken);
            if (user != null) {

                List<Transaction> latestTransactions = transactionService.getAllTransactions(user);
                model.addAttribute("latestTransactions", latestTransactions);
    	        return "records";
         
            }
        }
        return "login";
    }
    @GetMapping("/register")
    public String register(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "register";
        
    }
    @GetMapping("/add-transaction")
    public String addTransaction(HttpSession session) {
    	String sessionToken = (String) session.getAttribute("sessionToken");
        if (sessionToken != null) {
        	return "add-transaction";
        }
        return "login";
        
    }


    @PostMapping("/add-transaction")
    public String saveTransaction(@RequestParam("type") String type, @RequestParam("amount") double amount, @RequestParam("time") String time, @RequestParam("date") Date date, @RequestParam("description") String description, HttpSession session) {
			String sessionToken = (String) session.getAttribute("sessionToken");
			User user = userRepository.findBySessionToken(sessionToken);
			if (sessionToken != null) {
				Transaction transaction = new Transaction();
			    transaction.setType(type);
			    transaction.setAmount(amount);
				try {
					Time newtime = TimeConversionUtil.convertStringToTime(time);
					transaction.setTime(newtime);
				} catch (ParseException e) {
				}
			    transaction.setDate(date);
			    transaction.setDescription(description);
			    transaction.setUser(user);
	
			    transactionService.saveTransaction(transaction);
	
			    return "redirect:/records";
			}

	        return "login";
		}

    @GetMapping("/delete-transaction/{id}")
    public String deleteTransaction(@PathVariable("id") Long id, Model model) {
    	transactionService.deleteTransaction(id);
        return "redirect:/records";
    }
    
    
    

    
    @PostMapping("/register")
    public String addUser(@Validated User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register";
        }
        userRepository.save(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password, Model model, HttpSession session) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            String sessionToken = UUID.randomUUID().toString();
            user.setSessionToken(sessionToken);
            userRepository.save(user);
            session.setAttribute("sessionToken", sessionToken);
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Username or password is incorrect.");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("sessionToken");
        return "redirect:/login";
    }
    

    @Autowired
    private ProfileService profileService; // คลาสให้บริการการจัดการ Profile
    
    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        String sessionToken = (String) session.getAttribute("sessionToken");
        if (sessionToken != null) {
            User user = userRepository.findBySessionToken(sessionToken);
            if (user != null) {
                Profile profile = profileService.getProfileByUser(user);
                System.out.print(profile);
                if (profile != null) {
	                model.addAttribute("profile", profile);
	    	        return "view-profile";
	    	        }
                return "create-profile";
         
            }
        }
        return "login";
    }
    @GetMapping("/create-profile")
    public String creatProfile(HttpSession session) {
    	String sessionToken = (String) session.getAttribute("sessionToken");
        if (sessionToken != null) {
        	return "create-profile";
        }
        return "login";
        
    }


    @PostMapping("/save-profile")
    public String saveProfile(@RequestParam("fname") String fname,
    						  @RequestParam("lname") String lname, 
    						  @RequestParam("email") String email, 
    						  @RequestParam("telephone") String telephone,
    						  HttpSession session) {
			String sessionToken = (String) session.getAttribute("sessionToken");
			User user = userRepository.findBySessionToken(sessionToken);
			if (sessionToken != null) {
		    	Profile profile = new Profile();
		    	profile.setId(user.getUserID());
		    	profile.setUser(user);
		    	profile.setFname(fname);
		    	profile.setLname(lname);
		    	profile.setEmail(email);
		    	profile.setTelephone(telephone);
		    	profileService.saveProfile(profile);
	
			    return "redirect:/profile";
			}

	        return "login";
		}
    @GetMapping("/edit-profile/{id}")
    public String editProfile(@PathVariable int id, Model model) {
        Profile profile = profileService.getProfileById(id);
        
        model.addAttribute("profile", profile);
        
        return "edit-profile";
    }
    
    @PostMapping("/edit-profile/{id}")
    public String saveEditedProfile(@PathVariable int id, @ModelAttribute Profile editedprofile) {
    	Profile profile = profileService.getProfileById(id);
	    	profile.setFname(editedprofile.getFname());
	    	profile.setLname(editedprofile.getLname());
	    	profile.setEmail(editedprofile.getEmail());
	    	profile.setTelephone(editedprofile.getTelephone());
	    	profileService.saveProfile(profile);
        
        return "redirect:/profile";
    }




}