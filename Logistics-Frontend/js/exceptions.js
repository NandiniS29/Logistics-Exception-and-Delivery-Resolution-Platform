/* ============================================================
   LOGISTICS EXCEPTION & DELIVERY RESOLUTION PLATFORM
   Exception Management
   File: js/exceptions.js
   ============================================================ */

document.addEventListener("DOMContentLoaded", function () {
    initializeExceptionsPage();
});

const EXCEPTIONS_API_URL = "http://localhost:8080/api/exceptions";

/* ============================================================
   INITIALIZE PAGE
   ============================================================ */

function initializeExceptionsPage() {
    loadExceptions();

    const refreshButton = document.getElementById("refreshExceptions");

    if (refreshButton) {
        refreshButton.addEventListener("click", loadExceptions);
    }

    const searchInput = document.getElementById("exceptionSearch");

    if (searchInput) {
        searchInput.addEventListener("input", filterExceptions);
    }

    const statusFilter = document.getElementById("exceptionStatusFilter");

    if (statusFilter) {
        statusFilter.addEventListener("change", filterExceptions);
    }

    const priorityFilter = document.getElementById("exceptionPriorityFilter");

    if (priorityFilter) {
        priorityFilter.addEventListener("change", filterExceptions);
    }
}

/* ============================================================
   LOAD EXCEPTIONS FROM SPRING BOOT
   ============================================================ */

let allExceptions = [];

async function loadExceptions() {
    const tableBody =
        document.getElementById("exceptionsTableBody") ||
        document.getElementById("exceptionTableBody");

    setExceptionMessage("Loading exception records...", "info", false);

    if (tableBody) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="8" style="text-align:center;padding:24px;">
                    Loading exceptions...
                </td>
            </tr>
        `;
    }

    try {
        const response = await fetch(EXCEPTIONS_API_URL, {
            method: "GET",
            headers: {
                "Accept": "application/json"
            },
            credentials: "include"
        });

        if (!response.ok) {
            throw new Error(
                "Unable to load exceptions. HTTP " + response.status
            );
        }

        const data = await response.json();

        if (!Array.isArray(data)) {
            throw new Error("The exceptions API did not return a list.");
        }

        allExceptions = data;

        displayExceptions(allExceptions);
        updateExceptionStatistics(allExceptions);

        setExceptionMessage(
            "Exception records loaded successfully.",
            "success"
        );

    } catch (error) {
        console.error("Error loading exceptions:", error);

        if (tableBody) {
            tableBody.innerHTML = `
                <tr>
                    <td colspan="8"
                        style="text-align:center;padding:24px;color:#dc2626;">
                        Exception records could not be loaded.
                        Check your backend connection and login.
                    </td>
                </tr>
            `;
        }

        setExceptionMessage(error.message, "error");
    }
}

/* ============================================================
   DISPLAY EXCEPTIONS
   ============================================================ */

function displayExceptions(exceptions) {
    const tableBody =
        document.getElementById("exceptionsTableBody") ||
        document.getElementById("exceptionTableBody");

    if (!tableBody) {
        console.warn(
            "Exception table not found. Expected " +
            "exceptionsTableBody or exceptionTableBody."
        );
        return;
    }

    if (!exceptions || exceptions.length === 0) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="8"
                    style="text-align:center;padding:24px;color:#64748b;">
                    No exception records found.
                </td>
            </tr>
        `;
        return;
    }

    tableBody.innerHTML = exceptions.map(function (exception) {
        const id = exception.exceptionId ?? exception.id ?? "";
        const shipmentId = exception.shipmentId ?? "";
        const type = exception.exceptionType ?? exception.type ?? "";
        const description = exception.description ?? "";
        const priority = exception.priority ?? "Normal";
        const status = exception.status ?? "Open";
        const reportedDate =
            exception.reportedDate ??
            exception.createdAt ??
            "";

        const dateText = formatExceptionDate(reportedDate);

        return `
            <tr>
                <td>${escapeExceptionHTML(id)}</td>

                <td>${escapeExceptionHTML(shipmentId)}</td>

                <td>${escapeExceptionHTML(type)}</td>

                <td>${escapeExceptionHTML(description)}</td>

                <td>
                    <span class="exception-priority ${getPriorityClass(priority)}">
                        ${escapeExceptionHTML(priority)}
                    </span>
                </td>

                <td>
                    <span class="exception-status ${getStatusClass(status)}">
                        ${escapeExceptionHTML(status)}
                    </span>
                </td>

                <td>${escapeExceptionHTML(dateText)}</td>

                <td>
                    <button
                        type="button"
                        class="exception-view-button"
                        data-exception-id="${escapeExceptionHTML(id)}">
                        View
                    </button>
                </td>
            </tr>
        `;
    }).join("");

    tableBody.querySelectorAll(".exception-view-button")
        .forEach(function (button) {
            button.addEventListener("click", function () {
                openExceptionDetails(button.dataset.exceptionId);
            });
        });
}

/* ============================================================
   SEARCH AND FILTER
   ============================================================ */

