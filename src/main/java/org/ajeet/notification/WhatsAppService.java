package org.ajeet.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.nio.charset.StandardCharsets;

/**
 * Sends WhatsApp messages through the Twilio REST API.
 * Set WHATSAPP_ENABLED=true plus TWILIO_ACCOUNT_SID / TWILIO_AUTH_TOKEN /
 * TWILIO_WHATSAPP_FROM to enable; otherwise messages are logged.
 */
@Service
public class WhatsAppService {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppService.class);
    private static final String TWILIO_API = "https://api.twilio.com/2010-04-01";

    private final boolean enabled;
    private final String accountSid;
    private final String authToken;
    private final String fromNumber;
    private final RestClient restClient;

    public WhatsAppService(@Value("${app.notifications.whatsapp.enabled:false}") boolean enabled,
                           @Value("${app.notifications.whatsapp.account-sid:}") String accountSid,
                           @Value("${app.notifications.whatsapp.auth-token:}") String authToken,
                           @Value("${app.notifications.whatsapp.from-number:}") String fromNumber,
                           RestClient.Builder restClientBuilder) {
        this.enabled = enabled;
        this.accountSid = accountSid;
        this.authToken = authToken;
        this.fromNumber = fromNumber;
        this.restClient = restClientBuilder.build();
    }

    public void send(String toPhone, String body) {
        if (!enabled) {
            log.info("[whatsapp:dev] to={} body={}", toPhone, body);
            return;
        }

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("To", "whatsapp:" + toPhone);
        form.add("From", fromNumber.startsWith("whatsapp:") ? fromNumber : "whatsapp:" + fromNumber);
        form.add("Body", body);

        restClient.post()
                .uri(TWILIO_API + "/Accounts/" + accountSid + "/Messages.json")
                .header(HttpHeaders.AUTHORIZATION, basicAuth())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .toBodilessEntity();

        log.info("[whatsapp] sent to={}", toPhone);
    }

    private String basicAuth() {
        String credentials = accountSid + ":" + authToken;
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }
}
