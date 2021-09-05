package roomreservation;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="Infomation_table")
public class Infomation {

        @Id
        @GeneratedValue(strategy=GenerationType.AUTO)
        private Long id;
        private Long orderId;
        private String orderStatus;
        private String payStatus;
        private String reservationStatus;


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
        public String getOrderStatus() {
            return orderStatus;
        }

        public void setOrderStatus(String orderStatus) {
            this.orderStatus = orderStatus;
        }
        public String getPayStatus() {
            return payStatus;
        }

        public void setPayStatus(String payStatus) {
            this.payStatus = payStatus;
        }
        public String getReservationStatus() {
            return reservationStatus;
        }

        public void setReservationStatus(String reservationStatus) {
            this.reservationStatus = reservationStatus;
        }

}
