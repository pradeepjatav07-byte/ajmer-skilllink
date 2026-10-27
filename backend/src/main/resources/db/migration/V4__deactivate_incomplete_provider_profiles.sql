-- Deactivate active profiles that have not met the publication requirements.
UPDATE provider_profiles
SET active = 0
WHERE active = 1
  AND (
    primary_category_id IS NULL
    OR TRIM(COALESCE(primary_area, '')) = ''
    OR experience_years < 0
    OR skills_json IS NULL
    OR JSON_TYPE(skills_json) <> 'ARRAY'
    OR JSON_LENGTH(skills_json) = 0
  );