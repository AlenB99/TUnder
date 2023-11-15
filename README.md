# TUnder

TUnder is a dynamic web application designed to connect students at the TU Wien, helping them find and form study groups. Inspired by the popular dating app Tinder, TUnder offers a swiping interface for easy browsing of potential study partners and groups.

## Features

- **Swiping Interface**: A user-friendly way to explore potential study matches.
- **Matching System**: Connect with other students based on shared interests and study needs.
- **Match Management**: Keep track of your pending matches, confirmed matches, and superlikes.
- **Chat Functionality**: Engage in one-on-one or group chats with your matches.
- **Profile Customization**:
  - Upload Pictures
  - Add your Name and a Description
  - List your LVAs (Lehrveranstaltungen – courses)
  - Share your Interests and Hobbies
  - Indicate the number of Semesters completed
  - Optionally include Grades
  - And more...

## Getting Started

### Prerequisites

Ensure you have the following installed:
- Java (for Spring Backend)
- Node.js and Angular CLI (for Angular Frontend)

### Running the Backend

1. **Start the backend**:
   ```sh
   mvn spring-boot:run
   ```
2. **Start the backend with test data (Note: If the database is not clean, test data won't be inserted):**:
   ```sh
   mvn spring-boot:run -Dspring-boot.run.profiles=generateData
   ```


### Running the Frontend
  ```sh
  ng serve
  ```

## Technology Stack

- **Backend**: Spring Boot
- **Frontend**: Angular
- **Styling**: Tailwind CSS, Bootstrap
- **Database**: H2 (Embedded database for development)
