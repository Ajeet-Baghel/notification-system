package org.ajeet.notification.event;

/**
 * Published after a successful registration so notification channels
 * can react without coupling the auth flow to delivery.
 */
public record UserRegisteredEvent(String name, String email, String phone) {
}
