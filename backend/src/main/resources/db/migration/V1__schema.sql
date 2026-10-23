-- Ajmer SkillLink normalized MySQL 8 schema. All user and provider seed records are fictional.
CREATE TABLE users (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  full_name VARCHAR(100) NOT NULL,
  email VARCHAR(190) NOT NULL,
  password_hash VARCHAR(100) NOT NULL,
  role VARCHAR(20) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  UNIQUE KEY uk_users_email (email), KEY idx_users_role_status (role,status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE customer_profiles (
  user_id VARCHAR(36) NOT NULL PRIMARY KEY,
  preferred_language VARCHAR(20) NOT NULL DEFAULT 'English',
  profile_complete TINYINT(1) NOT NULL DEFAULT 1,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_customer_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE service_categories (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  name VARCHAR(80) NOT NULL,
  slug VARCHAR(90) NOT NULL,
  description VARCHAR(300),
  active TINYINT(1) NOT NULL DEFAULT 1,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  UNIQUE KEY uk_category_slug (slug), KEY idx_category_active (active,name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE provider_profiles (
  user_id VARCHAR(36) NOT NULL PRIMARY KEY,
  primary_category_id VARCHAR(36),
  provider_code VARCHAR(24) NOT NULL,
  primary_area VARCHAR(80) NOT NULL,
  experience_years INT NOT NULL DEFAULT 0,
  skills_json JSON,
  about_text VARCHAR(1200),
  verification_status VARCHAR(24) NOT NULL DEFAULT 'NOT_SUBMITTED',
  rating_average FLOAT(53)NOT NULL DEFAULT 0,
  completed_jobs INT NOT NULL DEFAULT 0,
  trust_score INT NOT NULL DEFAULT 0,
  on_time_rate FLOAT(53)NOT NULL DEFAULT 0,
  cancellation_rate FLOAT(53)NOT NULL DEFAULT 0,
  emergency_available TINYINT(1) NOT NULL DEFAULT 0,
  female_professional TINYINT(1) NOT NULL DEFAULT 0,
  available_today TINYINT(1) NOT NULL DEFAULT 1,
  active TINYINT(1) NOT NULL DEFAULT 1,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  UNIQUE KEY uk_provider_code (provider_code), KEY idx_provider_area (primary_area), KEY idx_provider_verification (verification_status),
  CONSTRAINT fk_provider_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_provider_category FOREIGN KEY (primary_category_id) REFERENCES service_categories(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE provider_services (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  provider_id VARCHAR(36) NOT NULL,
  category_id VARCHAR(36) NOT NULL,
  skill_name VARCHAR(120) NOT NULL,
  active TINYINT(1) NOT NULL DEFAULT 1,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  KEY idx_provider_services_provider (provider_id), KEY idx_provider_services_category (category_id),
  CONSTRAINT fk_provider_services_provider FOREIGN KEY (provider_id) REFERENCES provider_profiles(user_id) ON DELETE CASCADE,
  CONSTRAINT fk_provider_services_category FOREIGN KEY (category_id) REFERENCES service_categories(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE service_areas (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  provider_id VARCHAR(36) NOT NULL,
  area_name VARCHAR(80) NOT NULL,
  is_primary TINYINT(1) NOT NULL DEFAULT 0,
  CONSTRAINT fk_service_areas_provider FOREIGN KEY (provider_id) REFERENCES provider_profiles(user_id) ON DELETE CASCADE,
  KEY idx_service_areas_name (area_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE provider_availability (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  provider_id VARCHAR(36) NOT NULL,
  day_of_week TINYINT NOT NULL,
  start_time TIME NOT NULL,
  end_time TIME NOT NULL,
  emergency_available TINYINT(1) NOT NULL DEFAULT 0,
  CONSTRAINT fk_availability_provider FOREIGN KEY (provider_id) REFERENCES provider_profiles(user_id) ON DELETE CASCADE,
  KEY idx_availability_provider_day (provider_id,day_of_week)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE addresses (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  user_id VARCHAR(36) NOT NULL,
  label VARCHAR(60) NOT NULL,
  area_name VARCHAR(80) NOT NULL,
  address_line VARCHAR(220) NOT NULL,
  is_default TINYINT(1) NOT NULL DEFAULT 0,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_addresses_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  KEY idx_addresses_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE fair_quote_rules (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  category_id VARCHAR(36) NOT NULL,
  area_name VARCHAR(80) NOT NULL DEFAULT 'Ajmer - all areas',
  estimated_min INT NOT NULL,
  estimated_max INT NOT NULL,
  visit_fee_min INT NOT NULL DEFAULT 0,
  visit_fee_max INT NOT NULL DEFAULT 0,
  emergency_surcharge_min INT NOT NULL DEFAULT 0,
  emergency_surcharge_max INT NOT NULL DEFAULT 0,
  active TINYINT(1) NOT NULL DEFAULT 1,
  updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  UNIQUE KEY uk_fair_quote_category_area (category_id,area_name),
  CONSTRAINT fk_quote_category FOREIGN KEY (category_id) REFERENCES service_categories(id),
  KEY idx_quote_category_area (category_id,area_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE bookings (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  booking_code VARCHAR(24) NOT NULL,
  customer_id VARCHAR(36) NOT NULL,
  provider_id VARCHAR(36),
  category_id VARCHAR(36) NOT NULL,
  service_title VARCHAR(140) NOT NULL,
  problem_description VARCHAR(1500) NOT NULL,
  area_name VARCHAR(80) NOT NULL,
  address_label VARCHAR(220) NOT NULL,
  appointment_date DATE,
  preferred_time VARCHAR(40) NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'REQUESTED',
  is_emergency TINYINT(1) NOT NULL DEFAULT 0,
  estimated_min INT,
  estimated_max INT,
  verified_provider_only TINYINT(1) NOT NULL DEFAULT 1,
  daytime_preferred TINYINT(1) NOT NULL DEFAULT 1,
  female_professional_preferred TINYINT(1) NOT NULL DEFAULT 0,
  trusted_contact_name VARCHAR(100),
  share_with_contact TINYINT(1) NOT NULL DEFAULT 0,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  UNIQUE KEY uk_booking_code (booking_code),
  KEY idx_booking_customer_created (customer_id,created_at),
  KEY idx_booking_provider_status (provider_id,status), KEY idx_booking_status (status),
  CONSTRAINT fk_booking_customer FOREIGN KEY (customer_id) REFERENCES users(id),
  CONSTRAINT fk_booking_provider FOREIGN KEY (provider_id) REFERENCES users(id) ON DELETE SET NULL,
  CONSTRAINT fk_booking_category FOREIGN KEY (category_id) REFERENCES service_categories(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE booking_status_history (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  booking_id VARCHAR(36) NOT NULL,
  changed_by VARCHAR(36) NOT NULL,
  status_value VARCHAR(32) NOT NULL,
  note VARCHAR(500),
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_history_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
  CONSTRAINT fk_history_user FOREIGN KEY (changed_by) REFERENCES users(id),
  KEY idx_booking_history_booking_time (booking_id,created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE attachments (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  booking_id VARCHAR(36) NOT NULL,
  uploaded_by VARCHAR(36) NOT NULL,
  phase VARCHAR(12) NOT NULL,
  file_name VARCHAR(500) NOT NULL,
  demo_url VARCHAR(1000) NOT NULL,
  captured_at TIMESTAMP(6) NOT NULL,
  consent_confirmed TINYINT(1) NOT NULL,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_attachment_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
  CONSTRAINT fk_attachment_user FOREIGN KEY (uploaded_by) REFERENCES users(id),
  KEY idx_attachment_booking_phase (booking_id,phase)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE ratings_reviews (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  booking_id VARCHAR(36) NOT NULL,
  customer_id VARCHAR(36) NOT NULL,
  provider_id VARCHAR(36) NOT NULL,
  rating INT NOT NULL,
  review_text VARCHAR(1200),
  reported TINYINT(1) NOT NULL DEFAULT 0,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  UNIQUE KEY uk_review_booking (booking_id), KEY idx_review_provider_created (provider_id,created_at),
  CONSTRAINT fk_review_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
  CONSTRAINT fk_review_customer FOREIGN KEY (customer_id) REFERENCES users(id),
  CONSTRAINT fk_review_provider FOREIGN KEY (provider_id) REFERENCES users(id),
  CONSTRAINT chk_review_rating CHECK (rating BETWEEN 1 AND 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE complaints (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  booking_id VARCHAR(36),
  customer_id VARCHAR(36) NOT NULL,
  provider_id VARCHAR(36),
  category VARCHAR(80) NOT NULL,
  description VARCHAR(1200) NOT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
  priority VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
  resolution_note VARCHAR(1200),
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_complaint_booking FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE SET NULL,
  CONSTRAINT fk_complaint_customer FOREIGN KEY (customer_id) REFERENCES users(id),
  CONSTRAINT fk_complaint_provider FOREIGN KEY (provider_id) REFERENCES users(id) ON DELETE SET NULL,
  KEY idx_complaints_status_created (status,created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE trusted_contacts (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  customer_id VARCHAR(36) NOT NULL,
  display_name VARCHAR(100) NOT NULL,
  contact_hint VARCHAR(120),
  consent_confirmed TINYINT(1) NOT NULL DEFAULT 0,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_trusted_customer FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE notifications (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  user_id VARCHAR(36) NOT NULL,
  title VARCHAR(160) NOT NULL,
  body VARCHAR(600) NOT NULL,
  kind VARCHAR(30) NOT NULL,
  read_at TIMESTAMP(6),
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  KEY idx_notifications_user_created (user_id,created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE refresh_tokens (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  user_id VARCHAR(36) NOT NULL,
  token_hash VARCHAR(64) NOT NULL,
  expires_at TIMESTAMP(6) NOT NULL,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  UNIQUE KEY uk_refresh_token_hash (token_hash), KEY idx_refresh_user (user_id),
  CONSTRAINT fk_refresh_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE audit_logs (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  actor_id VARCHAR(36),
  action_key VARCHAR(80) NOT NULL,
  target_type VARCHAR(80),
  target_id VARCHAR(80),
  details VARCHAR(1200),
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_audit_actor FOREIGN KEY (actor_id) REFERENCES users(id) ON DELETE SET NULL,
  KEY idx_audit_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE admin_announcements (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  title VARCHAR(160) NOT NULL,
  body VARCHAR(1200) NOT NULL,
  audience VARCHAR(24) NOT NULL DEFAULT 'ALL',
  published TINYINT(1) NOT NULL DEFAULT 0,
  starts_at TIMESTAMP(6),
  ends_at TIMESTAMP(6),
  created_by VARCHAR(36),
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  CONSTRAINT fk_announcement_creator FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
