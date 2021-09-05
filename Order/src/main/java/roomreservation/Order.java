package roomreservation;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Order_table")
public class Order {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long orderId;
    private Long customerId;
    private Long roomNo;
    private Long cardNo;
    private Integer guest;
    private String status;

    @PostPersist
    public void onPostPersist(){
        Ordered ordered = new Ordered();
        BeanUtils.copyProperties(this, ordered);
        ordered.setStatus("Ordered");
        ordered.publishAfterCommit();
        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        roomreservation.external.Pay pay = new roomreservation.external.Pay();
        pay.setCardNo(this.cardNo);
        pay.setCustomerId(this.customerId);
        pay.setOrderId(this.orderId);
        pay.setStatus("Pay Request");
        pay.setRoomNo(this.roomNo);

        // mappings goes here
        OrderApplication.applicationContext.getBean(roomreservation.external.PayService.class)
            .payment(pay);
    }
    
    @PostUpdate
    public void onPostUpdate(){
        OrderCanceled orderCanceled = new OrderCanceled();
        BeanUtils.copyProperties(this, orderCanceled);
        orderCanceled.setStatus("Order Canceled");
        orderCanceled.publishAfterCommit();

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getorderId() {
        return orderId;
    }
    public void setorderId(Long orderId) {
        this.orderId = orderId;
    }
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    public Long getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(Long roomNo) {
        this.roomNo = roomNo;
    }
    public Long getCardNo() {
        return cardNo;
    }

    public void setCardNo(Long cardNo) {
        this.cardNo = cardNo;
    }
    public Integer getGuest() {
        return guest;
    }

    public void setGuest(Integer guest) {
        this.guest = guest;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }




}