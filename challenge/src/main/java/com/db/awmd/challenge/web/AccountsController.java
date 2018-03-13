package com.db.awmd.challenge.web;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Transfer;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.InsufficientBalanceException;
import com.db.awmd.challenge.exception.NoSuchAccountException;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.TransferService;

import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/accounts")
@Slf4j
public class AccountsController {

  private final AccountsService accountsService;
  
  private final TransferService transferService;

  @Autowired
  public AccountsController(AccountsService accountsService, TransferService transferService) {
    this.accountsService = accountsService;
    this.transferService = transferService;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> createAccount(@RequestBody @Valid Account account) {
    log.info("Creating account {}", account);

    try {
    this.accountsService.createAccount(account);
    } catch (DuplicateAccountIdException daie) {
      return new ResponseEntity<>(daie.getMessage(), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @GetMapping(path = "/{accountId}")
  public Object getAccount(@PathVariable String accountId) {
    log.info("Retrieving account for id {}", accountId);
    Account account = null;
    try {
    	account = this.accountsService.getAccount(accountId);
    } catch (NoSuchAccountException nsae) {
    	return new ResponseEntity<>(nsae.getMessage(), HttpStatus.BAD_REQUEST);
    }
    return account;
  }
  
  @PostMapping(path = "/transfer", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> transferAmount(@RequestBody @Valid Transfer transfer) {
    log.info("Transfer in progress {}", transfer);

    try {
    this.transferService.transferAmount(transfer);
    } catch (NoSuchAccountException | InsufficientBalanceException ex) {
      return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(HttpStatus.ACCEPTED);
  }

}
