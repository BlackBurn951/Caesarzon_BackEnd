package org.caesar.productservice.Controller;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.caesar.productservice.Utils.PayPalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

@RestController
@RequestMapping("/product-api")
@RequiredArgsConstructor
public class PayPalController {


    private final PayPalService payPalService;

    @GetMapping("/pay")
    public String payment() {
        try {
            Payment payment = payPalService.createPayment(
                    10.00, "EUR", "paypal",
                    "sale", "Pagamento ordine",
                    "http://localhost:4200/pagamento", //TODO REDIRECT SUL FRONT ANCHE
                    "http://localhost:4200/personal-data");
            for (Links link : payment.getLinks()) {
                if (link.getRel().equals("approval_url")) {
                    return "redirect:" + link.getHref();
                }
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }


    @GetMapping("/success")
    public ResponseEntity<String> successPay(@RequestParam("paymentId") String paymentId, @RequestParam("token") String token, @RequestParam("PayerID") String payerId) {
        Payment payment = payPalService.executePayment(paymentId, payerId);
        if (payment.getState().equals("approved")) {
            return "Success";
        }
        return "Failed";

    }


    @GetMapping("/cancel")
    @ResponseBody
    public String cancelPay() {
        return "Payment cancelled";
    }
}
