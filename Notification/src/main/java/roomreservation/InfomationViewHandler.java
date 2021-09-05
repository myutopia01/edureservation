package roomreservation;

import roomreservation.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class InfomationViewHandler {


    @Autowired
    private InfomationRepository infomationRepository;

    // @StreamListener(KafkaProcessor.INPUT)
    // public void whenOrdered_then_CREATE_1 (@Payload Ordered ordered) {
    //     try {

    //         if (!ordered.validate()) return;

    //         // view 객체 생성
    //         Infomation infomation = new Infomation();
    //         // view 객체에 이벤트의 Value 를 set 함
    //         infomation.setOrderId(ordered.getorderId());
    //         infomation.setOrderStatus(ordered.getStatus());
    //         // view 레파지 토리에 save
    //         infomationRepository.save(infomation);

    //     }catch (Exception e){
    //         e.printStackTrace();
    //     }
    // }


    // @StreamListener(KafkaProcessor.INPUT)
    // public void whenPaid_then_UPDATE_1(@Payload Paid paid) {
    //     try {
    //         if (!paid.validate()) return;
    //             // view 객체 조회

    //                 List<Infomation> infomationList = infomationRepository.findByOrderId(paid.getOrderId());
    //                 for(Infomation infomation : infomationList){
    //                 // view 객체에 이벤트의 eventDirectValue 를 set 함
    //                 infomation.setPayStatus(paid.getStatus());
    //             // view 레파지 토리에 save
    //             infomationRepository.save(infomation);
    //             }

    //     }catch (Exception e){
    //         e.printStackTrace();
    //     }
    // }
    
    @StreamListener(KafkaProcessor.INPUT)
    public void whenOrdered_then_CREATE_1 (@Payload Paid paid) {
        try {

            if (!paid.validate()) return;

            // view 객체 생성
            Infomation infomation = new Infomation();
            // view 객체에 이벤트의 Value 를 set 함
            infomation.setOrderId(paid.getOrderId());
            infomation.setPayStatus(paid.getStatus());
            // view 레파지 토리에 save
            infomationRepository.save(infomation);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whenPaid_then_UPDATE_1(@Payload Ordered ordered) {
        try {
            if (!ordered.validate()) return;
                // view 객체 조회

                    List<Infomation> infomationList = infomationRepository.findByOrderId(ordered.getorderId());
                    for(Infomation infomation : infomationList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    infomation.setOrderStatus(ordered.getStatus());
                // view 레파지 토리에 save
                infomationRepository.save(infomation);
                }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenReserved_then_UPDATE_2(@Payload Reserved reserved) {
        try {
            if (!reserved.validate()) return;
                // view 객체 조회

                    List<Infomation> infomationList = infomationRepository.findByOrderId(reserved.getOrderId());
                    for(Infomation infomation : infomationList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    infomation.setReservationStatus(reserved.getStatus());
                // view 레파지 토리에 save
                infomationRepository.save(infomation);
                }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenOrderCanceled_then_UPDATE_3(@Payload OrderCanceled orderCanceled) {
        try {
            if (!orderCanceled.validate()) return;
                 // view 객체 조회
                
                    List<Infomation> infomationList = infomationRepository.findByOrderId(orderCanceled.getorderId());
                    for(Infomation infomation : infomationList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    infomation.setOrderStatus(orderCanceled.getStatus());
                // view 레파지 토리에 save
                infomationRepository.save(infomation);
                }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenPaymentCanceled_then_UPDATE_4(@Payload PaymentCanceled paymentCanceled) {
        try {
            if (!paymentCanceled.validate()) return;
                // view 객체 조회

                    List<Infomation> infomationList = infomationRepository.findByOrderId(paymentCanceled.getOrderId());
                    for(Infomation infomation : infomationList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    infomation.setPayStatus(paymentCanceled.getStatus());
                // view 레파지 토리에 save
                infomationRepository.save(infomation);
                }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenReservationCanceled_then_UPDATE_5(@Payload ReservationCanceled reservationCanceled) {
        try {
            if (!reservationCanceled.validate()) return;
                // view 객체 조회

                    List<Infomation> infomationList = infomationRepository.findByOrderId(reservationCanceled.getOrderId());
                    for(Infomation infomation : infomationList){
                    // view 객체에 이벤트의 eventDirectValue 를 set 함
                    infomation.setReservationStatus(reservationCanceled.getStatus());
                // view 레파지 토리에 save
                infomationRepository.save(infomation);
                }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

