# Notification System

Registration and login app that notifies users by **email** and **WhatsApp**
whenever they register or sign in.

- **Backend** — Spring Boot 3.5 (Java 24), Spring Security + JWT, Spring Data JPA, H2
- **Frontend** — React 19 + TypeScript + Vite + Tailwind CSS (`frontend/`)
- **Email** — `JavaMailSender` against a company SMTP relay
- **WhatsApp** — Twilio REST API (works with the free Twilio WhatsApp sandbox)
- **Delivery** — register/login publish Spring events; an `@Async` listener sends
  both notifications off the request thread, so a provider outage never breaks auth.

## Layout

```
src/main/java/org/ajeet
├── NotificationSystemApplication
├── auth/            register + login endpoints and service
├── security/        JWT service/filter, SecurityConfig, UserDetailsService
├── user/            User entity + repository
├── notification/    EmailService, WhatsAppService, async event listener, events
└── common/          GlobalExceptionHandler
frontend/
└── src/             pages (login/register/dashboard), api client, auth context
```

## Run

```bash
# backend (from repo root) — use IntelliJ run config or bundled maven
mvn spring-boot:run            # serves http://localhost:8080

# frontend
cd frontend
npm install
npm run dev                    # serves http://localhost:5173, proxies /api to :8080
```

H2 console: `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:notifications`).

## Configuration (environment variables)

| Variable | Purpose |
|---|---|
| `MAIL_HOST` / `MAIL_PORT` | Company SMTP relay (e.g. `smtp.office365.com:587`, internal relay on `:25`) |
| `MAIL_USERNAME` / `MAIL_PASSWORD` | SMTP credentials |
| `MAIL_FROM` / `MAIL_FROM_NAME` | Sender identity, e.g. `no-reply@yourcompany.com` |
| `WHATSAPP_ENABLED` | `true` to actually send WhatsApp messages via Twilio |
| `TWILIO_ACCOUNT_SID` / `TWILIO_AUTH_TOKEN` | Twilio credentials |
| `TWILIO_WHATSAPP_FROM` | Sender, e.g. `whatsapp:+14155238886` (sandbox) |
| `JWT_SECRET` | HMAC signing secret — set a real one outside local dev |
| `CORS_ALLOWED_ORIGINS` | Comma-separated frontend origins |

Without `MAIL_FROM` / Twilio credentials the app still runs — notifications are
logged to the backend console instead of being delivered.

## Git workflow

Work happens on `dev`; merge to `main` via pull request once reviewed.

## API

| Method | Path | Body |
|---|---|---|
| `POST` | `/api/auth/register` | `{name, email, phone, password}` — phone in E.164 (`+919876543210`) |
| `POST` | `/api/auth/login` | `{email, password}` |
| `GET` | `/api/auth/me` | `Authorization: Bearer <token>` |

Both auth endpoints return `{token, name, email}` and fire email + WhatsApp
notifications asynchronously.
