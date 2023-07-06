package com.czar.controller;

import com.czar.entity.PaymethodsAtGateway;
import com.czar.entity.Trans;
import com.czar.entity.User;
import com.czar.repository.PaymethodsAtGatewayRepository;
import com.czar.repository.TransRepository;
import com.czar.repository.UserRepository;
import com.czar.service.TransService;
import com.czar.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class UserController {
    @Autowired
    private UserRepository userrepository;
    @Autowired
    private UserService userservice;
    @Autowired
    TransService transservice;
    @Autowired
    TransRepository transrepository;
    private static final Logger logger = LogManager.getLogger(DepositController.class);

    @GetMapping("/login")
    public String loginUser() {
        logger.info("/main");
        return "login";
    }


    @GetMapping("/forgot")
    public String forgot() {
        logger.info("/forgot");
        return "forgot";
    }


    @GetMapping("/data")
    public String getAllTrans(Model model) {
        logger.info("/data");
        try {

            List<Trans> transactions = transrepository.findAllOrderByStartDateDsc();
            model.addAttribute("transactions", transactions);

        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
        }

        return "trans";

    }

    @GetMapping("/data/filter")
    public String getTransaction(Model model,
                                 @RequestParam(name = "gateway", required = false) String gateway,
                                 @RequestParam(name = "paymentMethod", required = false) String paymentMethod,
                                 @RequestParam(name = "state", required = false) String state,
                                 @RequestParam(name = "transactionType", required = false) String transactionType) {

        logger.info("/data/filter");
        List<Trans> transactions = transservice.getAllTransactions();

        try {

            if (paymentMethod != null && !paymentMethod.isEmpty()) {
                transactions = transactions.stream()
                        .filter(t -> t.getPaymentMethod().equals(paymentMethod))
                        .collect(Collectors.toList());
            }
            if (transactionType != null && !transactionType.isEmpty()) {
                transactions = transactions.stream()
                        .filter(t -> t.getTransactionType().equals(transactionType))
                        .collect(Collectors.toList());
            }
            if (state != null && !state.isEmpty()) {
                transactions = transactions.stream()
                        .filter(t -> {
                            if (state.equals("null")) {
                                return t.getState() == null;
                            } else {
                                return t.getState() != null && t.getState().equals(state);
                            }
                        })
                        .collect(Collectors.toList());
            }

            if (gateway != null && !gateway.isEmpty()) {
                transactions = transactions.stream()
                        .filter(t -> {
                            if (gateway.equals("null")) {
                                return t.getGateway() == null;
                            } else {
                                return t.getGateway() != null && t.getGateway().equals(gateway);
                            }
                        })
                        .collect(Collectors.toList());
            }
            System.out.println(gateway);

            model.addAttribute("transactions", transactions);
            model.addAttribute("gateway", gateway);
            model.addAttribute("paymentMethod", paymentMethod);
            model.addAttribute("transactionType", transactionType);
            model.addAttribute("state", state);

            return "trans";

        } catch (Exception e) {
            e.getMessage();
            return getData();

        }
    }

    @GetMapping("/main")
    public String getData() {

        logger.info("main");
        return "homepage";
    }

    @PostMapping("/main")
    public String homeuser(User user, RedirectAttributes ra) {

        List<User> user1 = null;
        try {
            user1 = userrepository.findByPasswordAndLoginNameAndDomain(user.getPassword(), user.getLoginName(), user.getDomain());
            if (user1.size() > 0) {
                return "homepage";
            } else {
                ra.addFlashAttribute("message", "invalid credential");
                ra.addAttribute("message", "invalid data");
                return "redirect:login?error";
            }
        } catch (Exception e) {
            return e.getMessage();
        }

    }


    @Autowired
    private PaymethodsAtGatewayRepository gatewayPaymethods;

    @GetMapping("/paymethodslist")
    public String getAllpaymethods(Model model) {
        logger.info("/paymethodslist");
        try {

            List<PaymethodsAtGateway> paymethodsAtgateway = gatewayPaymethods.findAll();
            logger.info("paymethodsAtgateway : " + paymethodsAtgateway);
            model.addAttribute("paymethodsAtgateway", paymethodsAtgateway);

        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
        }

        return "listpaymethods";

    }

}
