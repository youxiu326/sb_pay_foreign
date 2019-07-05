
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

[charge说明](https://stripe.com/docs/api/charges/object)
charge json格式说明:
{
  "id": "ch_1EsgisCZQYXRW1ySbEYA0CGC",
  "object": "charge",
  "amount": 100,
  "amount_refunded": 0,
  "application": null,
  "application_fee": null,
  "application_fee_amount": null,
  "balance_transaction": "txn_1EqFZ7CZQYXRW1ySAIVwCw7n",
  "billing_details": {
    "address": {
      "city": null,
      "country": null,
      "line1": null,
      "line2": null,
      "postal_code": null,
      "state": null
    },
    "email": null,
    "name": "Jenny Rosen",
    "phone": null
  },
  "captured": false,
  "created": 1562292026,
  "currency": "gbp",
  "customer": null,
  "description": "My First Test Charge (created for API docs)",
  "destination": null,
  "dispute": null,
  "failure_code": null,
  "failure_message": null,
  "fraud_details": {},
  "invoice": null,
  "livemode": false,
  "metadata": {},
  "on_behalf_of": null,
  "order": null,
  "outcome": null,
  "paid": true,
  "payment_intent": null,
  "payment_method": "card_1EsgbmCZQYXRW1ySdimjTjPr",
  "payment_method_details": {
    "card": {
      "brand": "visa",
      "checks": {
        "address_line1_check": null,
        "address_postal_code_check": null,
        "cvc_check": null
      },
      "country": "US",
      "exp_month": 8,
      "exp_year": 2020,
      "fingerprint": "pEmaXtWXvgqmxRma",
      "funding": "credit",
      "last4": "4242",
      "three_d_secure": null,
      "wallet": null
    },
    "type": "card"
  },
  "receipt_email": null,
  "receipt_number": null,
  "receipt_url": "https://pay.stripe.com/receipts/acct_1EmY0uCZQYXRW1yS/ch_1EsgisCZQYXRW1ySbEYA0CGC/rcpt_FNRcMiXOCecEaGCcDKytnTasloyycNn",
  "refunded": false,
  "refunds": {
    "object": "list",
    "data": [],
    "has_more": false,
    "total_count": 0,
    "url": "/v1/charges/ch_1EsgisCZQYXRW1ySbEYA0CGC/refunds"
  },
  "review": null,
  "shipping": null,
  "source": {
    "id": "card_1EsgbmCZQYXRW1ySdimjTjPr",
    "object": "card",
    "address_city": null,
    "address_country": null,
    "address_line1": null,
    "address_line1_check": null,
    "address_line2": null,
    "address_state": null,
    "address_zip": null,
    "address_zip_check": null,
    "brand": "Visa",
    "country": "US",
    "customer": null,
    "cvc_check": null,
    "dynamic_last4": null,
    "exp_month": 8,
    "exp_year": 2020,
    "fingerprint": "pEmaXtWXvgqmxRma",
    "funding": "credit",
    "last4": "4242",
    "metadata": {},
    "name": "Jenny Rosen",
    "tokenization_method": null
  },
  "source_transfer": null,
  "statement_descriptor": null,
  "status": "succeeded",
  "transfer_data": null,
  "transfer_group": null
}
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
③、支付成功后将Charge id保存并直接指向支付完成逻辑，后续将以Charge id 用于退款
④、应该在webhooks 异步通知中根据关联的Charge id完成支付



```