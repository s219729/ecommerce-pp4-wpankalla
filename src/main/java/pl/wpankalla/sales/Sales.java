package pl.wpankalla.sales;

import pl.wpankalla.sales.cart.Cart;
import pl.wpankalla.sales.cart.CartItem;
import pl.wpankalla.sales.cart.CartStorage;
import pl.wpankalla.sales.offerting.Offer;
import pl.wpankalla.sales.offerting.OfferCalculator;
import pl.wpankalla.sales.payment.PaymentDetails;
import pl.wpankalla.sales.payment.PaymentGateway;
import pl.wpankalla.sales.product.ProductDetails;
import pl.wpankalla.sales.product.ProductDetailsProvider;
import pl.wpankalla.sales.product.ProductNotAvailableException;
import pl.wpankalla.sales.reservation.InMemoryReservationStorage;
import pl.wpankalla.sales.reservation.Reservation;

import java.util.UUID;

public class Sales {
    private CartStorage cartStorage;
    private ProductDetailsProvider productDetailsProvider;

    private PaymentGateway paymentGateway;

    private InMemoryReservationStorage reservationStorage;

    private OfferCalculator offerCalculator;

    public Sales(CartStorage cartStorage, ProductDetailsProvider productDetailsProvider, PaymentGateway paymentGateway, InMemoryReservationStorage reservationStorage, OfferCalculator offerCalculator) {
        this.cartStorage = cartStorage;
        this.productDetailsProvider = productDetailsProvider;
        this.paymentGateway = paymentGateway;
        this.reservationStorage = reservationStorage;
        this.offerCalculator = offerCalculator;
    }

    public Offer getCurrentOffer(String customerId) {
        Cart cart = cartStorage.getBy(customerId)
                .orElse(Cart.getEmptyCart());

        return offerCalculator.calculateOffer(cart);
    }

    public void addToCart(String customerId, String productId) {
        Cart cart = cartStorage.getBy(customerId)
                .orElse(Cart.getEmptyCart());

        ProductDetails details = productDetailsProvider.getById(productId)
                        .orElseThrow(ProductNotAvailableException::new);

        cart.add(CartItem.of(productId,
                details.getName(),
                details.getPrice()));

        cartStorage.save(customerId, cart);
    }

    public PaymentDetails acceptOffer(String customerId, CustomerData customerData) {
        Cart cart = cartStorage.getBy(customerId)
                .orElse(Cart.getEmptyCart());

        Offer currentOffer = offerCalculator.calculateOffer(cart);

        String reservationId = UUID.randomUUID().toString();

        Reservation reservation = Reservation.of(reservationId, currentOffer.getTotal(), customerData);
        reservation.registerPayment(paymentGateway);

        reservationStorage.save(reservation);

        return new PaymentDetails(reservationId, reservation.getPaymentId(), reservation.getPaymentUrl());
    }


}
