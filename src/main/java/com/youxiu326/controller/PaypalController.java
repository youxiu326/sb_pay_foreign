package com.youxiu326.controller;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/paypal")
public class PaypalController {

    @Value("${stripe.apiKey}")
    private String privateKey;

    @Autowired
    private APIContext apiContext;


    @GetMapping("/")
    public String index(HttpServletRequest httpRequest, HttpServletResponse httpResponse, Model model) {
        try {
            Amount amount = new Amount();
            amount.setCurrency("GBP");
            amount.setTotal("500");

            Transaction transaction = new Transaction();
            transaction.setDescription("这是一个测试单描述");
            transaction.setCustom(UUID.randomUUID().toString());
            transaction.setAmount(amount);

            List<Transaction> transactions = new ArrayList<>();
            transactions.add(transaction);

            Payer payer = new Payer();
            payer.setPaymentMethod("paypal");
            Payment payment = new Payment();
            payment.setIntent("sale");
            payment.setPayer(payer);
            payment.setTransactions(transactions);
            RedirectUrls redirectUrls = new RedirectUrls();
            redirectUrls.setCancelUrl("https://example.com/success");
            redirectUrls.setReturnUrl("https://example.com/cancel");
            payment.setRedirectUrls(redirectUrls);
            Payment paymentResult = payment.create(apiContext);
            for(Links links : paymentResult.getLinks()){
                if(links.getRel().equals("approval_url")){
                    return "redirect:" + links.getHref();
                }
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
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
    public String paySuccess(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId, Model model){
        try {
            Payment payment = new Payment();
            payment.setId(paymentId);
            PaymentExecution paymentExecute = new PaymentExecution();
            paymentExecute.setPayerId(payerId);
            Payment paymentResult = payment.execute(apiContext, paymentExecute);

            if(payment.getState().equals("approved")){
                //订单编号
                String orderCode = payment.getTransactions().get(0).getCustom();
                //支付成功则做相应处理
                Payment getPayment = Payment.get(apiContext, paymentId);
                //提交到paypal的订单金额
                String total = getPayment.getTransactions().get(0).getAmount().getTotal();
                if(getPayment.getState().equals("approved")){
                    //用户的邮箱
                    String email = getPayment.getPayer().getPayerInfo().getEmail();

                    //如果支付成功可以去做对应操作

                    return "pay-success";
                }else {
                    return "redirect:paypal/payError";
                }
            }

        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
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