package roomreservation;

import roomreservation.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverReserved_Kakaotalk(@Payload Reserved reserved){

        if(!reserved.validate()) return;

        System.out.println("\n\n##### listener Kakaotalk : " + reserved.toJson() + "\n\n");



        // Sample Logic //

    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrdered_Kakaotalk(@Payload Ordered ordered){

        if(!ordered.validate()) return;

        System.out.println("\n\n##### listener Kakaotalk : " + ordered.toJson() + "\n\n");



        // Sample Logic //

    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaid_Kakaotalk(@Payload Paid paid){

        if(!paid.validate()) return;

        System.out.println("\n\n##### listener Kakaotalk : " + paid.toJson() + "\n\n");



        // Sample Logic //

    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaymentCanceled_Kakaotalk(@Payload PaymentCanceled paymentCanceled){

        if(!paymentCanceled.validate()) return;

        System.out.println("\n\n##### listener Kakaotalk : " + paymentCanceled.toJson() + "\n\n");



        // Sample Logic //

    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderCanceled_Kakaotalk(@Payload OrderCanceled orderCanceled){

        if(!orderCanceled.validate()) return;

        System.out.println("\n\n##### listener Kakaotalk : " + orderCanceled.toJson() + "\n\n");



        // Sample Logic //

    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverReservationCanceled_Kakaotalk(@Payload ReservationCanceled reservationCanceled){

        if(!reservationCanceled.validate()) return;

        System.out.println("\n\n##### listener Kakaotalk : " + reservationCanceled.toJson() + "\n\n");



        // Sample Logic //

    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
