# Campus Network Auto Sign-In

A Kotlin application that automatically logs into a campus network using stored credentials.

## Features

- Automatic login to campus network
- Command-line interface for configuration and testing
- Configurable retry mechanism
- Secure credential storage
- Logging for troubleshooting

## Requirements

- Java 11 or higher
- Gradle 7.0 or higher

## Setup

1. Clone the repository
2. Build the project with Gradle:

```bash
./gradlew build
```

3. Run the application:

```bash
./gradlew run
```

## Usage

The application can be used in two modes:

### Interactive Mode

Run the application without arguments to start the command-line interface:

```bash
./gradlew run
```

This will present a menu with the following options:

1. **Set credentials**: Configure your username, password, and login URL
2. **Test login**: Test the login functionality with the current configuration
3. **Show current configuration**: Display the current configuration
4. **Exit**: Exit the application

### Auto-login Mode

Run the application with the `--auto-login` argument to attempt login immediately:

```bash
./gradlew run --args="--auto-login"
```

This is useful for scheduling automatic logins using cron jobs or task schedulers.

## Configuration

The application stores configuration in a properties file located at:

```
~/.campus-autologin.properties
```

The following properties can be configured:

- `username`: Your campus network username
- `password`: Your campus network password
- `loginUrl`: The URL endpoint for the login request
- `autoRetry`: Whether to automatically retry login on failure (true/false)
- `maxRetries`: Maximum number of retry attempts
- `retryDelayMs`: Delay between retry attempts in milliseconds

## Scheduling Automatic Login

### On Linux/macOS (using cron)

Add a cron job to run the application periodically:

```bash
# Edit crontab
crontab -e

# Add a line to run the application every hour
0 * * * * cd /path/to/project && ./gradlew run --args="--auto-login" > /dev/null 2>&1
```

### On Windows (using Task Scheduler)

1. Open Task Scheduler
2. Create a new task
3. Set the trigger to run at your desired schedule
4. Set the action to run the application with the `--auto-login` argument

## Troubleshooting

The application logs to the console by default. If you encounter issues, check the log output for error messages.

## License

This project is licensed under the MIT License - see the LICENSE file for details.