ALTER TABLE provider_profiles
    ADD COLUMN languages VARCHAR(255) NULL,
    ADD COLUMN on_time_sample_count INT NOT NULL DEFAULT 0;