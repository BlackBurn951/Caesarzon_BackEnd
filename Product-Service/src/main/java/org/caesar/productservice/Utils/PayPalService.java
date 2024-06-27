package org.caesar.productservice.Utils;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.Error;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class PayPalService {

    private static final Logger log = LoggerFactory.getLogger(PayPalService.class);
    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    public Payment createPayment(Double total, String currency, String method,
                                 String intent, String description, String cancelUrl, String successUrl) throws PayPalRESTException {

        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(String.format(Locale.US,"%.2f", total)); // Formattare correttamente l'importo


        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod(method);

        Payment payment = new Payment();
        payment.setIntent(intent);
        payment.setPayer(payer);
        payment.setTransactions(transactions);
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);
        payment.setRedirectUrls(redirectUrls);

        APIContext apiContext = new APIContext(clientId, clientSecret, mode);
        return payment.create(apiContext);
    }


    public Payment executePayment(String paymentId, String payerId){
        Payment payment = new Payment();
        try{
            System.out.println("stampa 1");
            System.out.println("stampa 2");
            payment.setId(paymentId);
            System.out.println("stampa 3");
            PaymentExecution paymentExecute = new PaymentExecution();
            System.out.println("stampa 4");
            paymentExecute.setPayerId(payerId);
            System.out.println("stampa 5");
            APIContext apiContext = new APIContext(clientId, clientSecret, mode);
            System.out.println("stampa 6");
            Payment executedPayment = payment.execute(apiContext, paymentExecute);
            System.out.println("stampa 7");
            // Verifica l'account destinatario
            String payeeEmail = executedPayment.getTransactions().getFirst().getPayee().getEmail();
            System.out.println("Payment received by: " + payeeEmail);

            return executedPayment;
        }catch (Exception | Error e){
            log.debug("Diocane");
            return payment;
        }

    }

}
