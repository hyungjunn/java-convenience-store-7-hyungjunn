package store;

import java.math.BigDecimal;
import java.math.BigInteger;

public class PaymentInformation {
    BigDecimal totalAmount;
    BigDecimal eventDiscountAmount;
    BigInteger membershipDiscountAmount;

    public PaymentInformation(BigDecimal totalAmount, BigDecimal eventDiscountAmount, BigInteger membershipDiscountAmount) {
        this.totalAmount = totalAmount;
        this.eventDiscountAmount = eventDiscountAmount;
        this.membershipDiscountAmount = membershipDiscountAmount;
    }

    public BigDecimal calculateFinalAmount() {
        return totalAmount
                .subtract(eventDiscountAmount)
                .subtract(new BigDecimal(membershipDiscountAmount));
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getEventDiscountAmount() {
        return eventDiscountAmount;
    }

    public BigInteger getMembershipDiscountAmount() {
        return membershipDiscountAmount;
    }
}
