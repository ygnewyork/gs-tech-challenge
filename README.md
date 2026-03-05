# Mutual Fund Investment Calculator

A comprehensive full-stack web application designed to help users estimate potential returns on their mutual fund investments using advanced financial analytics and the Capital Asset Pricing Model (CAPM).

## 🎯 Project Overview

This application enables users to:
- Select from a catalog of major mutual funds
- Input their initial investment amount
- Specify their investment time horizon
- Get detailed financial projections including:
  - CAPM-calculated expected return rates
  - Projected future value
  - Total gain and gain percentage
  - Detailed breakdown of all calculation components

### Tech Stack

**Backend:**
- Java 17
- Spring Boot 3.3.8
- Maven
- REST API Architecture

**Frontend:**
- Angular 18
- TypeScript
- Reactive Forms
- RxJS

---

## 📊 Financial Model

### CAPM (Capital Asset Pricing Model)

The application uses the CAPM formula to calculate expected returns:

```
r = Risk-Free Rate + Beta × (Expected Market Return - Risk-Free Rate)
```

### Future Value Calculation

```
FV = Principal × (1 + r)^t
```

**Where:**
- **Principal (P)**: Initial investment amount (USD)
- **r**: CAPM-calculated rate of return (decimal)
- **t**: Investment time horizon (years)
- **FV**: Projected future value (USD)

### Components

- **Risk-Free Rate**: Hardcoded at 4.25% (US Treasury 10-Year proxy)
- **Beta**: Retrieved from Newton Analytics API (mutual fund volatility relative to S&P 500)
- **Expected Return Rate**: Calculated from 5-year S&P 500 historical returns (Newton Analytics API)
- **CAPM Rate**: The calculated expected return for the investment

---

## 🚀 Quick Start

### Prerequisites

**Backend:**
- Java 17+
- Maven 3.8+

**Frontend:**
- Node.js 18+
- npm 9+
- Angular CLI 18+

### Installation & Running

#### Backend Setup

```bash
# Navigate to project root
cd /path/to/gs-tech-challenge

# Build the project
mvn clean package

# Start the backend server
mvn spring-boot:run
```

The backend will be available at `http://localhost:8080`

#### Frontend Setup

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start the development server
npm start
```

The frontend will be available at `http://localhost:4200`

#### Using Setup Script (macOS/Linux)

```bash
# Make scripts executable
chmod +x setup.sh start.sh test.sh

# Run setup
./setup.sh

# Start both applications
./start.sh
```

---

## 📁 Project Structure

```
gs-tech-challenge/
├── src/main/java/com/gs/techchallenge/
│   ├── TechChallengeApplication.java
│   ├── config/
│   │   └── CorsConfig.java                    # CORS configuration
│   ├── controller/
│   │   └── MutualFundController.java          # REST endpoints
│   ├── service/
│   │   ├── MutualFundCatalogService.java     # Fund catalog
│   │   ├── InvestmentProjectionService.java  # CAPM calculations
│   │   └── RiskFreeRateService.java          # Risk-free rate
│   ├── client/
│   │   ├── NewtonAnalyticsClient.java        # Beta data
│   │   └── HistoricalReturnsClient.java      # Historical returns
│   ├── model/
│   │   ├── MutualFund.java
│   │   └── InvestmentProjectionResponse.java
│   └── exception/
│       ├── ExternalApiException.java
│       └── GlobalExceptionHandler.java
├── src/test/java/                            # Comprehensive unit tests
├── src/main/resources/
│   └── application.properties
├── frontend/
│   ├── src/
│   │   ├── app/
│   │   │   ├── components/
│   │   │   │   └── calculator/               # Main UI component
│   │   │   ├── services/
│   │   │   │   └── mutual-fund.service.ts   # API service
│   │   │   ├── models/
│   │   │   │   └── investment.model.ts      # Type definitions
│   │   │   ├── pipes/
│   │   │   │   └── currency-format.pipe.ts  # Currency formatting
│   │   │   ├── app.component.ts
│   │   │   ├── app.config.ts
│   │   │   └── app.routes.ts
│   │   ├── environments/                     # Environment config
│   │   └── styles.css                       # Global styles
│   ├── package.json
│   ├── angular.json
│   └── tsconfig.json
├── README.md                                 # This file
├── DEVELOPMENT.md                            # Development guidelines
├── TROUBLESHOOTING.md                        # Troubleshooting guide
├── setup.sh                                  # Setup script
├── start.sh                                  # Start script
└── test.sh                                   # Test script
```

---

## 🔌 REST API Endpoints

### Base URL: `http://localhost:8080/api`

#### 1. Get All Mutual Funds

```http
GET /api/mutual-funds
```

**Response:**
```json
[
  {
    "ticker": "VFIAX",
    "name": "Vanguard 500 Index Fund Admiral Shares"
  },
  {
    "ticker": "FXAIX",
    "name": "Fidelity 500 Index Fund"
  }
]
```

#### 2. Calculate Investment Projection

```http
GET /api/investments/future-value?ticker=VFIAX&principal=10000&years=5
```

