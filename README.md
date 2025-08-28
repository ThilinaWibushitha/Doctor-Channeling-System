# Doctor-Channeling-System
# Doctor Channelling System

A comprehensive Java Swing application for managing doctor appointments, patient records, and medical scheduling.

## Features

### 🏥 Doctor Management
- Register new doctors with detailed information
- View all registered doctors
- Search doctors by name or specialization
- Update doctor information
- Delete doctor records
- Manage doctor availability and time slots

### 👥 Patient Management
- Register new patients
- View patient records
- Search and update patient information
- Manage patient appointments

### 📅 Appointment Management
- Schedule appointments between patients and doctors
- View all appointments
- Update appointment status (Scheduled, Confirmed, Cancelled, Completed, etc.)
- Manage appointment time slots
- Search appointments by various criteria

### 🔧 System Features
- Modern Java Swing GUI with intuitive design
- MongoDB database integration for data persistence
- Demo mode for testing without database connection
- Input validation and error handling
- Responsive user interface

## Technology Stack

- **Language**: Java 8+
- **GUI Framework**: Java Swing
- **Database**: MongoDB (with fallback to demo mode)
- **Build Tool**: Manual compilation with batch scripts
- **Dependencies**: MongoDB Java Driver 4.11.0

## Project Structure

```
DoctorChannellingSystem/
├── src/
│   ├── Main.java                          # Application entry point
│   └── com/xyz/
│       ├── Ui/                            # User Interface components
│       │   ├── Menu.java                  # Main menu
│       │   ├── DoctorWindow.java          # Doctor management UI
│       │   ├── PatientWindow.java         # Patient management UI
│       │   ├── AppointmentWindow.java     # Appointment management UI
│       │   ├── DoctorRegistrationForm.java # Doctor registration form
│       │   └── PatientRegistrationForm.java # Patient registration form
│       ├── model/                         # Data models
│       │   ├── Doctor.java                # Doctor entity
│       │   ├── Patient.java               # Patient entity
│       │   └── Appointment.java           # Appointment entity
│       ├── Service/                       # Business logic layer
│       │   ├── DoctorService.java         # Doctor business logic
│       │   ├── PatientService.java        # Patient business logic
│       │   ├── AppointmentService.java    # Appointment business logic
│       │   ├── NotificationService.java   # Notification handling
│       │   └── DemoDataService.java       # Demo data for testing
│       ├── Data/                          # Data access layer
│       │   ├── DoctorRepository.java      # Doctor data operations
│       │   ├── PatientRepository.java     # Patient data operations
│       │   └── AppointmentRepository.java # Appointment data operations
│       └── Utility/                       # Utility classes
│           ├── MongoDBConnection.java     # Database connection
│           ├── Validator.java             # Input validation
│           └── InputHelper.java           # Input utilities
├── lib/                                   # External dependencies
├── out/                                   # Compiled classes
├── build.bat                             # Build script
├── run.bat                               # Run script
└── README.md                             # This file
```

## Installation and Setup

### Prerequisites
- Java 8 or higher
- MongoDB (optional - application works in demo mode without it)

### Setup Instructions

1. **Clone or download the project**
   ```bash
   git clone <repository-url>
   cd DoctorChannellingSystem
   ```

2. **Compile the project**
   ```bash
   # On Windows
   .\build.bat
   
   # On Linux/Mac
   javac -cp "lib/*" -d out src/com/xyz/model/*.java src/com/xyz/Utility/*.java src/com/xyz/Data/*.java src/com/xyz/Service/*.java src/com/xyz/Ui/*.java src/Main.java
   ```

3. **Run the application**
   ```bash
   # On Windows
   .\run.bat
   
   # On Linux/Mac
   java -cp "out:lib/*" Main
   ```

### MongoDB Setup (Optional)

If you want to use MongoDB for data persistence:

1. **Install MongoDB**
   - Download from [MongoDB website](https://www.mongodb.com/try/download/community)
   - Install and start the MongoDB service

2. **Database Configuration**
   - The application connects to `mongodb://localhost:27017`
   - Database name: `Doctor_System`
   - Collections: `doctors`, `patients`, `appointments`

3. **Demo Mode**
   - If MongoDB is not available, the application automatically switches to demo mode
   - Demo mode provides sample data for testing functionality

## Usage Guide

### Starting the Application
1. Run the application using the provided scripts
2. The main menu will appear with three options:
   - Patient Management
   - Doctor Management
   - Appointment Management

### Doctor Management
1. Click "Doctor Management" from the main menu
2. Choose from the following options:
   - **Register New Doctor**: Add a new doctor to the system
   - **View All Doctors**: See all registered doctors in a table
   - **Search Doctors**: Search by name or specialization
   - **Update Doctor Information**: Modify existing doctor details
   - **Delete Doctor**: Remove a doctor from the system

### Patient Management
1. Click "Patient Management" from the main menu
2. Similar options available for patient records

### Appointment Management
1. Click "Appointment Management" from the main menu
2. Schedule and manage appointments between patients and doctors

## Features in Detail

### Doctor Registration
- **Required Fields**: First Name, Last Name, Specialization, Email, Phone, License Number, Consultation Fee, Qualification, Experience Years
- **Validation**: Email format, phone number format, fee validation, experience validation
- **Duplicate Check**: Prevents duplicate email and license numbers

### Appointment Scheduling
- **Time Slot Management**: Doctors have predefined time slots
- **Conflict Prevention**: Prevents double-booking
- **Status Tracking**: Multiple appointment statuses (Scheduled, Confirmed, Cancelled, Completed, etc.)

### Search and Filter
- **Doctor Search**: By name or specialization
- **Patient Search**: By name or ID
- **Appointment Search**: By date, doctor, or patient

## Error Handling

The application includes comprehensive error handling:
- **Input Validation**: All user inputs are validated before processing
- **Database Errors**: Graceful handling of database connection issues
- **User Feedback**: Clear error messages and success confirmations
- **Demo Mode Fallback**: Automatic fallback to demo data when database is unavailable

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## Troubleshooting

### Common Issues

1. **MongoDB Connection Error**
   - Ensure MongoDB is running on localhost:27017
   - Check if MongoDB service is started
   - Application will work in demo mode if MongoDB is unavailable

2. **Compilation Errors**
   - Ensure Java 8+ is installed
   - Check that all dependencies are in the lib/ directory
   - Verify classpath includes all required JAR files

3. **Runtime Errors**
   - Check Java version compatibility
   - Ensure all compiled classes are in the out/ directory
   - Verify classpath configuration

### Getting Help

If you encounter issues:
1. Check the console output for error messages
2. Ensure all prerequisites are met
3. Try running in demo mode first
4. Check the MongoDB connection if using database mode

## License

This project is open source and available under the MIT License.

## Version History

- **v1.0.0**: Initial release with core functionality
  - Doctor, Patient, and Appointment management
  - MongoDB integration with demo mode fallback
  - Java Swing GUI
  - Comprehensive validation and error handling
