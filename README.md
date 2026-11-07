MovieMate REST API

Project Overview

The MovieMate REST API is a backend service developed using Node.js and Express.js. It provides communication between the MovieMate Android application and its Firebase Cloud Firestore database through the Firebase Admin SDK.

The API handles movie data retrieval, movie searches, watchlist management and movie rating submissions. It serves as the backend component of the MovieMate application, enabling data to be accessed and managed through structured RESTful endpoints.

Technologies Used

Node.js – JavaScript runtime environment.

Express.js – Backend framework used to develop the REST API.

Firebase Admin SDK – Provides secure server-side access to Firebase services.

Cloud Firestore – NoSQL database used to store movie and user-related information.

JavaScript – Primary programming language used for backend development.

Prerequisites

Before running the API locally, ensure that the following are installed and available:

Node.js version 20 or later.

npm (Node Package Manager).

Access to the MovieMate Firebase project.

A valid Firebase service-account JSON file.

Installation and Setup

Follow these steps to configure and run the MovieMate REST API on a Windows computer.

1. Configure Firebase Credentials

Download the Firebase service-account JSON file for the MovieMate Firebase project.

Place the downloaded file in the root directory of the MovieMateAPI folder and rename it:

serviceAccountKey.json

Important: The service-account file contains sensitive credentials. It must remain private and must never be uploaded to GitHub or shared publicly. The file is excluded through the project's .gitignore configuration.

2. Install Dependencies

Open a terminal in the MovieMateAPI project directory and execute:

npm install

This installs the dependencies required to run the backend service.

3. Configure the Firebase Environment Variable

In Windows PowerShell, execute:

$env:GOOGLE_APPLICATION_CREDENTIALS = (Resolve-Path .\serviceAccountKey.json).Path

This sets the environment variable used by the Firebase Admin SDK to locate the service-account credentials.

4. Start the API Server

Execute the following command:

npm start

The API server will start on port 3000 by default.

5. Verify the API

Open a web browser and navigate to:

http://localhost:3000/health

A successful response confirms that the API server is running and responding to health-check requests.

API Endpoints

The MovieMate REST API provides the following endpoints:

Health Check

GET /health – Checks the availability of the API server.

Movie Management

GET /movies – Retrieves the available movie records.

GET /movies?query=avatar – Searches for movies using a search query.

GET /movies/:id – Retrieves a specific movie using its identifier.

Watchlist Management

GET /api/watchlist?userId=FIREBASE_UID – Retrieves the watchlist associated with a user.

POST /api/watchlist?userId=FIREBASE_UID – Adds a movie to a user's watchlist.

DELETE /api/watchlist/:movieId?userId=FIREBASE_UID – Removes a movie from a user's watchlist.

Movie Ratings

POST /api/ratings – Submits a movie rating.

Application Connectivity

The API listens on port 3000 by default and binds to all available network interfaces. This allows compatible devices, including Android emulators and physical Android devices connected to the same trusted network, to communicate with the backend using the host computer's local IP address.

For production deployment, the API should be hosted using HTTPS, with the Android application's API base URL configured to point to the deployed server.

Security Considerations

The current implementation is intended for a coursework application prototype.

Before deploying the API in a public production environment, additional security measures should be implemented, including:

Firebase Authentication ID-token verification.

Server-side authentication and authorisation for user-specific operations.

Input validation and sanitisation.

Appropriate error handling and request validation.

Secure management of Firebase service-account credentials.

HTTPS encryption for API communication.

Firebase service-account credentials must never be committed to source control.

Project Purpose

The MovieMate REST API forms an important part of the MovieMate Android application by providing a structured backend for retrieving and managing application data.

It demonstrates the integration of a RESTful web service with Firebase Cloud Firestore and supports the application's movie discovery, watchlist and rating functionality.


Release Notes – Version 1.0

This release contains the final implementation of MovieMate following the initial prototype.

The updates include:

Improved user interface and navigation.

User registration and login using Firebase Authentication.

Movie searching and movie detail viewing through the backend API.

Watchlist functionality with Firebase Firestore integration.

User settings and application preferences.

Multiple language support.

Offline functionality.

Improved input handling and error messages.

Backend API integration for retrieving movie information.

This version also includes improvements to the overall application structure, reliability and user experience.
