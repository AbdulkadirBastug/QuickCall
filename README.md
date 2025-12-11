# Quick Call 📞⚡

**Quick Call** is a specialized Android application designed for speed and accessibility. It allows you to call a pre-saved emergency or favorite contact instantly by simply **double-pressing the volume button**, even when your phone is locked or you are using another app.

## 🚀 Features

-   **Background Calling:** Initiate calls without unlocking your phone or opening the app.
-   **Volume Key Trigger:** Double-press Volume Up or Down to dial.
-   **Instant & Reliable:** Uses the official Android Telecom API for robust performance.
-   **Safety First:** Vibrates to confirm the command before dialing.
-   **User Friendly:** Simple interface to save your number and enable the service.

## 🛠️ How It Works

1.  **Install & Open:** Launch the app and grant the necessary permissions (Phone, Overlay).
2.  **Save Number:** Enter the phone number you want to call quickly.
3.  **Enable Service:** Turn on "Quick Call" in your phone's Accessibility Settings.
4.  **Use Anywhere:** Go to your home screen, lock your phone, or browse the web.
5.  **Trigger:** Click the volume button twice.
6.  **Dialing:** The app will vibrate to confirm and immediately launch the call.

## 📱 Installation

### From APK
1.  Download the `app-debug.apk` or build it yourself.
2.  Install it on your Android device.
3.  Follow the in-app setup guide.

### From Source (Developers)
1.  Clone the repository:
    ```bash
    git clone https://github.com/AbdulkadirBastug/QuickCall.git
    ```
2.  Navigate to the project folder:
    ```bash
    cd QuickCall/App
    ```
3.  Install dependencies:
    ```bash
    npm install
    ```
4.  Run on Android:
    ```bash
    npx react-native run-android
    ```

## ⚠️ Important Permissions

To function correctly, Quick Call requires:
-   **Accessibility Service:** To detect volume button presses in the background.
-   **Display Over Other Apps:** To launch the call screen from the background.
-   **Call Phone:** To initiate the phone call.