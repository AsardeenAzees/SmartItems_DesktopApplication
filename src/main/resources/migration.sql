-- Migration script to add missing columns to products table
-- Run this script if you get "Unknown column" errors

USE smartitemsdb;

-- Add image_path column if it doesn't exist
ALTER TABLE products ADD COLUMN IF NOT EXISTS image_path VARCHAR(500) AFTER description;

-- Add brand column if it doesn't exist
ALTER TABLE products ADD COLUMN IF NOT EXISTS brand VARCHAR(100) AFTER category;

-- Verify the columns were added
DESCRIBE products;
