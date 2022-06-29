package pl.wpankalla.credit;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class CreditCardTest {
    @Test
    void itAllowsAssignCreditLimit() {
        CreditCard card = new CreditCard();

        card.assignLimit(BigDecimal.valueOf(1000));

        assertEquals(BigDecimal.valueOf(1000), card.getBalance());
    }
}
