package com.home.batch.validator;

import java.util.Set;

import javax.validation.ConstraintViolation;

import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.home.batch.model.Claim;

@Component
public class ClaimValidator implements Validator<Claim> {
	
	@Autowired
	private javax.validation.Validator validator;

	@Override
	public void validate(Claim value) throws ValidationException {
		Set<ConstraintViolation<Claim>> constraintViolations =  validator.validate(value);
		
		if(!constraintViolations.isEmpty())
		{
			throw new ValidationException("Claim Validation exception");
		}
		
	}

}
