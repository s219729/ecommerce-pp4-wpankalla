package pl.wpankalla.payu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateResponse {
    Status status;
    String redirectUri;
    String orderId;
    String extOrderId;
}
