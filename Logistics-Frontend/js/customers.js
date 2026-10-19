/* =========================================================
   LOGISTICS EXCEPTION & DELIVERY RESOLUTION PLATFORM
   Customer Management - Frontend JavaScript
   File: js/customers.js
   ========================================================= */

document.addEventListener("DOMContentLoaded", function () {
    initializeCustomerPage();
});

const CUSTOMER_API_URL = "http://localhost:8080/api/customers";

/* ---------------------------------------------------------
   INITIALIZE CUSTOMER PAGE
   --------------------------------------------------------- */
function initializeCustomerPage() {
    loadCustomers();

    const customerForm = document.getElementById("customerForm");

    if (customerForm) {
        customerForm.addEventListener("submit", handleCustomerSubmit);
    }

    const refreshButton = document.getElementById("refreshCustomers");

    if (refreshButton) {
        refreshButton.addEventListener("click", loadCustomers);
    }

    const clearButton = document.getElementById("clearCustomerForm");

    if (clearButton) {
        clearButton.addEventListener("click", function () {
            if (customerForm) {
                customerForm.reset();
            }
        });
    }
}

/* ---------------------------------------------------------
   LOAD ALL CUSTOMERS
   --------------------------------------------------------- */
async function loadCustomers() {
    const tableBody =
        document.getElementById("customerTableBody") ||
        document.getElementById("customersTableBody");

    const connectionStatus = document.getElementById("customerConnection");
    const customerCount = document.getElementById("totalCustomers");

    if (tableBody) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="7" style="text-align:center;padding:20px;">
                    Loading customer records...
                </td>
            </tr>
        `;
    }

    try {
        const response = await fetch(CUSTOMER_API_URL, {
            method: "GET",
            headers: {
                "Accept": "application/json"
            },
            credentials: "include"
        });

        if (!response.ok) {
            throw new Error(
                "Unable to load customers. HTTP " + response.status
            );
        }

        const customers = await response.json();

        if (!Array.isArray(customers)) {
            throw new Error("The customer API did not return a list.");
        }

        renderCustomers(customers);

        if (customerCount) {
            customerCount.textContent = customers.length;
        }

        if (connectionStatus) {
            connectionStatus.textContent = "Connected";
            connectionStatus.style.color = "#059669";
        }

        showCustomerMessage(
            "Customer records loaded successfully.",
            "success",
            false
        );

    } catch (error) {
        console.error("Error loading customers:", error);

        if (tableBody) {
            tableBody.innerHTML = `
                <tr>
                    <td colspan="7"
                        style="text-align:center;padding:24px;color:#dc2626;">
                        Customer data could not be loaded.
                        Please check the backend connection and login.
                    </td>
                </tr>
            `;
        }

        if (connectionStatus) {
            connectionStatus.textContent = "Not connected";
            connectionStatus.style.color = "#dc2626";
        }

        showCustomerMessage(error.message, "error", false);
    }
}

/* ---------------------------------------------------------
   DISPLAY CUSTOMER RECORDS IN THE TABLE
   --------------------------------------------------------- */
function renderCustomers(customers) {
    const tableBody =
        document.getElementById("customerTableBody") ||
        document.getElementById("customersTableBody");

    if (!tableBody) {
        console.warn(
            "Customer table body was not found. " +
            "Expected customerTableBody or customersTableBody."
        );
        return;
    }

    if (customers.length === 0) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="7"
                    style="text-align:center;padding:24px;color:#64748b;">
                    No customer records found.
                </td>
            </tr>
        `;
        return;
    }

    tableBody.innerHTML = customers.map(function (customer) {
        const id = customer.customerId ?? customer.id ?? "";
        const name = customer.name ?? customer.fullName ?? "";
        const email = customer.email ?? "";
        const phone = customer.phone ?? "";
        const city = customer.city ?? "";
        const address = customer.address ?? "";

        return `
            <tr>
                <td>${escapeCustomerHTML(id)}</td>
                <td>${escapeCustomerHTML(name)}</td>
                <td>${escapeCustomerHTML(email)}</td>
                <td>${escapeCustomerHTML(phone)}</td>
                <td>${escapeCustomerHTML(city)}</td>
                <td>${escapeCustomerHTML(address)}</td>
                <td>
                    <button
                        type="button"
                        class="customer-delete-button"
                        data-customer-id="${escapeCustomerHTML(id)}"
                        aria-label="Delete customer">
                        Delete
                    </button>
                </td>
            </tr>
        `;
    }).join("");

    tableBody.querySelectorAll(".customer-delete-button")
        .forEach(function (button) {
            button.addEventListener("click", function () {
                deleteCustomer(button.dataset.customerId);
            });
        });
}

/* ---------------------------------------------------------
   ADD A CUSTOMER
   --------------------------------------------------------- */
