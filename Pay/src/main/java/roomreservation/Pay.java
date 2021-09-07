package roomreservation;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Pay_table")
public class Pay {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long orderId;
    private Long customerId;
    private Long cardNo;
    private String status;
    private Long roomNo;
    @PrePersist
    public void onPrePersist(){
        Paid paid = new Paid();
        BeanUtils.copyProperties(this, paid);
        // System.out.println("######################################################");
        // System.out.println("######################################################");
        // System.out.println("#############"+paid.getStatus()+"##########");
        // System.out.println("######################################################");
        // System.out.println("######################################################");
        paid.setStatus("Paid");
        paid.publish();
        try{
            Thread.currentThread().sleep((long)(400+Math.random()*220));
        } catch (InterruptedException e){
            e.printStackTrace();
        }

    }
    @PostUpdate
    public void onPostUpdate(){
        PaymentCanceled paymentCanceled = new PaymentCanceled();
        BeanUtils.copyProperties(this, paymentCanceled);
        paymentCanceled.publishAfterCommit();

    }
    public Long getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(Long roomNo) {
        this.roomNo = roomNo;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    public Long getCardNo() {
        return cardNo;
    }

    public void setCardNo(Long cardNo) {
        this.cardNo = cardNo;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }




}