package org.caesar.productservice.Controller;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.caesar.productservice.Utils.PayPalService;
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
                    10.00, "USD", "paypal",
                    "sale", "Test payment",
                    "http://localhost:8090/product-api/cancel",
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
    public String successPay(@RequestParam("paymentId") String paymentId, @RequestParam("token") UUID token, @RequestParam("PayerID") String payerId) {
        System.out.println("SONO NEL SUCCESS");
        Payment payment = payPalService.executePayment(paymentId, payerId);
        System.out.println("Stato pagamento: " + payment.getState().equals("approved"));
        if (payment.getState().equals("approved")) {
            // Verifica l'account destinatario
            System.out.println("Email: " + payment.getTransactions().getFirst().getPayee().getEmail());
            String payeeEmail = payment.getTransactions().getFirst().getPayee().getEmail();
            if (payeeEmail.equals("sb-543xfo26747131@business.example.com")) {
                return "Success";
            } else {
                return "Failed: Payment sent to wrong account.";
            }
        }
        return "Failed";

    }


    @GetMapping("/cancel")
    @ResponseBody
    public String cancelPay() {
        return "Payment cancelled";
    }
}
