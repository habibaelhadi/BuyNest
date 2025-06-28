# 🛒 BuyNest

**BuyNest** is a mobile shopping application designed to offer users a smooth and intuitive online shopping experience. Built with modern Android development practices, the app allows users to browse products, filter by category and price, search by keyword, manage their cart, and authenticate using Firebase.

---

## 📱 Features

- 🔐 Firebase Authentication (Email/Password)
- 🔎 Real-time product search with filter & price range
- 🛍 Browse products by category
- 🛒 Add to cart and manage cart items
- ❤️ Add/remove items to/from favorites
- 📄 Product details screen
- 🧭 Bottom navigation for easy navigation

---

## 🧠 Architecture & Tech Stack

- **Architecture:** MVVM + Repository pattern
- **Language:** Kotlin
- **UI Toolkit:** Jetpack Compose
- **Navigation:** Jetpack Navigation Component
- **State Management:** ViewModel + StateFlow
- **Authentication:** Firebase Auth
- **Database & Backend:** Firebase Firestore
- **Dependency Injection:** Hilt
- **Network:** Apollo GraphQL (for Shopify integration)
- **Image Loading:** Coil
- **Testing:** Unit testing with JUnit and Mockito

---

## 📸 Screenshots

| Home | Search + Filter | Product Details | Cart |
|------|------------------|------------------|------|
| ![Home](screenshots/home.png) | ![Search](screenshots/search.png) | ![Details](screenshots/details.png) | ![Cart](screenshots/cart.png) |

*Add your actual screenshots in the `screenshots/` folder for proper display.*

---

## 🧪 Testing

- Unit tests for ViewModels and repositories
- Tested Firebase Auth logic using mocked callbacks
- Coroutine test support with `runTest`

---

## 🚧 Future Enhancements

- Order tracking system
- Payment integration (Stripe/PayPal)
- User profile screen
- Product reviews and ratings
- Admin dashboard for managing products

---

## 🧑‍💻 Developer

**Habiba Mohamed Elhadi**  
Android Developer | ITI Graduate  
- 🔗 [GitHub](https://github.com/habibaelhadi)
- 🔗 [LinkedIn](https://www.linkedin.com/in/habiba-elhadi-228774336)

---

## 📂 How to Run

1. Clone the repo:
   ```bash
   git clone https://github.com/habibaelhadi/BuyNest.git
