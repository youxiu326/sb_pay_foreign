package com.youxiu326.controller;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/stripe")
public class StripeController {

    @Value("${stripe.apiKey}")
    private String privateKey;


    @GetMapping("/")
    public String index(Model model) {

        try {
            Stripe.apiKey = "sk_test_S1xsxKPNYknXjKqUoF915Ior00oiKImo2G";

            Map<String, Object> params = new HashMap<String, Object>();

            ArrayList<String> paymentMethodTypes = new ArrayList<>();
            paymentMethodTypes.add("card");
            params.put("payment_method_types", paymentMethodTypes);

            ArrayList<HashMap<String, Object>> lineItems = new ArrayList<>();
            HashMap<String, Object> lineItem = new HashMap<String, Object>();
            lineItem.put("name", "youxiu326");
            lineItem.put("description", "这是一个测试单描述");
            lineItem.put("amount", 500);
            lineItem.put("currency", "gbp");
            lineItem.put("quantity", 1);
            lineItems.add(lineItem);
            params.put("line_items", lineItems);

            params.put("success_url", "https://example.com/success");
            params.put("cancel_url", "https://example.com/cancel");
            Session session = Session.create(params);
            model.addAttribute("CHECKOUT_SESSION_ID",session.getId());
        } catch (StripeException e) {
            e.printStackTrace();
        }
        return "checkout/stripe";
    }

    @PostMapping("/pay")
    public String pay() {


        return "pay";
    }

    /**
     * 支付成功页面
     * @return
     */
    @GetMapping("/paySuccess")
    public String paySuccess(){
        return "pay-success";
    }

    /**
     * 支付失败页面
     * @return
     */
    @GetMapping("/payError")
    public String payError(){
        return "pay-error";
    }


} 