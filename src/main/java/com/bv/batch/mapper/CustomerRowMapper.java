package com.bv.batch.mapper;

import com.bv.batch.domain.Customer;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerRowMapper implements RowMapper<Customer> {

	@Override
	public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
		return Customer.builder()
			.id(rs.getLong("id"))
			.firstName(rs.getString("firstName"))
			.lastName(rs.getString("lastName"))
			.birthdate(rs.getString("birthdate"))
			.build();
	}

}
