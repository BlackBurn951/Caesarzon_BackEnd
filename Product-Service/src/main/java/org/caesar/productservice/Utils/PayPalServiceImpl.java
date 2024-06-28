package org.caesar.productservice.Utils;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.caesar.productservice.Data.Services.PayPalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.Error;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class PayPalServiceImpl implements PayPalService {

    private static final Logger log = LoggerFactory.getLogger(PayPalServiceImpl.class);

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
            payment.setId(paymentId);
            PaymentExecution paymentExecute = new PaymentExecution();
            paymentExecute.setPayerId(payerId);
            APIContext apiContext = new APIContext(clientId, clientSecret, mode);
            Payment executedPayment = payment.execute(apiContext, paymentExecute);
            // Verifica l'account destinatario
            String payeeEmail = executedPayment.getTransactions().getFirst().getPayee().getEmail();

            return executedPayment;
        }catch (Exception | Error e){
            log.debug("Errore nel pagamento con paypal");
            return payment;
        }

    }

}
