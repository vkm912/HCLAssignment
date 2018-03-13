package com.db.awmd.challenge.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Transfer;
import com.db.awmd.challenge.exception.InsufficientBalanceException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TransferService {
	
	@Autowired
	AccountsService accountService;
	
	NotificationService notificationService = (a, d) -> log.info(
			"Sending notification to owner of account " + a.getAccountId() + " : "+ d);
	
	public void transferAmount(Transfer transfer) throws InsufficientBalanceException{
		Account fromAccount = accountService.getAccount(transfer.getFromAccountId());
		Account toAccount = accountService.getAccount(transfer.getToAccountId());
		if(fromAccount.getBalance().compareTo(transfer.getAmountTotransfer()) == -1) {
			throw new InsufficientBalanceException(
					"Accout "+fromAccount.getAccountId()+ " has insufficient balance!");
		}
		synchronized (toAccount) {
			synchronized (fromAccount) {
				BigDecimal initFromBalace = fromAccount.getBalance();
				BigDecimal initToBalace = toAccount.getBalance();
				try {
					fromAccount.setBalance(fromAccount.getBalance().subtract(transfer.getAmountTotransfer()));
					toAccount.setBalance(toAccount.getBalance().add(transfer.getAmountTotransfer()));
					notificationService.notifyAboutTransfer(fromAccount, transfer.getAmountTotransfer()
							+" amount has been transfred to account "+toAccount.getAccountId());
					notificationService.notifyAboutTransfer(toAccount, transfer.getAmountTotransfer()
							+" amount has been received from account "+fromAccount.getAccountId());
				} catch (Exception e) {
					log.error("Exception occured while transferring the amount...rolling back");
					fromAccount.setBalance(initFromBalace);
					toAccount.setBalance(initToBalace);
				}
			}
		}
		
	}
}
