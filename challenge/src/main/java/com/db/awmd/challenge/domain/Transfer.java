package com.db.awmd.challenge.domain;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Transfer {
	
	  @NotNull
	  @NotEmpty
	  private final String fromAccountId;
	  
	  @NotNull
	  @NotEmpty
	  private final String toAccountId;

	  @NotNull
	  @Min(value = 0, message = "Amount to transfer must be positive.")
	  private BigDecimal amountTotransfer;
	  
	  @JsonCreator
	  public Transfer(@JsonProperty("fromAccountId") String fromAccountId,
	    @JsonProperty("toAccountId") String toAccountId,	  
	    @JsonProperty("amountToTransfer") BigDecimal amountTotransfer) {
	    this.fromAccountId = fromAccountId;
	    this.toAccountId = toAccountId;
	    this.amountTotransfer = amountTotransfer;
	  }

}
