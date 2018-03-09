package com.home.batch.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.home.batch.model.Claim;

/**
 * @author 613316
 *
 */
public class ClaimRowMapper implements RowMapper<Claim>{

	public Claim mapRow(ResultSet claimRow, int rowNum) throws SQLException {
		Claim claimModel = new Claim();
		claimModel.setId(claimRow.getInt("id"));
		claimModel.setClaimId(claimRow.getInt("ClaimId"));
		claimModel.setInsuredId(claimRow.getInt("InsuredId"));
		claimModel.setAmount(claimRow.getInt("Amount"));
		claimModel.setPatientName(claimRow.getString("PatientName"));
		
		return claimModel;
	}

}
