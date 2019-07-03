package com.youxiu326.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/paypal")
public class PaypalController {

    @Value("${stripe.apiKey}")
    private String privateKey;


    @GetMapping("/")
    public String index() {
        return "checkout/paypal";
    }

} 