// ==================================================
// LOGISTICS PLATFORM - AUTHENTICATION
// Logistics Control Officer Login + Customer Login + Registration
// ==================================================

let selectedRole = "admin";

const API_BASE_URL = "http://127.0.0.1:8080";

// Update this only if your backend uses a different registration endpoint.
const CUSTOMER_REGISTER_URL =
    API_BASE_URL + "/api/users/register";


// ==================================================
// ROLE SELECTION
// ==================================================

function selectRole(role) {

    if (role !== "admin" && role !== "customer") {
        return;
    }

    selectedRole = role;

    const adminButton =
        document.getElementById("adminRoleBtn");

    const customerButton =
        document.getElementById("customerRoleBtn");

    if (adminButton) {
        adminButton.classList.toggle(
            "active",
            role === "admin"
        );
    }

    if (customerButton) {
        customerButton.classList.toggle(
            "active",
            role === "customer"
        );
    }

    const message =
        document.getElementById("loginMessage");

    if (message) {
        message.textContent = "";
    }
}


// ==================================================
// POPUP MESSAGE
// ==================================================

function showPopup(message, type = "info") {

    let popup = document.getElementById("securityPopup");

    if (!popup) {

        popup = document.createElement("div");

        popup.id = "securityPopup";

        popup.setAttribute("role", "alert");

        document.body.appendChild(popup);
    }

    const symbols = {
        success: "✓",
        error: "🔒",
        info: "ℹ",
        warning: "⚠"
    };

    popup.textContent =
        (symbols[type] || "ℹ") + " " + message;

    popup.className = "security-popup " + type;

    popup.style.display = "block";

    clearTimeout(window.securityPopupTimer);

    window.securityPopupTimer = setTimeout(() => {
        popup.style.display = "none";
    }, 4000);
}


// ==================================================
// PASSWORD VISIBILITY
// ==================================================

function togglePassword() {

    const passwordInput =
        document.getElementById("password");

    if (!passwordInput) {
        return;
    }

    passwordInput.type =
        passwordInput.type === "password"
            ? "text"
            : "password";
}


// ==================================================
// FORGOT PASSWORD
// ==================================================

function forgotPassword(event) {

    if (event) {
        event.preventDefault();
    }

    showPopup(
        "Please contact the administrator to reset your password.",
        "info"
    );
}


// ==================================================
// SWITCH BETWEEN LOGIN AND REGISTRATION
// ==================================================

function showRegistration() {

    const loginPanel =
        document.getElementById("loginPanel");

    const registrationPanel =
        document.getElementById("registrationPanel");

    if (!registrationPanel) {

        showPopup(
            "The registration form is not available on this page.",
            "error"
        );

        return;
    }

    if (loginPanel) {
        loginPanel.style.display = "none";
    }

    registrationPanel.style.display = "block";

    const registrationMessage =
        document.getElementById("registrationMessage");

    if (registrationMessage) {
        registrationMessage.textContent = "";
    }
}


function showLogin() {

    const loginPanel =
        document.getElementById("loginPanel");

    const registrationPanel =
        document.getElementById("registrationPanel");

    if (registrationPanel) {
        registrationPanel.style.display = "none";
    }

    if (loginPanel) {
        loginPanel.style.display = "block";
    }
}


// ==================================================
// CUSTOMER REGISTRATION
// ==================================================

