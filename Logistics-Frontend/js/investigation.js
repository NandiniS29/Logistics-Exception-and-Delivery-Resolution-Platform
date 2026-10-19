/* =====================================================
   LOGISTICS EXCEPTION & DELIVERY RESOLUTION PLATFORM
   Investigation Management
   ===================================================== */

document.addEventListener("DOMContentLoaded", function () {
    loadInvestigations();

    const form = document.getElementById("investigationForm");

    if (form) {
        form.addEventListener("submit", saveInvestigation);
    }

    const refreshButton = document.getElementById("refreshInvestigations");

    if (refreshButton) {
        refreshButton.addEventListener("click", loadInvestigations);
    }
});

async function loadInvestigations() {
    const tableBody =
        document.querySelector("#investigationsTable tbody") ||
        document.getElementById("investigationsTableBody");

    if (!tableBody) return;

    tableBody.innerHTML =
        '<tr><td colspan="8">Loading investigations...</td></tr>';

    try {
        const response = await fetch(`${API_BASE_URL}/investigations`);

        if (!response.ok) {
            throw new Error("Unable to load investigations.");
        }

        const data = await response.json();

        if (!Array.isArray(data) || data.length === 0) {
            tableBody.innerHTML =
                '<tr><td colspan="8">No investigations found.</td></tr>';
            return;
        }

        tableBody.innerHTML = data.map(item => `
            <tr>
                <td>${item.investigationId ?? item.id ?? "-"}</td>
                <td>${item.exceptionId ?? "-"}</td>
                <td>${item.investigatorName ?? item.assignedTo ?? "-"}</td>
                <td>${item.findings ?? "-"}</td>
                <td>${item.rootCause ?? "-"}</td>
                <td>${item.status ?? "-"}</td>
                <td>${formatInvestigationDate(item.investigationDate)}</td>
                <td>
                    <button type="button"
                        onclick="viewInvestigation(${Number(item.investigationId ?? item.id)})">
                        View
                    </button>
                </td>
            </tr>
        `).join("");

    } catch (error) {
        console.error("Investigation loading error:", error);

        tableBody.innerHTML =
            '<tr><td colspan="8">Could not load investigations. Check the API endpoint.</td></tr>';
    }
}

async function saveInvestigation(event) {
    event.preventDefault();

    const form = event.target;
    const formData = new FormData(form);
    const investigation = Object.fromEntries(formData.entries());

    try {
        const response = await fetch(`${API_BASE_URL}/investigations`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(investigation)
        });

        if (!response.ok) {
            throw new Error("Unable to save investigation.");
        }

        alert("Investigation saved successfully.");
        form.reset();
        loadInvestigations();

    } catch (error) {
        console.error("Investigation saving error:", error);
        alert("Could not save the investigation. Please check your backend API.");
    }
}

async function viewInvestigation(id) {
    try {
        const response = await fetch(
            `${API_BASE_URL}/investigations/${encodeURIComponent(id)}`
        );

        if (!response.ok) {
            throw new Error("Investigation details could not be loaded.");
        }

        const data = await response.json();

        alert(JSON.stringify(data, null, 2));

    } catch (error) {
        console.error(error);
        alert("Unable to view investigation details.");
    }
}

function formatInvestigationDate(value) {
    if (!value) return "-";

    const date = new Date(value);

    return Number.isNaN(date.getTime())
        ? value
        : date.toLocaleDateString();
}