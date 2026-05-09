# AI Policy Drafting Assistant - Backend

## 🚀 Project Overview
Spring Boot backend for Policy Management System with performance optimizations, caching readiness, and scalable architecture.

---

## ⚡ Performance Optimizations

### 1. Database Indexing
- Added indexes on:
  - due_date
  - is_deleted
  - category
  - title

### 2. Composite Indexing
- is_deleted + due_date combined index for faster filtering

### 3. Full Text Search Optimization
- GIN index on:
  - title + description

---

## 🔥 Query Optimizations

### 4. N+1 Problem Fixed
- Used JOIN FETCH to load audit logs efficiently
- Eliminated multiple queries per entity

### 5. Pagination Optimization
- Replaced findAll() with:
  - findByDeletedFalse(Pageable pageable)
- Reduced unnecessary data loading

---

## 📊 Performance Improvements

- Dashboard query optimized using COUNT queries
- Scheduler queries optimized using indexed columns
- Reduced execution time from ~145ms → ~1–3ms in indexed queries

---

## 🧠 Tech Stack
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Hibernate
- Redis (configured)
- Maven

---

## 📌 Key Features
- Policy CRUD operations
- Soft delete support
- Audit logging system
- Pagination + sorting
- Search API
- Scheduled jobs
- Performance optimized queries

---

## 🧪 Testing APIs
Use Postman:
- /api/policies/list
- /api/policies/optimized
- /api/policies/stats