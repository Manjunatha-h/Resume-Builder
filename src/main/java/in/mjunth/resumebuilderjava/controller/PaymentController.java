package in.mjunth.resumebuilderjava.controller;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.razorpay.RazorpayException;
import in.mjunth.resumebuilderjava.document.Payment;
import in.mjunth.resumebuilderjava.service.PaymentSerice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static in.mjunth.resumebuilderjava.utils.AppConstants.PREMIUM;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentSerice paymentSerice;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String,String> request,
                                         Authentication authentication) throws RazorpayException {
        String planType = request.get("planType");
        if(!PREMIUM.equalsIgnoreCase(planType)){
            return ResponseEntity.badRequest().body(Map.of("message","Invalid Plan Type"));
        }

        Payment payment = paymentSerice.createOrder(authentication.getPrincipal(),planType);

        Map<String, Object> response = Map.of(
                "orderId",payment.getRazorpayOrderId(),
                "amount",payment.getAmount(),
                "currency",payment.getCurrency(),
                "receipt", payment.getReceipt()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String,String> request) throws RazorpayException {

        String razorpayOrderId = request.get("razorpay_order_id");
        String razorpayPaymentId = request.get("razorpay_payment_id");
        String razorpaySignature = request.get("razorpay_signature");

        if(Objects.isNull(razorpayPaymentId) || Objects.isNull(razorpayOrderId) || Objects.isNull(razorpaySignature)){
            return ResponseEntity.badRequest().body(Map.of("message","Missing Required payment parameters"));
        }

        boolean isValid = paymentSerice.verifyPayment(razorpayOrderId,razorpayPaymentId,razorpaySignature);

        if(isValid){

            return ResponseEntity.ok().body(Map.of("message","Payment verified Succesfully",
                                                    "status","success"
                    ));
        }
        return ResponseEntity.badRequest().body(Map.of("message","Payment verification failed"));
    }

    @GetMapping("/history")
    public ResponseEntity<?> getPaymentHistory(Authentication authentication){
        List<Payment> payments = paymentSerice.getPaymentHostory(authentication.getPrincipal());
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderDetails(@PathVariable String orderId){
        if(Objects.isNull(orderId)){
            return ResponseEntity.badRequest().body(Map.of("message","Order id required"));
        }

        Payment payment = paymentSerice.getOrderDeatils(orderId);
        return ResponseEntity.ok(payment);
    }
}
