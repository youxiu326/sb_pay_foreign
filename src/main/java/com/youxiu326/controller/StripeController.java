package com.youxiu326.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/stripe")
public class StripeController {

    @Value("${stripe.apiKey}")
    private String privateKey;


    @GetMapping("/")
    public String index() {
        return "checkout/stripe";
    }



} 