
###### 自己整合外国支付 stripe 与 paypal



```checkout文档链接```
<br>

https://developer.paypal.com/demo/checkout/#/pattern/client
<br>
https://developer.paypal.com/demo/checkout/#/pattern/server
<br>
https://stripe.com/docs/payments/checkout/client
<br>
https://stripe.com/docs/payments/checkout/server
<br>


```
回调地址都需要时https，不然报错


```


```
测试stripe 卡号:
Card numbers:
    4242424242424242
Tokens:
    tok_visa



①、②、③、④、⑤、⑥、⑦、⑧、⑨、⑩
stripe checkout流程说明:
①、服务端端传入订单信息，创建session，并将session id 返回至前台
②、服务端取得客户端传来的session id 去支付
③、支付成功后会重定向至您定义的成功或者失败页面
④、应该在webhooks 异步通知中完成最终支付(成功的回调页面不做处理)


[session说明](https://stripe.com/docs/api/checkout/sessions)
session json格式说明:
{
  "id": "cs_test_s4MIEfeZbWvQDSO7A4Bcgpfi0odKeWW6bTCokCuGhEnI8B8mbXcB3Wes",
  "object": "checkout.session",
  "billing_address_collection": null,
  "cancel_url": "https://example.com/cancel",
  "client_reference_id": null,
  "customer": null,
  "customer_email": null,
  "display_items": [
    {
      "amount": 1500,
      "currency": "usd",
      "custom": {
        "description": "Comfortable cotton t-shirt",
        "images": null,
        "name": "T-shirt"
      },
      "quantity": 2,
      "type": "custom"
    }
  ],
  "livemode": false,
  "locale": null,
  "payment_intent": "pi_1EUmyo2x6R10KRrhUuJXu9m0",
  "payment_method_types": [
    "card"
  ],
  "submit_type": null,
  "subscription": null,
  "success_url": "https://example.com/success"
}

webhooks回调地址:
http://test.youxiu326.xin/stripe/webhooks


stripe token流程说明:
①、客户端支付成功后，创建token，并将token id 返回至后台
②、服务端取得客户端传来的token id 去支付创建Charge对象
③、支付成功后将Charge id保存，后续将以Charge id 用于退款
④、应该在webhooks 异步通知中完成最终支付(成功的回调页面不做处理)



```