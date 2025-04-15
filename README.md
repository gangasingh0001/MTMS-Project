# MTMS Project

**MTMS (Movie Ticket Management System)** is a Java-based distributed system designed for managing movie ticket bookings. It has been extended in **Assignment-3 (Web Services)** to support advanced distributed features including replicas, sequencing, and fault-tolerant communication protocols.

## 🔧 Project Overview

The project has evolved to support a highly available and resilient architecture through the use of:

- **Three replicas**: Independent instances of the MTMS server running in parallel.
- **Sequencer-based message ordering**: Ensures consistency across replicas by sequencing requests.
- **UDP Communication**: Utilizes both unicast and multicast messaging to communicate efficiently across the distributed system.
- **Fault Tolerance & Bug Detection**:
  - Detects bugs or inconsistencies automatically using **majority answer checking** among replicas.
  - Automatically **restarts replicas** upon detection of failure or inconsistent behavior.
- **Data Synchronization**:
  - Implements a **UDP-based server-to-server sync mechanism** to ensure consistency after recovery.

## 🏗️ System Architecture

- **Client** sends a request.
- **Sequencer** receives the request and assigns a sequence number.
- **Multicast** is used to send the ordered request to all replicas.
- **Each Replica** processes the request independently and returns a response.
- **Front End** performs **majority voting** on responses.
- In case of a mismatch, the buggy replica is restarted and resynchronized via UDP.

## 📦 Technologies Used

- Java
- Maven
- UDP Sockets
- Multithreading
- Log4j
- Custom Health Check & Restart logic

## 📁 Project Structure

