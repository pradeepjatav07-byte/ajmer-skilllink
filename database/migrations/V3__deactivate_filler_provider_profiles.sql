-- Keep Aarav (ASL-P01) active; retain filler profiles and all related history.
UPDATE provider_profiles
SET active = 0
WHERE provider_code BETWEEN 'ASL-P02' AND 'ASL-P25'
  AND active = 1;