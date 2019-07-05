package com.youxiu326.controller;

import com.google.gson.JsonSyntaxException;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/stripe")
public class StripeController {

    @Value("${stripe.apiKey}")
    private String privateKey;

    @Value("${stripe.webhookSecret}")
    private String endpointSecret;

    /**
     * checkout唤起支付页面
     * @param model
     * @return
     */
    @GetMapping("/")
    public String index(Model model) {

        try {
            Stripe.apiKey = privateKey;

            Map<String, Object> params = new HashMap<String, Object>();

            //预填充客户邮箱
            //params.put("customer_email", "youxiu326@163.com");

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

    /**
     * 使用token 令牌方式支付
     * @param model
     * @return
     */
    @GetMapping("token")
    public String token(Model model) {
        return "token/stripe";
    }

    @PostMapping("/pay")
    public String pay() {

        return "pay";
    }


    /**
     * 客户端提交token令牌id 后，执行付费操作
     * @param stripeToken
     * @param orderCode
     * @return
     */
    @PostMapping("/charge")
    @ResponseBody
    public String charge(String stripeToken,String orderCode) {
        try {
            //TODO 对orderCode 进行一系列判断

            Stripe.apiKey = privateKey;

            Token token = Token.retrieve(stripeToken);
            if (token==null || token.getUsed()){
                return "token 无效";
            }

            // Create a Customer:
            Map<String, Object> customerParams = new HashMap<>();
            customerParams.put("source", stripeToken);//TODO 使用了测试token tok_visa
            customerParams.put("email", "youxiu326@163.com");
            Customer customer = Customer.create(customerParams);

            {
                // Charge the Customer instead of the card:
                Map<String, Object> chargeParams = new HashMap<>();
                chargeParams.put("amount", 500);
                //chargeParams.put("currency", "usd");//美元
                chargeParams.put("currency", "GBP");//英镑
                chargeParams.put("customer", customer.getId());
                chargeParams.put("description", "这是一个测试的商品描述");
                chargeParams.put("receipt_email", "youxiu326@163.com");//正式环境下付款成功后将会收到邮件信息
                Charge charge = Charge.create(chargeParams);
                System.out.println(charge);
                if ("succeeded".equals(charge.getStatus())){
                    return "支付成功";
                }else{
                    return "支付失败";
                }
            }
        } catch (StripeException e) {
            e.printStackTrace();
        }
        return "支付失败";
    }


    /**
     * webhooks 异步通知
     * @return
     */
    @PostMapping("/webhooks")
    @ResponseBody
    public String webhooks(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Stripe.apiKey = privateKey;

        InputStream inputStream = request.getInputStream();
        byte[] bytes = IOUtils.toByteArray(inputStream);
        String payload = new String(bytes, "UTF-8");

        String sigHeader = request.getHeader("Stripe-Signature");
        Event event = null;
        try {
            event = Webhook.constructEvent(
                    payload, sigHeader, endpointSecret
            );
        } catch (JsonSyntaxException e) {
            response.setStatus(400);
            return "";
        } catch (SignatureVerificationException e) {
            response.setStatus(400);
            return "";
        }

        // Deserialize the nested object inside the event
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().orElse(null);
        } else {
            // Deserialization failed, probably due to an API version mismatch.
            // Refer to the Javadoc documentation on `EventDataObjectDeserializer` for
            // instructions on how to handle this case, or return an error here.
        }

        // Handle the event
        switch (event.getType()) {
            /*case "payment_intent.succeeded":
                PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
                System.out.println(paymentIntent);
                response.setStatus(200);
                break;*/
            case "charge.succeeded":
                //使用token支付成功回调
                Charge charge = (Charge) stripeObject;
                System.out.println(charge);
                //TODO 请处理支付成功业务代码
                response.setStatus(200);
                break;
            case "checkout.session.completed":
                //使用checkout支付成功回调
                Session session = (Session) stripeObject;
                System.out.println(session);
                //TODO 请处理支付成功业务代码
                response.setStatus(200);
                break;
            default:
                response.setStatus(400);
                return "";
        }
        response.setStatus(200);
        return "";
    }

    /**
     * 支付成功回调页面
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

    /**
     * 退款
     * @param returnId
     * @return
     */
    @PostMapping("/refund")
    @ResponseBody
    public String refund(String returnId) {

        try {
            Stripe.apiKey = privateKey;

            Map<String, Object> params = new HashMap<>();
            params.put("charge", "ch_AzFPjXyE00KGmEkvwBNP");
            params.put("amount", 500);
            Refund refund = Refund.create(params);
            if ("succeeded".equals(refund.getStatus())){
                return "退款成功";
            }else {
                return "退款失败";
            }
        } catch (StripeException e) {
            e.printStackTrace();
        }

        return "退款失败";
    }

} 