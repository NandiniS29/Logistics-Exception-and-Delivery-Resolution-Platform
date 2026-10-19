/* ==========================================
   LOGISTICS PLATFORM - API CONFIGURATION
   ========================================== */

const API_BASE_URL = "http://localhost:8080";

/**
 * Get the authentication token saved during login.
 */
function getAuthToken() {
    return (
        localStorage.getItem("token") ||
        localStorage.getItem("authToken") ||
        localStorage.getItem("jwtToken") ||
        ""
    );
}

/**
 * Send requests to the Spring Boot backend.
 */
async function apiRequest(endpoint, options = {}) {

    const token = getAuthToken();

    const headers = {
        ...(options.headers || {})
    };

    if (!headers["Content-Type"] && options.body) {
        headers["Content-Type"] = "application/json";
    }

    if (token) {
        headers["Authorization"] = `Bearer ${token}`;
    }

    const response = await fetch(
        `${API_BASE_URL}${endpoint}`,
        {
            ...options,
            credentials: "include",
            headers: headers
        }
    );

    if (response.status === 401) {
        throw new Error(
            "Authentication failed. Please log in again."
        );
    }

    if (response.status === 403) {
        throw new Error(
            "You do not have permission to perform this action."
        );
    }

    if (!response.ok) {
        const message = await response.text();

        throw new Error(
            message || `Request failed: HTTP ${response.status}`
        );
    }

    if (response.status === 204) {
        return null;
    }

    const contentType = response.headers.get("content-type");

    if (contentType && contentType.includes("application/json")) {
        return await response.json();
    }

    return await response.text();
}

/**
 * GET request
 */
async function apiGet(endpoint) {
    return apiRequest(endpoint, {
        method: "GET"
    });
}

/**
 * POST request
 */
async function apiPost(endpoint, data) {
    return apiRequest(endpoint, {
        method: "POST",
        body: JSON.stringify(data)
    });
}

/**
 * PUT request
 */
async function apiPut(endpoint, data) {
    return apiRequest(endpoint, {
        method: "PUT",
        body: JSON.stringify(data)
    });
}

/**
 * DELETE request
 */
async function apiDelete(endpoint) {
    return apiRequest(endpoint, {
        method: "DELETE"
    });
}