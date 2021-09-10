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

        String location = System.getenv("LOCATION");
        if(location == null) location = "LOCAL";
        System.out.println("######################################################################################");
        System.out.println("#################################ConfigMap############################################");
        System.out.println("######################################################################################");
        System.out.println("######################################################################################");
        System.out.println("########################CURRENT_LOCATION = "+location+"###############################");
        System.out.println("######################################################################################");
        System.out.println("######################################################################################");
        System.out.println("#################################ConfigMap############################################");
        System.out.println("######################################################################################");
        Ordered ordered = new Ordered();
        BeanUtils.copyProperties(this, ordered);
        ordered.setStatus("Ordered");
        ordered.publishAfterCommit();
        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.
        System.out.println("######################################11111111#########################################");
        roomreservation.external.Pay pay = new roomreservation.external.Pay();
        System.out.println("######################################22222222#########################################");
        pay.setCardNo(this.cardNo);
        pay.setCustomerId(this.customerId);
        pay.setOrderId(this.orderId);
        pay.setStatus("Pay Request");
        pay.setRoomNo(this.roomNo);
        System.out.println("######################################33333333#########################################");
        // mappings goes here
        OrderApplication.applicationContext.getBean(roomreservation.external.PayService.class)
            .payment(pay);
            System.out.println("######################################444444444#########################################");
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