# App Name : EduStack

## Roles
- Student
- Teacher
- Admin (future-proof)

## Core Requirements
Auth (JWT-based)
Offline-first data
Modular features
Security hardening
CI/CD
Observability

## Non-Functional Requirements
Scales to 100k users
Feature toggles per role
Build variants (School A / School B)
Works on low-end devices

## Core Modules
Module Name ->    What It Proves to Recruiters
- Auth ->    Security, token handling, role-based flows
- Core ->    Networking, logging, design system
- Quiz Engine -> MVI, Compose performance, state machines
- IT Practicals -> Offline sync, WorkManager, file handling
- App Help -> Dynamic feature, content delivery
- Admin/Teacher -> Feature toggles, analytics
- Security -> Root detection, tamper checks, SSL pinning


:app - Navigation, theme
:auth - UI and ViewModels
:core-api - All interfaces
:core-common - Result, utilities
:core-database - Room setup
:core-impl - DI binding / implementation 
:core-firebase - New module
:quiz - UI and ViewModels
:practical - All code
:help - All code
:security - All code
:designsystem - All code