function filterExceptions() {
    const searchInput = document.getElementById("exceptionSearch");
    const statusFilter = document.getElementById("exceptionStatusFilter");
    const priorityFilter = document.getElementById("exceptionPriorityFilter");

    const searchText = searchInput
        ? searchInput.value.trim().toLowerCase()
        : "";

    const selectedStatus = statusFilter
        ? statusFilter.value.toLowerCase()
        : "";

    const selectedPriority = priorityFilter
        ? priorityFilter.value.toLowerCase()
        : "";

    const filteredExceptions = allExceptions.filter(function (exception) {
        const searchableText = [
            exception.exceptionId,
            exception.shipmentId,
            exception.exceptionType,
            exception.description,
            exception.priority,
            exception.status
        ].join(" ").toLowerCase();

        const matchesSearch = searchableText.includes(searchText);

        const matchesStatus =
            !selectedStatus ||
            selectedStatus === "all" ||
            String(exception.status || "").toLowerCase() === selectedStatus;

        const matchesPriority =
            !selectedPriority ||
            selectedPriority === "all" ||
            String(exception.priority || "").toLowerCase() === selectedPriority;

        return matchesSearch && matchesStatus && matchesPriority;
    });

    displayExceptions(filteredExceptions);
}

/* ============================================================
   UPDATE SUMMARY STATISTICS
   ============================================================ */

function updateExceptionStatistics(exceptions) {
    const total = exceptions.length;

    const open = exceptions.filter(function (exception) {
        return String(exception.status || "").toLowerCase() === "open";
    }).length;

    const investigating = exceptions.filter(function (exception) {
        return String(exception.status || "").toLowerCase() === "investigating";
    }).length;

    const resolved = exceptions.filter(function (exception) {
        return String(exception.status || "").toLowerCase() === "resolved";
    }).length;

    const highPriority = exceptions.filter(function (exception) {
        return String(exception.priority || "").toLowerCase() === "high";
    }).length;

    setTextIfExists("totalExceptions", total);
    setTextIfExists("openExceptions", open);
    setTextIfExists("investigatingExceptions", investigating);
    setTextIfExists("resolvedExceptions", resolved);
    setTextIfExists("highPriorityExceptions", highPriority);
}

/* ============================================================
   OPEN EXCEPTION DETAILS
   ============================================================ */

function openExceptionDetails(exceptionId) {
    if (!exceptionId) {
        setExceptionMessage("Exception ID is missing.", "error");
        return;
    }

    const detailsPage = "exception-details.html";

    window.location.href =
        detailsPage + "?id=" + encodeURIComponent(exceptionId);
}

/* ============================================================
   PRIORITY AND STATUS CSS CLASSES
   ============================================================ */

function getPriorityClass(priority) {
    switch (String(priority || "").toLowerCase()) {
        case "critical":
            return "priority-critical";

        case "high":
            return "priority-high";

        case "medium":
            return "priority-medium";

        case "low":
            return "priority-low";

        default:
            return "priority-normal";
    }
}

function getStatusClass(status) {
    switch (String(status || "").toLowerCase()) {
        case "open":
            return "status-open";

        case "investigating":
            return "status-investigating";

        case "resolved":
            return "status-resolved";

        case "closed":
            return "status-closed";

        default:
            return "status-default";
    }
}

/* ============================================================
   DATE FORMATTING
   ============================================================ */

function formatExceptionDate(dateValue) {
    if (!dateValue) {
        return "—";
    }

    const date = new Date(dateValue);

    if (Number.isNaN(date.getTime())) {
        return String(dateValue);
    }

    return date.toLocaleDateString("en-IN", {
        day: "2-digit",
        month: "short",
        year: "numeric"
    });
}

/* ============================================================
   SAFE HTML OUTPUT
   ============================================================ */

function escapeExceptionHTML(value) {
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

/* ============================================================
   HELPER FUNCTIONS
   ============================================================ */

function setTextIfExists(elementId, value) {
    const element = document.getElementById(elementId);

    if (element) {
        element.textContent = value;
    }
}

function setExceptionMessage(message, type, autoHide = true) {
    let messageElement = document.getElementById("exceptionMessage");

    if (!messageElement) {
        messageElement = document.createElement("div");
        messageElement.id = "exceptionMessage";
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
            console.info(message);
            return;
        }
    }

    messageElement.textContent = message;
    messageElement.style.display = "block";

    if (type === "error") {
        messageElement.style.color = "#b91c1c";
        messageElement.style.background = "#fee2e2";
        messageElement.style.border = "1px solid #fecaca";
    } else if (type === "success") {
        messageElement.style.color = "#047857";
        messageElement.style.background = "#d1fae5";
        messageElement.style.border = "1px solid #a7f3d0";
    } else {
        messageElement.style.color = "#1d4ed8";
        messageElement.style.background = "#dbeafe";
        messageElement.style.border = "1px solid #bfdbfe";
    }

    if (messageElement.exceptionMessageTimer) {
        clearTimeout(messageElement.exceptionMessageTimer);
    }

    if (autoHide) {
        messageElement.exceptionMessageTimer = setTimeout(function () {
            messageElement.style.display = "none";
        }, 4000);
    }
}