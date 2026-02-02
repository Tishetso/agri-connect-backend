package com.agriconnect.backend.service;

import com.agriconnect.backend.model.Order;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    /*Notify farmer about new order*/

    public void notifyFarmerNewOrder(Order order){
        String farmerEmail = order.getFarmer().getEmail();
        String consumerName = order.getConsumer().getName() + " " + order.getConsumer().getSurname();

        /*//TODO//implement email sending*/
        System.out.println("=== EMAIL TO FARMER ===");
        System.out.println("To: " + farmerEmail);
        System.out.println("Subject: New Order Received!");
        System.out.println("Body: You have a new order from " + consumerName);
        System.out.println("Order ID: " + order.getId());
        System.out.println("Total: R" + order.getGrandTotal());
        System.out.println("Items: " + order.getItems().size());

        /*//ToDO: implement SMS sending (optional)
        sendSMS(order.getFarmer().getPhone(), "New order #" + order.getId() + " from " + consumerName);
*/
    }

    /*Notify consumer that order was placed*/

    public void notifyConsumerOrderPlace(Order order){
        String consumerEmail = order.getConsumer().getEmail();
        String farmerName = order.getFarmer().getName() + " " + order.getFarmer().getSurname();

        System.out.println("=== EMAIL TO CONSUMER ===");
        System.out.println("To: " + consumerEmail);
        System.out.println("Subject: Order Placed Successfully!");
        System.out.println("Body: Your order from " + farmerName + " has been placed.");
        System.out.println("Order ID " + order.getId());
        System.out.println("Total: R" + order.getGrandTotal());
        System.out.println("Status: " + order.getStatus());

      /*  sendSMS(order.getConsumer().getPhone(), "Order #" + order.getId() + " placed successfully!");*/
    }

    /*Notify consumer that order was confirmed*/

    public void notifyConsumerOrderConfirmed(Order order){
        String consumerEmail = order.getConsumer().getEmail();
        String farmerName = order.getFarmer().getName() + " " + order.getFarmer().getSurname();


        System.out.println("=== EMAIL TO CONSUMER ===");
        System.out.println("To: " + consumerEmail);
        System.out.println("Subject: Order Confirmed!");
        System.out.println("Body: " + farmerName + " has confirmed your order #" + order.getId() );

        /*sendSMS(order.getConsumer().getPhone(), farmerName + " confirmed your order #" + order.getId());*/
    }


    public void notifyConsumerOrderDelivered(Order order){
        String consumerEmail = order.getConsumer().getEmail();

        System.out.println("=== EMAIL TO CONSUMER ===");
        System.out.println("To: " + consumerEmail);
        System.out.println("Subject: Order Delivered!");
        System.out.println("Body: Your order #" + order.getId() + " has been delivered!");
    }

    /*Notify farmer that order was cancelled*/
    public void notifyFarmerOrderCancelled(Order order){
        String farmerEmail = order.getFarmer().getEmail();
        String consumerName = order.getConsumer().getName() + " " + order.getConsumer().getSurname();

        System.out.println("=== EMAIL TO FARMER ===");
        System.out.println("To: " + farmerEmail);
        System.out.println("Subject: Order Cancelled!");
        System.out.println("Body: Order #" + order.getId() + " from " + consumerName + " was cancelled.");
        System.out.println("Reason: " + order.getCancellationReason());

       /* sendSMS(order.getFarmer().getPhone(), "Order #" + order.getId() + " cancelled by " + consumerName);*/
    }

    /*Send SMS (Placeholder for actual SMS service)*/
    private void sendSMS(String phoneNumber, String message){
        //Todo: Integrate with sms provider (Twilio, Africa's Talking
        System.out.println("=== SMS ===");
        System.out.println("To: " + phoneNumber);
        System.out.println("Message: " + message);
        System.out.println("===============");
    }

    private void sendEmail(String to, String subject, String body){

        //Todo: Integrate with email service(sendGrid, Mailgun, JavaMail
        System.out.println("=== EMAIL ===");
        System.out.println("To: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("Body: " + body);
        System.out.println("================");

    }

}
