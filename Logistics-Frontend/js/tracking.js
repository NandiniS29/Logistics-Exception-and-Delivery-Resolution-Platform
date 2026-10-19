/* =====================================================
   LOGISTICS PLATFORM - SHIPMENT TRACKING
   ===================================================== */

document.addEventListener("DOMContentLoaded", function () {
    loadTrackingRecords();

    const form = document.getElementById("trackingForm");

    if (form) {
        form.addEventListener("submit", searchTracking);
    }

    const refreshButton = document.getElementById("refreshTracking");

    if (refreshButton) {
        refreshButton.addEventListener("click", loadTrackingRecords);
    }
});

async function loadTrackingRecords() {
    const tableBody =
        document.querySelector("#trackingTable tbody") ||
        document.getElementById("trackingTableBody");

    if (!tableBody) return;

    tableBody.innerHTML =
        '<tr><td colspan="6">Loading tracking records...</td></tr>';

    try {
        const response = await fetch(`${API_BASE_URL}/tracking`);

        if (!response.ok) {
            throw new Error("Unable to load tracking records.");
        }

        const records = await response.json();

        if (!Array.isArray(records) || records.length === 0) {
            tableBody.innerHTML =
                '<tr><td colspan="6">No tracking records found.</td></tr>';
            return;
        }

        tableBody.innerHTML = records.map(record => `
            <tr>
                <td>${record.trackingId ?? record.id ?? "-"}</td>
                <td>${record.shipmentId ?? "-"}</td>
                <td>${record.location ?? "-"}</td>
                <td>${record.status ?? "-"}</td>
                <td>${record.description ?? record.notes ?? "-"}</td>
                <td>${formatTrackingDate(
                    record.timestamp ?? record.trackingDate ?? record.createdAt
                )}</td>
            </tr>
        `).join("");

    } catch (error) {
        console.error("Tracking loading error:", error);

        tableBody.innerHTML =
            '<tr><td colspan="6">Could not load tracking records. Verify the backend endpoint.</td></tr>';
    }
}

async function searchTracking(event) {
    event.preventDefault();

    const form = event.target;

    const trackingInput =
        form.querySelector('[name="trackingNumber"]') ||
        form.querySelector('[name="shipmentId"]') ||
        document.getElementById("trackingNumber");

    if (!trackingInput || !trackingInput.value.trim()) {
        alert("Enter a shipment ID or tracking number.");
        return;
    }

    const value = trackingInput.value.trim();

    try {
        const response = await fetch(
            `${API_BASE_URL}/tracking/${encodeURIComponent(value)}`
        );

        if (!response.ok) {
            throw new Error("Tracking information was not found.");
        }

        const result = await response.json();

        displayTrackingResult(result);

    } catch (error) {
        console.error("Tracking search error:", error);
        alert("Tracking information could not be retrieved. Check the endpoint and tracking number.");
    }
}

function displayTrackingResult(result) {
    const output =
        document.getElementById("trackingResult") ||
        document.getElementById("trackingDetails");

    if (!output) {
        alert(JSON.stringify(result, null, 2));
        return;
    }

    output.hidden = false;

    output.textContent = JSON.stringify(result, null, 2);
}

function formatTrackingDate(value) {
    if (!value) return "-";

    const date = new Date(value);

    return Number.isNaN(date.getTime())
        ? value
        : date.toLocaleString();
}