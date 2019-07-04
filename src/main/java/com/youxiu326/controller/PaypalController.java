package com.youxiu326.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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