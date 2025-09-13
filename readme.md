# OneTap 👆➀ Photo

**OneTap** is a **secure file-sharing application that allows users to send one-time viewable documents, photos, videos, audios, and PDFs**. This application aims to solve **the problem of privacy and security in digital file sharing, where recipients often keep permanent copies or take screenshots of sensitive files**.

---

## ✨ Features

- 🔐 **One-Time File Sharing:** Share files that can be opened only once before expiring.
- 📵 **Screenshot Protection:** Prevent recipients from capturing or saving sensitive content (where supported).
- 🛡️ **End-to-End Encryption:** Files are encrypted before storage and decrypted only at the recipient’s end.
- 📦 **Scalable Architecture:** Built with **Docker** for easy deployment and scaling.
- ⏳ **Automatic Expiry:** Files self-destruct after first access or after a set time.
- 🌍 **Cross-Platform Sharing:** Works across devices and platforms, not tied to WhatsApp or any single app.

---

## 🛠️ Tech Stack

| Layer        | Technology |
|--------------|------------|
| **Backend**  | Spring Boot (Java) |
| **Database** | PostgreSQL |
| **Cache**    | Redis (temporary file storage) |
| **Security** | AES-256 Encryption |
| **Infra**    | Docker |

---

## <caption> Getting Started

Follow these instructions to get a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

* Java Development Kit (JDK) [specify version, 21]
* Apache Maven
* Spring Boot
* PostgreSQL
* Redis
* AES-GCM Encryption 
* Docker (Optional, for containerized deployment)

### Installation & Local Setup

1.  **Clone the repository:**
    ```sh
    git clone [https://github.com/Encryption-prince/OneTap.git](https://github.com/Encryption-prince/OneTap.git)
    cd OneTap
    ```
2. **Create a .env file in the root directory and add the following environment variables:**
   ```
   SPRING_DATASOURCE_URL=jdbc:postgresql://[ur-host-db]:5432/your_database
   DB_USER=your_username
   DB_PASSWORD=your_password
   ENCRYPTION_KEY=your_32_character_key_here
   ```
   *(Replace `your_database`, `your_username`, `your_password`, and `your_32_character_key_here` with your actual PostgreSQL credentials and a secure 32-character encryption key.)*

3. **Build the project using Maven:**
    ```sh
    mvn clean install
    ```

4. **Run the application:**
    * **Directly via Java:**
        ```sh
        java -jar target/onetap-[version].jar 
        ```
      *(Update `onetap-[version].jar` with the actual name of the generated JAR file in your `target` directory.)*

    * **Using Docker (Cloud Redis):**
      This method builds the image from the Dockerfile and runs with cloud-hosted Redis.
        ```sh
        # Build the image
        docker build -t onetap:latest .
        
        # Run with environment variables
        docker run -p 8080:8080 \
          -e SPRING_REDIS_HOST=your-redis-host \
          -e SPRING_REDIS_PASSWORD=your-redis-password \
          -e SPRING_DATASOURCE_URL=your-db-url \
          -e DB_USER=your-db-user \
          -e DB_PASSWORD=your-db-password \
          -e ENCRYPTION_KEY=your-encryption-key \
          onetap:latest
        ```

## Usage : Visit `http://localhost:8080/swagger-ui/index.html` 
### Here u will find the endpoints
## 🤝 Contributing

Contributions are welcome! If you'd like to contribute, please follow these steps:

1.  Fork the Project.
2.  Create your Feature Branch (`git checkout -b feature/AmazingFeature`).
3.  Commit your Changes (`git commit -m 'Add some AmazingFeature'`).
4.  Push to the Branch (`git push origin feature/AmazingFeature`).
5.  Open a Pull Request.

**Project Author:** Encryption-prince ([https://github.com/Encryption-prince](https://github.com/Encryption-prince))
