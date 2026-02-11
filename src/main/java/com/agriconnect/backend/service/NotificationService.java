package com.agriconnect.backend.service;

import com.agriconnect.backend.model.Order;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String fromEmail;

    /**
     * Notify farmer about new order
     */
    public void notifyFarmerNewOrder(Order order) {
        String farmerEmail = order.getFarmer().getEmail();
        String consumerName = order.getConsumer().getName() + " " + order.getConsumer().getSurname();

        String subject = "🔔 New Order Received - Order #" + order.getId();

        StringBuilder body = new StringBuilder();
        body.append("<html><body style='font-family: Arial, sans-serif;'>");
        body.append("<div style='max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f8f9fa;'>");
        body.append("<div style='background-color: white; padding: 30px; border-radius: 10px;'>");

        body.append("<h2 style='color: #27ae60; margin-top: 0;'>🎉 New Order Received!</h2>");
        body.append("<p>Hello ").append(order.getFarmer().getName()).append(",</p>");
        body.append("<p>You have received a new order from <strong>").append(consumerName).append("</strong>.</p>");

        body.append("<div style='background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin: 20px 0;'>");
        body.append("<h3 style='margin-top: 0; color: #2c3e50;'>Order Details</h3>");
        body.append("<p><strong>Order ID:</strong> #").append(order.getId()).append("</p>");
        body.append("<p><strong>Total Amount:</strong> R").append(String.format("%.2f", order.getGrandTotal())).append("</p>");
        body.append("<p><strong>Payment Method:</strong> ").append(order.getPaymentMethod()).append("</p>");

        body.append("<h4>Items:</h4><ul>");
        order.getItems().forEach(item -> {
            body.append("<li>").append(item.getProductName())
                    .append(" × ").append(item.getQuantity())
                    .append(" - R").append(String.format("%.2f", item.getSubtotal()))
                    .append("</li>");
        });
        body.append("</ul>");

        body.append("<h4>Delivery Information:</h4>");
        body.append("<p><strong>Address:</strong> ").append(order.getDeliveryAddress()).append("</p>");
        body.append("<p><strong>Contact:</strong> ").append(order.getContactNumber()).append("</p>");
        if (order.getDeliveryNotes() != null && !order.getDeliveryNotes().isEmpty()) {
            body.append("<p><strong>Notes:</strong> ").append(order.getDeliveryNotes()).append("</p>");
        }
        body.append("</div>");

        body.append("<p style='margin-top: 20px;'>Please log in to your dashboard to confirm or reject this order.</p>");
        body.append("<a href='http://localhost:3000/farmer/orders' style='display: inline-block; padding: 12px 30px; background-color: #27ae60; color: white; text-decoration: none; border-radius: 5px; margin-top: 10px;'>View Order</a>");

        body.append("<p style='margin-top: 30px; color: #7f8c8d; font-size: 12px;'>This is an automated message from AgriConnect. Please do not reply to this email.</p>");
        body.append("</div></div></body></html>");

        sendEmail(farmerEmail, subject, body.toString());
       // sendSMS(order.getFarmer().get, "New order #" + order.getId() + " from " + consumerName + ". Total: R" + String.format("%.2f", order.getGrandTotal()));
    }

    /**
     * Notify consumer that order was placed
     */
    public void notifyConsumerOrderPlaced(Order order) {
        String consumerEmail = order.getConsumer().getEmail();
        String farmerName = order.getFarmer().getName() + " " + order.getFarmer().getSurname();

        String subject = "✅ Order Confirmed - Order #" + order.getId();

        StringBuilder body = new StringBuilder();
        body.append("<html><body style='font-family: Arial, sans-serif;'>");
        body.append("<div style='max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f8f9fa;'>");
        body.append("<div style='background-color: white; padding: 30px; border-radius: 10px;'>");

        body.append("<h2 style='color: #27ae60; margin-top: 0;'>✅ Order Placed Successfully!</h2>");
        body.append("<p>Hello ").append(order.getConsumer().getName()).append(",</p>");
        body.append("<p>Your order from <strong>").append(farmerName).append("</strong> has been placed successfully.</p>");

        body.append("<div style='background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin: 20px 0;'>");
        body.append("<h3 style='margin-top: 0; color: #2c3e50;'>Order Summary</h3>");
        body.append("<p><strong>Order ID:</strong> #").append(order.getId()).append("</p>");
        body.append("<p><strong>Status:</strong> <span style='color: #f39c12;'>").append(order.getStatus()).append("</span></p>");
        body.append("<p><strong>Total:</strong> R").append(String.format("%.2f", order.getGrandTotal())).append("</p>");

        body.append("<h4>Items:</h4><ul>");
        order.getItems().forEach(item -> {
            body.append("<li>").append(item.getProductName())
                    .append(" × ").append(item.getQuantity())
                    .append("</li>");
        });
        body.append("</ul>");
        body.append("</div>");

        body.append("<p>The farmer will review your order shortly. You'll receive another email once they confirm.</p>");
        body.append("<a href='http://localhost:3000/consumer/orders' style='display: inline-block; padding: 12px 30px; background-color: #3498db; color: white; text-decoration: none; border-radius: 5px; margin-top: 10px;'>Track Order</a>");

        body.append("<p style='margin-top: 30px; color: #7f8c8d; font-size: 12px;'>Thank you for using AgriConnect!</p>");
        body.append("</div></div></body></html>");

        sendEmail(consumerEmail, subject, body.toString());
       // sendSMS(order.getConsumer().getPhone(), "Order #" + order.getId() + " placed successfully! Farmer: " + farmerName);
    }

    /**
     * Notify consumer that order was confirmed
     */
    public void notifyConsumerOrderConfirmed(Order order) {
        String consumerEmail = order.getConsumer().getEmail();
        String farmerName = order.getFarmer().getName() + " " + order.getFarmer().getSurname();

        String subject = "🎉 Order Confirmed by Farmer - Order #" + order.getId();

        StringBuilder body = new StringBuilder();
        body.append("<html><body style='font-family: Arial, sans-serif;'>");
        body.append("<div style='max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f8f9fa;'>");
        body.append("<div style='background-color: white; padding: 30px; border-radius: 10px;'>");

        body.append("<h2 style='color: #27ae60; margin-top: 0;'>🎉 Your Order Has Been Confirmed!</h2>");
        body.append("<p>Hello ").append(order.getConsumer().getName()).append(",</p>");
        body.append("<p>Great news! <strong>").append(farmerName).append("</strong> has confirmed your order.</p>");

        body.append("<div style='background-color: #d4edda; padding: 15px; border-radius: 5px; margin: 20px 0; border-left: 4px solid #27ae60;'>");
        body.append("<p style='margin: 0;'><strong>Order #").append(order.getId()).append("</strong> is now being prepared for delivery.</p>");
        body.append("</div>");

        body.append("<p>You'll receive another notification when your order is out for delivery.</p>");
        body.append("<a href='http://localhost:3000/consumer/orders' style='display: inline-block; padding: 12px 30px; background-color: #27ae60; color: white; text-decoration: none; border-radius: 5px; margin-top: 10px;'>Track Order</a>");

        body.append("<p style='margin-top: 30px; color: #7f8c8d; font-size: 12px;'>Thank you for supporting local farmers!</p>");
        body.append("</div></div></body></html>");

        sendEmail(consumerEmail, subject, body.toString());
        //sendSMS(order.getConsumer().getPhone(), farmerName + " confirmed your order #" + order.getId() + "!");
    }

    /**
     * Notify consumer that order was delivered
     */
    public void notifyConsumerOrderDelivered(Order order) {
        String consumerEmail = order.getConsumer().getEmail();

        String subject = "📦 Order Delivered - Order #" + order.getId();

        StringBuilder body = new StringBuilder();
        body.append("<html><body style='font-family: Arial, sans-serif;'>");
        body.append("<div style='max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f8f9fa;'>");
        body.append("<div style='background-color: white; padding: 30px; border-radius: 10px;'>");

        body.append("<h2 style='color: #27ae60; margin-top: 0;'>📦 Order Delivered!</h2>");
        body.append("<p>Hello ").append(order.getConsumer().getName()).append(",</p>");
        body.append("<p>Your order #").append(order.getId()).append(" has been successfully delivered!</p>");

        body.append("<div style='background-color: #d4edda; padding: 15px; border-radius: 5px; margin: 20px 0;'>");
        body.append("<p style='margin: 0;'>We hope you enjoy your fresh produce! 🌱</p>");
        body.append("</div>");

        body.append("<p>Thank you for choosing AgriConnect and supporting local farmers.</p>");
        body.append("<a href='http://localhost:3000/consumer' style='display: inline-block; padding: 12px 30px; background-color: #27ae60; color: white; text-decoration: none; border-radius: 5px; margin-top: 10px;'>Shop Again</a>");

        body.append("<p style='margin-top: 30px; color: #7f8c8d; font-size: 12px;'>Thank you for using AgriConnect!</p>");
        body.append("</div></div></body></html>");

        sendEmail(consumerEmail, subject, body.toString());
       // sendSMS(order.getConsumer().getPhone(), "Order #" + order.getId() + " delivered! Enjoy your fresh produce 🌱");
    }

    /**
     * Notify farmer that order was cancelled
     */
    public void notifyFarmerOrderCancelled(Order order) {
        String farmerEmail = order.getFarmer().getEmail();
        String consumerName = order.getConsumer().getName() + " " + order.getConsumer().getSurname();

        String subject = "❌ Order Cancelled - Order #" + order.getId();

        StringBuilder body = new StringBuilder();
        body.append("<html><body style='font-family: Arial, sans-serif;'>");
        body.append("<div style='max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f8f9fa;'>");
        body.append("<div style='background-color: white; padding: 30px; border-radius: 10px;'>");

        body.append("<h2 style='color: #e74c3c; margin-top: 0;'>❌ Order Cancelled</h2>");
        body.append("<p>Hello ").append(order.getFarmer().getName()).append(",</p>");
        body.append("<p>Order #").append(order.getId()).append(" from <strong>").append(consumerName).append("</strong> has been cancelled.</p>");

        if (order.getCancellationReason() != null && !order.getCancellationReason().isEmpty()) {
            body.append("<div style='background-color: #f8d7da; padding: 15px; border-radius: 5px; margin: 20px 0;'>");
            body.append("<p style='margin: 0;'><strong>Reason:</strong> ").append(order.getCancellationReason()).append("</p>");
            body.append("</div>");
        }

        body.append("<p>No further action is required.</p>");

        body.append("<p style='margin-top: 30px; color: #7f8c8d; font-size: 12px;'>AgriConnect - Connecting Farmers & Consumers</p>");
        body.append("</div></div></body></html>");

        sendEmail(farmerEmail, subject, body.toString());
       // sendSMS(order.getFarmer().getPhone(), "Order #" + order.getId() + " cancelled by " + consumerName);
    }

    /**
     * Send Email
     */
    private void sendEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true = HTML

            mailSender.send(message);

            System.out.println("✅ Email sent to: " + to);
            System.out.println("📧 Subject: " + subject);

        } catch (MessagingException e) {
            System.err.println("❌ Failed to send email to: " + to);
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Send SMS (placeholder for actual SMS service)
     */
    private void sendSMS(String phoneNumber, String message) {
        // TODO: Integrate with SMS provider (Twilio, Africa's Talking, etc.)
        System.out.println("📱 SMS to " + phoneNumber + ": " + message);
    }
}