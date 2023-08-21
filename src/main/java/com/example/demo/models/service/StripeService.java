package com.example.demo.models.service;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.model.Charge;

@Service
public class StripeService {

	@Value("${stripe.key.secret}")
	private String API_SECET_KEY;

	public StripeService() {

	}
	
	public String createCharge(String dni, String token, int amount) {
		
		String chargeId = null;
		
		try {
			Stripe.apiKey = API_SECET_KEY;
			
			Map<String, Object> chargeParams = new HashMap<>();
			chargeParams.put("description","Separación de Lote de "+dni);
			chargeParams.put("currency","pen");
			chargeParams.put("amount",amount);
			chargeParams.put("source",token);
			
			Charge charge = Charge.create(chargeParams);
			
		    chargeId = charge.getId();	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return chargeId;
	}

}
