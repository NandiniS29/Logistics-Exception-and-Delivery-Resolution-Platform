/* =====================================================
   LOGISTICS PLATFORM - SHIPMENT MANAGEMENT
   ===================================================== */

document.addEventListener("DOMContentLoaded", function () {
    loadShipments();

    const form = document.getElementById("shipmentForm");

    if (form) {
        form.addEventListener("submit", saveShipment);
    }

    const refreshButton = document.getElementById("refreshShipments");

    if (refreshButton) {
        refreshButton.addEventListener("click", loadShipments);
    }

    const searchInput = document.getElementById("shipmentSearch");

    if (searchInput) {
        searchInput.addEventListener("input", function () {
            filterShipmentRows(this.value);
        });
    }
});

async function loadShipments() {
    const tableBody =
        document.querySelector("#shipmentsTable tbody") ||
        document.getElementById("shipmentsTableBody");

    if (!tableBody) return;

    tableBody.innerHTML =
        '<tr><td colspan="8">Loading shipments...</td></tr>';

    try {
        const response = await fetch(`${API_BASE_URL}/shipments`);

        if (!response.ok) {
            throw new Error("Unable to load shipments.");
        }

        const shipments = await response.json();

        if (!Array.isArray(shipments) || shipments.length === 0) {
            tableBody.innerHTML =
                '<tr><td colspan="8">No shipments found.</td></tr>';
            return;
        }

        tableBody.innerHTML = shipments.map(shipment => `
            <tr>
                <td>${shipment.shipmentId ?? shipment.id ?? "-"}</td>
                <td>${shipment.trackingNumber ?? "-"}</td>
                <td>${shipment.customerName ?? shipment.customerId ?? "-"}</td>
                <td>${shipment.origin ?? shipment.source ?? "-"}</td>
                <td>${shipment.destination ?? "-"}</td>
                <td>${shipment.status ?? "-"}</td>
                <td>${shipment.expectedDeliveryDate ?? "-"}</td>
                <td>
                    <button type="button"
                        onclick="viewShipment(${Number(shipment.shipmentId ?? shipment.id)})">
                        View
                    </button>
                </td>
            </tr>
        `).join("");

    } catch (error) {
        console.error("Shipment loading error:", error);

        tableBody.innerHTML =
            '<tr><td colspan="8">Could not load shipments. Verify the backend endpoint.</td></tr>';
    }
}

async function saveShipment(event) {
    event.preventDefault();

    const form = event.target;
    const shipmentData = Object.fromEntries(new FormData(form).entries());

    try {
        const response = await fetch(`${API_BASE_URL}/shipments`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(shipmentData)
        });

        if (!response.ok) {
            throw new Error("Unable to save shipment.");
        }

        alert("Shipment saved successfully.");
        form.reset();
        loadShipments();

    } catch (error) {
        console.error("Shipment saving error:", error);
        alert("Could not save the shipment. Verify the backend API and required fields.");
    }
}

async function viewShipment(id) {
    try {
        const response = await fetch(
            `${API_BASE_URL}/shipments/${encodeURIComponent(id)}`
        );

        if (!response.ok) {
            throw new Error("Shipment details could not be loaded.");
        }

        const shipment = await response.json();

        alert(JSON.stringify(shipment, null, 2));

    } catch (error) {
        console.error(error);
        alert("Unable to view shipment details.");
    }
}

function filterShipmentRows(searchTerm) {
    const rows = document.querySelectorAll(
        "#shipmentsTable tbody tr, #shipmentsTableBody tr"
    );

    const term = searchTerm.toLowerCase().trim();

    rows.forEach(row => {
        row.hidden = !row.textContent.toLowerCase().includes(term);
    });
}