document.addEventListener("DOMContentLoaded", function () {

    const loginForm =
        document.getElementById("loginForm");

    const registrationForm =
        document.getElementById("registrationForm");


    // ----------------------------------------------
    // LOGIN FORM
    // ----------------------------------------------

    if (loginForm) {

        loginForm.addEventListener(
            "submit",
            async function (event) {

                event.preventDefault();

                const username =
                    document.getElementById("username")
                        .value.trim();

                const password =
                    document.getElementById("password")
                        .value;

                const message =
                    document.getElementById("loginMessage");


                if (!username || !password) {

                    message.style.color = "#dc2626";

                    message.textContent =
                        "Please enter your username and password.";

                    return;
                }


                message.style.color = "#67dfff";

                message.textContent =
                    "Authenticating...";


                try {

                    const response = await fetch(
                        API_BASE_URL + "/api/auth/login",
                        {
                            method: "POST",

                            headers: {
                                "Content-Type": "application/json"
                            },

                            credentials: "include",

                            body: JSON.stringify({
                                username: username,
                                password: password
                            })
                        }
                    );


                    const responseText =
                        await response.text();

                    let data = {};

                    try {
                        data = responseText
                            ? JSON.parse(responseText)
                            : {};
                    } catch {
                        data = {};
                    }


                    if (!response.ok) {

                        message.style.color = "#ff7373";

                        message.textContent =
                            data.message ||
                            "Invalid username or password.";

                        return;
                    }


                    // ----------------------------------
                    // CHECK AUTHENTICATED ROLE
                    // ----------------------------------

                    const actualRole =
                        String(data.role || "")
                            .trim()
                            .toUpperCase();

                    const expectedRole =
                        selectedRole === "admin"
                            ? "LOGISTICS CONTROL OFFICER"
                            : "CUSTOMER";


                    // Never trust the selected button alone.
                    // Compare it against the role returned
                    // by the authentication backend.

                    if (actualRole !== expectedRole) {

                        message.style.color = "#ff7373";

                        if (selectedRole === "admin") {

                            message.textContent =
                                "Restricted Area! Only authorised Logistics Control Officer's can access this dashboard";

                            showPopup(
                                "Restricted Area! Only authorised Logistics Control Officer's may access the Control Officer dashboard.",
                                "error"
                            );

                        } else {

                            message.textContent =
                                "This account is not registered as a Customer. Please select the correct account type.";

                            showPopup(
                                "Account role mismatch. Please select the correct login type.",
                                "warning"
                            );
                        }

                        return;
                    }


                    // ----------------------------------
                    // CHECK ACCOUNT STATUS
                    // ----------------------------------

                    if (
                        data.status &&
                        String(data.status).toUpperCase() !== "ACTIVE"
                    ) {

                        message.style.color = "#ff7373";

                        message.textContent =
                            "Your account is not active.";

                        return;
                    }


                    // ----------------------------------
                    // STORE USER DETAILS
                    // ----------------------------------

                    localStorage.setItem(
                        "username",
                        data.username || username
                    );

                    localStorage.setItem(
                        "fullName",
                        data.fullName || ""
                    );

                    localStorage.setItem(
                        "userId",
                        data.userId || ""
                    );

                    localStorage.setItem(
                        "role",
                        actualRole
                    );

                    localStorage.setItem(
                        "userRole",
                        selectedRole
                    );

                    localStorage.setItem(
                        "email",
                        data.email || ""
                    );


                    message.style.color = "#16d982";

                    message.textContent =
                        "Login successful. Redirecting...";


                    setTimeout(function () {

                        if (
                            actualRole ===
                            "LOGISTICS CONTROL OFFICER"
                        ) {

                            window.location.href =
                                "admin/dashboard.html";

                        } else if (
                            actualRole === "CUSTOMER"
                        ) {

                            window.location.href =
                                "customer/dashboard.html";

                        }

                    }, 700);

                } catch (error) {

                    console.error("Login error:", error);

                    message.style.color = "#ff7373";

                    message.textContent =
                        "Unable to connect to the server. Please check that Spring Boot is running.";
                }
            }
        );
    }


    // ----------------------------------------------
    // CUSTOMER REGISTRATION FORM
    // ----------------------------------------------

    if (registrationForm) {

        registrationForm.addEventListener(
            "submit",
            async function (event) {

                event.preventDefault();

                const registrationMessage =
                    document.getElementById(
                        "registrationMessage"
                    );


                const customer = {

                    fullName:
                        document.getElementById("regName")
                            .value.trim(),

                    phone:
                        document.getElementById("regPhone")
                            .value.trim(),

                    alternativePhone:
                        document.getElementById("regAlternativePhone")
                            .value.trim(),

                    email:
                        document.getElementById("regEmail")
                            .value.trim(),

                    address:
                        document.getElementById("regAddress")
                            .value.trim(),

                    city:
                        document.getElementById("regCity")
                            .value.trim(),

                    state:
                        document.getElementById("regState")
                            .value.trim(),

                    pincode:
                        document.getElementById("regPincode")
                            .value.trim(),

                    password:
                        document.getElementById("regPassword")
                            .value
                };


                if (
                    !customer.fullName ||
                    !customer.phone ||
                    !customer.email ||
                    !customer.address ||
                    !customer.city ||
                    !customer.state ||
                    !customer.pincode ||
                    !customer.password
                ) {

                    registrationMessage.textContent =
                        "Please complete all required fields.";

                    registrationMessage.style.color =
                        "#ff7373";

                    return;
                }


                if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(customer.email)) {

                    registrationMessage.textContent =
                        "Please enter a valid email address.";

                    registrationMessage.style.color =
                        "#ff7373";

                    return;
                }


                if (!/^\d{6}$/.test(customer.pincode)) {

                    registrationMessage.textContent =
                        "Please enter a valid 6-digit pincode.";

                    registrationMessage.style.color =
                        "#ff7373";

                    return;
                }


                const submitButton =
                    document.getElementById(
                        "createAccountButton"
                    );

                submitButton.disabled = true;

                registrationMessage.style.color =
                    "#67dfff";

                registrationMessage.textContent =
                    "Creating your account...";


                try {

                    const response = await fetch(
                        CUSTOMER_REGISTER_URL,
                        {
                            method: "POST",

                            headers: {
                                "Content-Type": "application/json"
                            },

                            credentials: "include",

                            body: JSON.stringify(customer)
                        }
                    );


                    const responseText =
                        await response.text();

                    let data = {};

                    try {
                        data = responseText
                            ? JSON.parse(responseText)
                            : {};
                    } catch {
                        data = {};
                    }


                    if (!response.ok) {

                        throw new Error(
                            data.message ||
                            "Registration failed. The backend registration API may not be configured yet."
                        );
                    }


                    registrationMessage.style.color =
                        "#16d982";

                    registrationMessage.textContent =
                        "Account created successfully. You can now log in.";

                    showPopup(
                        "Customer account created successfully.",
                        "success"
                    );

                    registrationForm.reset();

                    setTimeout(showLogin, 1500);

                } catch (error) {

                    console.error(
                        "Registration error:",
                        error
                    );

                    registrationMessage.style.color =
                        "#ff7373";

                    registrationMessage.textContent =
                        error.message ||
                        "Unable to create account. Please try again.";

                } finally {

                    submitButton.disabled = false;
                }
            }
        );
    }

});