async function handleCustomerSubmit(event) {
    event.preventDefault();

    const form = event.currentTarget;

    const name = getFormValue(form, [
        "name",
        "fullName",
        "customerName"
    ]);

    const email = getFormValue(form, [
        "email",
        "customerEmail"
    ]);

    const phone = getFormValue(form, [
        "phone",
        "customerPhone"
    ]);

    const city = getFormValue(form, [
        "city",
        "customerCity"
    ]);

    const address = getFormValue(form, [
        "address",
        "customerAddress"
    ]);

    if (!name || !email) {
        showCustomerMessage(
            "Please enter the customer's name and email.",
            "error"
        );
        return;
    }

    const customer = {
        name: name,
        email: email,
        phone: phone,
        city: city,
        address: address
    };

    try {
        const response = await fetch(CUSTOMER_API_URL, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json"
            },
            credentials: "include",
            body: JSON.stringify(customer)
        });

        if (!response.ok) {
            const errorText = await response.text();

            throw new Error(
                "Could not save customer. HTTP " +
                response.status +
                (errorText ? " - " + errorText : "")
            );
        }

        showCustomerMessage(
            "Customer saved successfully!",
            "success"
        );

        form.reset();

        await loadCustomers();

    } catch (error) {
        console.error("Error saving customer:", error);

        showCustomerMessage(
            error.message ||
            "An error occurred while saving the customer.",
            "error"
        );
    }
}

/* ---------------------------------------------------------
   DELETE A CUSTOMER
   --------------------------------------------------------- */
async function deleteCustomer(customerId) {
    if (!customerId) {
        showCustomerMessage(
            "Customer ID is missing.",
            "error"
        );
        return;
    }

    const confirmed = window.confirm(
        "Are you sure you want to delete customer ID " +
        customerId +
        "?"
    );

    if (!confirmed) {
        return;
    }

    try {
        const response = await fetch(
            CUSTOMER_API_URL + "/" + encodeURIComponent(customerId),
            {
                method: "DELETE",
                headers: {
                    "Accept": "application/json"
                },
                credentials: "include"
            }
        );

        if (!response.ok) {
            throw new Error(
                "Could not delete customer. HTTP " + response.status
            );
        }

        showCustomerMessage(
            "Customer deleted successfully.",
            "success"
        );

        await loadCustomers();

    } catch (error) {
        console.error("Error deleting customer:", error);

        showCustomerMessage(
            error.message ||
            "An error occurred while deleting the customer.",
            "error"
        );
    }
}

/* ---------------------------------------------------------
   HELPER: GET A FORM VALUE USING POSSIBLE FIELD NAMES
   --------------------------------------------------------- */
function getFormValue(form, possibleNames) {
    for (const fieldName of possibleNames) {
        const field =
            form.elements.namedItem(fieldName) ||
            document.getElementById(fieldName);

        if (field && typeof field.value === "string") {
            return field.value.trim();
        }
    }

    return "";
}

/* ---------------------------------------------------------
   HELPER: SAFELY DISPLAY CUSTOMER DATA
   --------------------------------------------------------- */
function escapeCustomerHTML(value) {
    return String(value ?? "").replace(/[&<>"']/g, function (character) {
        const replacements = {
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#39;"
        };

        return replacements[character];
    });
}

/* ---------------------------------------------------------
   HELPER: DISPLAY SUCCESS AND ERROR MESSAGES
   --------------------------------------------------------- */
function showCustomerMessage(message, type, autoHide = true) {
    let messageElement = document.getElementById("customerMessage");

    if (!messageElement) {
        messageElement = document.createElement("div");
        messageElement.id = "customerMessage";
        messageElement.setAttribute("role", "status");
        messageElement.style.margin = "12px 0";
        messageElement.style.padding = "12px 16px";
        messageElement.style.borderRadius = "10px";
        messageElement.style.fontSize = "14px";

        const mainContent =
            document.querySelector("main") ||
            document.querySelector(".main-content") ||
            document.querySelector(".content");

        if (mainContent) {
            mainContent.prepend(messageElement);
        } else {
            return;
        }
    }

    messageElement.textContent = message;
    messageElement.style.display = "block";

    if (type === "error") {
        messageElement.style.color = "#b91c1c";
        messageElement.style.background = "#fee2e2";
        messageElement.style.border = "1px solid #fecaca";
    } else {
        messageElement.style.color = "#047857";
        messageElement.style.background = "#d1fae5";
        messageElement.style.border = "1px solid #a7f3d0";
    }

    if (messageElement.customerMessageTimer) {
        clearTimeout(messageElement.customerMessageTimer);
    }

    if (autoHide) {
        messageElement.customerMessageTimer = setTimeout(function () {
            messageElement.style.display = "none";
        }, 4000);
    }
}