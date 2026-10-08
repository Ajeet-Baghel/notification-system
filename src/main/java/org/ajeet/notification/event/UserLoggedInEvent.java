package org.ajeet.notification.event;

import java.time.Instant;

/**
 * Published after a successful login so the user gets a security alert.
 */
public record UserLoggedInEvent(String name, String email, String phone, Instant at) {
}
