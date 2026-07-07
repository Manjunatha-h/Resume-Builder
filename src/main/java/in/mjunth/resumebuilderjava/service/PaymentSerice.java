package in.mjunth.resumebuilderjava.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import in.mjunth.resumebuilderjava.document.Payment;
import in.mjunth.resumebuilderjava.document.User;
import in.mjunth.resumebuilderjava.dto.AuthResponse;
import in.mjunth.resumebuilderjava.repository.PaymentRepositoty;
import in.mjunth.resumebuilderjava.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.json.JsonObject;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static in.mjunth.resumebuilderjava.utils.AppConstants.PREMIUM;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentSerice {

    private final PaymentRepositoty paymentRepositoty;
    private final AuthService authService;
    private final UserRepository userRepository;

    @Value("${razorpay.key.id}")
    private String razorPayKeyId;

    @Value("${razorpay.key.secret}")
    private String getRazorPayKeySecret;

    public Payment createOrder(Object principal, String planType) throws RazorpayException {
        AuthResponse profile = authService.getProfile(principal);

        RazorpayClient razorpayClient = new RazorpayClient(razorPayKeyId,getRazorPayKeySecret);

        int amount = 99900;
        String currency = "INR";
        String receipt = PREMIUM+"_"+UUID.randomUUID().toString().substring(0,8);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("amount",amount);
        jsonObject.put("currency",currency);
        jsonObject.put("receipt",receipt);

        Order order = razorpayClient.orders.create(jsonObject);

        Payment newPayment = Payment.builder()
                .userId(profile.getId())
                .currency(currency)
                .planType(PREMIUM)
                .amount(amount)
                .razorpayOrderId(order.get("id"))
                .status("created")
                .receipt(receipt)
                .build();

        paymentRepositoty.save(newPayment);

        return newPayment;

    }

    public boolean verifyPayment(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) throws RazorpayException {
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("razorpay_order_id", razorpayOrderId);
            jsonObject.put("razorpay_payment_id", razorpayPaymentId);
            jsonObject.put("razorpay_signature", razorpaySignature);

            boolean isVerified = Utils.verifyPaymentSignature(jsonObject, getRazorPayKeySecret);

            if (isVerified) {
                Payment payment = paymentRepositoty.findByRazorpayOrderId(razorpayOrderId).orElseThrow(() -> new RuntimeException("Payment not found"));
                payment.setRazorpayPaymentId(razorpayPaymentId);
                payment.setRazorpaySignature(razorpaySignature);
                payment.setStatus("paid");

                paymentRepositoty.save(payment);
                upgardeUserSubscription(payment.getUserId(), payment.getPlanType());
                return true;
            }
        }catch (Exception e){
            log.error("error while verifying the error ");
            return false;
        }
        return false;
    }

    private void upgardeUserSubscription(String userId, String planType) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("user not found"));
        user.setSubscriptionPlan(PREMIUM);
        userRepository.save(user);
        log.info("user {} upgraded to plan {}",userId,planType);
    }

    public List<Payment> getPaymentHostory(Object principal) {
        AuthResponse profile = authService.getProfile(principal);
        return paymentRepositoty.findByUserIdOrderByCreatedAtDesc(profile.getId());
    }

    public Payment getOrderDeatils(String orderId) {
        Payment payment = paymentRepositoty.findByRazorpayOrderId(orderId).orElseThrow(() -> new RuntimeException("Order Id not valid"));
        return payment;
    }
}