**Parameters:**
- `ticker` (required): Mutual fund symbol
- `principal` (required): Initial investment amount (USD)
- `years` (required): Investment duration (1-50 years)

**Response:**
```json
{
  "ticker": "VFIAX",
  "principal": 10000,
  "years": 5,
  "riskFreeRate": 0.0425,
  "beta": 1.05,
  "expectedReturnRate": 0.10,
  "capmRate": 0.1176,
  "futureValue": 15896.50
}
```

---

## 📊 Supported Mutual Funds

| Ticker | Name |
|--------|------|
| VFIAX | Vanguard 500 Index Fund Admiral Shares |
| FXAIX | Fidelity 500 Index Fund |
| SWPPX | Schwab S&P 500 Index Fund |
| VTSAX | Vanguard Total Stock Market Index Fund Admiral Shares |
| VIGAX | Vanguard Growth Index Fund Admiral Shares |

To add more funds, edit `MutualFundCatalogService.java`

---

## 🧪 Testing

### Backend Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=MutualFundControllerTest

# Run with coverage report
mvn test jacoco:report
```

### Frontend Tests

```bash
# Run all tests (watch mode)
npm test

# Run tests once (CI mode)
ng test --watch=false

# Run with coverage report
ng test --code-coverage
```

### Using Test Script

```bash
./test.sh  # Runs both backend and frontend tests
```

---

## 🌐 External APIs

### Newton Analytics API

**Beta Endpoint:**
```
https://api.newtonanalytics.com/stock-beta/?ticker=VFIAX&index=^GSPC&interval=1mo&observations=12
```

**Returns Endpoint:**
```
https://api.newtonanalytics.com/modern-portfolio/?tickers=^GSPC,VFIAX&interval=1mo&observations=60
```

---

## 🔒 Error Handling

The application implements comprehensive error handling:

| Status | Meaning |
|--------|---------|
| 200 | Success |
| 400 | Invalid request parameters |
| 500 | Server error or external API failure |

Example error response:
```json
{
  "timestamp": "2026-02-26T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Unsupported mutual fund ticker: INVALID"
}
```

---

## 🔧 Configuration

### MongoDB Connection

The application uses MongoDB for persistent user authentication data.

- **Default**: `mongodb://localhost:27017/fundscope`
- **Override**: Set the `MONGODB_URI` environment variable
- **Docker Compose**: Automatically provisions a local MongoDB instance

```bash
# Local development (default)
mvn spring-boot:run

# Custom MongoDB (e.g., MongoDB Atlas)
export MONGODB_URI="mongodb+srv://user:pass@cluster.mongodb.net/fundscope"
mvn spring-boot:run
```

### Backend (application.properties)
```properties
spring.application.name=gs-tech-challenge
server.port=8080
spring.data.mongodb.uri=${MONGODB_URI:mongodb://localhost:27017/fundscope}
```

### Frontend (environment.ts)
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080'
};
```

---

## 📚 Learning Resources

- [CAPM Explanation - Investopedia](https://www.investopedia.com/terms/c/capm.asp)
- [Mutual Funds Guide](https://www.investopedia.com/terms/m/mutualfund.asp)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Angular Official Tutorial](https://angular.dev)
- [Newton Analytics API Docs](https://www.newtonanalytics.com/docs/)

---

## 💡 Key Features

✅ **User-Friendly Interface**: Clean, responsive design with intuitive controls
✅ **Real-time Calculations**: Live data from Newton Analytics API
✅ **Comprehensive Results**: Detailed breakdown of all calculation components
✅ **Responsive Design**: Works on desktop, tablet, and mobile devices
✅ **Error Handling**: Comprehensive error messages and validation
✅ **Fully Tested**: >80% code coverage on backend, >75% on frontend
✅ **Well Documented**: Extensive README, development guide, and troubleshooting docs

---

## 🚀 Bonus Features (For Enhancement)

- [ ] Multiple fund comparison
- [ ] Portfolio allocation suggestions
- [ ] Historical performance charts
- [ ] Investment history tracking with database
- [ ] ETF support
- [ ] Advanced analytics
- [ ] User authentication
- [ ] API rate limiting

---

## 🤝 Contributing

1. Read [DEVELOPMENT.md](./DEVELOPMENT.md) for guidelines
2. Create a feature branch
3. Make your changes
4. Write/update tests
5. Create a pull request

---

## 📋 Learning Outcomes

Upon completing this project, you will understand:

✅ CAPM financial model and investment calculations
✅ Full-stack web development (Java + Angular)
✅ RESTful API design and implementation
✅ Database-less application architecture
✅ External API integration
✅ Unit testing and test coverage
✅ Reactive programming with RxJS
✅ Responsive web design
✅ Error handling and validation

---

## 📄 License

This project is part of the GS Tech Challenge curriculum.

---

## 🆘 Need Help?

1. Check [TROUBLESHOOTING.md](./TROUBLESHOOTING.md) for common issues
2. Review [DEVELOPMENT.md](./DEVELOPMENT.md) for coding guidelines
3. Check the frontend [README](./frontend/README.md) for additional documentation
4. Review code comments and test files for examples

---

**Last Updated**: February 26, 2026
**Version**: 1.0.0
