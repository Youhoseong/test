package com.example.usecase.sendmoney

import com.example.domain.sendmoney.SendMoneyRepository
import com.example.domain.sendmoney.SendMoney
import com.example.domain.user.UserRepository
import com.example.usecase.sendmoney.port.`in`.SendMoneyInPort
import com.example.usecase.support.port.out.ManageTransactionOutPort
import com.example.usecase.wallet.WalletRepository
import org.springframework.stereotype.Component
import java.lang.RuntimeException

@Component
class SendMoneyUseCase(
    private val sendMoneyRepository: SendMoneyRepository,
    private val walletRepository: WalletRepository,
    private val userRepository: UserRepository,
    private val manageTransactionPort: ManageTransactionOutPort,
) : SendMoneyInPort {
    override fun invoke(
        fromUserId: Long,
        toUserId: Long,
        amount: Long,
    ): SendMoney {
        val initialized = SendMoney.initialize(fromUserId, toUserId, amount)
        this.validateUserStatus(fromUserId, toUserId)
        this.validateDailyTotalSendMoneyAmount(fromUserId, amount)
        return manageTransactionPort.withTransaction {
            val savedInitialized = sendMoneyRepository.save(initialized)
            val fromWallet = walletRepository.findByUserId(fromUserId)
                ?: throw RuntimeException("NotFound Wallet")
            val toWallet = walletRepository.findByUserId(toUserId)
                ?: throw RuntimeException("NotFound Wallet")
            fromWallet.withdraw(amount)
            walletRepository.save(fromWallet)
            toWallet.deposit(amount)
            walletRepository.save(toWallet)
            val success = savedInitialized.success()
            sendMoneyRepository.save(success)
        }
    }

    private fun validateUserStatus(fromUserId: Long, toUserId: Long) {
        userRepository.findById(fromUserId)
            .also { it.validateBlockStatus() }
        userRepository.findById(toUserId)
            .also { it.validateBlockStatus() }
    }

    private fun validateDailyTotalSendMoneyAmount(userId: Long, amount: Long) {
        val totalAmount = sendMoneyRepository.getDailyTotalSendMoneyAmount(userId) + amount
        // 천만원 초과 시
        if (totalAmount > 10000000L) {
            throw RuntimeException("Daily Total Send Money Amount Exceeded")
        }
        sendMoneyRepository.setDailyTotalSendMoneyAmount(userId, totalAmount)
    }
}